package com.example.communitystore.shared.data;

import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Reads and writes "products".
 *
 * Queries only use a single equality filter and are sorted on the phone,
 * so no composite Firestore indexes are needed.
 */
public final class ProductRepository {

    public interface ListCallback {
        void onLoaded(List<Product> products, @Nullable Exception error);
    }

    private ProductRepository() {
    }

    private static FirebaseFirestore db() {
        return FirebaseFirestore.getInstance();
    }

    /** New document reference (id available before saving). */
    public static DocumentReference newProduct() {
        return db().collection("products").document();
    }

    public static Task<Void> create(Product product) {
        DocumentReference ref = product.id == null || product.id.isEmpty()
                ? newProduct()
                : db().collection("products").document(product.id);
        product.id = ref.getId();
        return ref.set(product.toMap(true));
    }

    public static Task<Void> update(Product product) {
        Map<String, Object> data = product.toMap(false);
        return db().collection("products").document(product.id).update(data);
    }

    public static Task<Void> setStatus(String productId, String status) {
        Map<String, Object> data = new HashMap<>();
        data.put("status", status);
        data.put("updatedAt", FieldValue.serverTimestamp());
        return db().collection("products").document(productId).update(data);
    }

    public static Task<Void> delete(String productId) {
        return db().collection("products").document(productId).delete();
    }

    public static Task<DocumentSnapshot> get(String productId) {
        return db().collection("products").document(productId).get();
    }

    /** All active listings, newest first. */
    public static void loadActive(ListCallback callback) {
        run(db().collection("products").whereEqualTo("status", Product.STATUS_ACTIVE), callback, true);
    }

    /** Every listing (any status) of one seller, newest first. */
    public static void loadBySeller(String sellerId, ListCallback callback) {
        run(db().collection("products").whereEqualTo("sellerId", sellerId), callback, false);
    }

    /** Active listings of one seller. */
    public static void loadActiveBySeller(String sellerId, ListCallback callback) {
        run(db().collection("products").whereEqualTo("sellerId", sellerId), (products, error) -> {
            List<Product> active = new ArrayList<>();
            for (Product p : products) {
                if (p.isActive()) {
                    active.add(p);
                }
            }
            callback.onLoaded(active, error);
        }, false);
    }

    /** Active Firestore listings followed by the built-in samples. */
    public static void loadMarketplace(ListCallback callback) {
        loadActive((products, error) -> {
            List<Product> all = new ArrayList<>(products);
            all.addAll(SampleProducts.all());
            callback.onLoaded(all, error);
        });
    }

    public static Task<Void> incrementPopularity(String productId) {
        return db().collection("products").document(productId)
                .update("popularity", FieldValue.increment(1));
    }

    private static void run(Query query, ListCallback callback, boolean activeOnly) {
        query.get()
                .addOnSuccessListener(snapshot -> callback.onLoaded(toList(snapshot, activeOnly), null))
                .addOnFailureListener(e -> callback.onLoaded(new ArrayList<>(), e));
    }

    private static List<Product> toList(QuerySnapshot snapshot, boolean activeOnly) {
        List<Product> products = new ArrayList<>();

        for (DocumentSnapshot document : snapshot.getDocuments()) {
            Product product = Product.fromDocument(document);
            if (!activeOnly || product.isActive()) {
                products.add(product);
            }
        }

        Collections.sort(products, (a, b) -> Long.compare(b.createdAt, a.createdAt));
        return products;
    }
}
