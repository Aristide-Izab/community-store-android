package com.example.communitystore.shared.data;

import android.content.Context;

import androidx.annotation.Nullable;

import com.example.communitystore.core.session.AuthSession;
import com.example.communitystore.core.session.SessionManager;
import com.example.communitystore.shared.cart.CartItem;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Creates and updates "orders" and "reviews".
 *
 * Points: buyers earn points when they place an order, sellers earn points
 * when they mark an order as delivered. Each user only updates their own
 * points, which keeps the security rules simple.
 */
public final class OrderRepository {

    public interface ListCallback {
        void onLoaded(List<Order> orders, @Nullable Exception error);
    }

    private OrderRepository() {
    }

    private static FirebaseFirestore db() {
        return FirebaseFirestore.getInstance();
    }

    public static long buyerPointsFor(long total) {
        return Math.max(5, total / 10);
    }

    public static long sellerPointsFor(long total) {
        return 20 + total / 20;
    }

    /**
     * Turns the cart into orders (one per seller) in a single batch.
     * The delivery fee and discount are applied to the first order.
     *
     * @return ids of the created orders, via the task result
     */
    public static Task<List<String>> placeOrders(
            Context context,
            List<CartItem> cart,
            long delivery,
            long discount,
            String paymentMethod,
            String deliveryAddress
    ) {

        String buyerId = AuthSession.uid();
        String buyerName = new SessionManager(context).getDisplayName();

        Map<String, List<CartItem>> bySeller = new LinkedHashMap<>();
        for (CartItem item : cart) {
            String seller = item.getSellerId().isEmpty() ? SampleProducts.SAMPLE_SELLER_ID : item.getSellerId();
            List<CartItem> group = bySeller.get(seller);
            if (group == null) {
                group = new ArrayList<>();
                bySeller.put(seller, group);
            }
            group.add(item);
        }

        WriteBatch batch = db().batch();
        List<String> ids = new ArrayList<>();
        List<CartItem> popular = new ArrayList<>();
        long grandTotal = 0;
        boolean first = true;

        for (Map.Entry<String, List<CartItem>> entry : bySeller.entrySet()) {
            List<CartItem> group = entry.getValue();
            CartItem lead = group.get(0);

            List<Map<String, Object>> items = new ArrayList<>();
            long subtotal = 0;

            for (CartItem item : group) {
                Map<String, Object> line = new HashMap<>();
                line.put("productId", item.getProductId());
                line.put("title", item.getTitle());
                line.put("unitPrice", item.getUnitPrice());
                line.put("quantity", item.getQuantity());
                line.put("category", item.getCategory());
                items.add(line);
                subtotal += item.getLineTotal();

                if (!item.getProductId().isEmpty() && !item.getProductId().startsWith("sample_")) {
                    popular.add(item);
                }
            }

            long orderDelivery = first ? delivery : 0;
            long orderDiscount = first ? Math.min(discount, subtotal + orderDelivery) : 0;
            long total = subtotal + orderDelivery - orderDiscount;
            grandTotal += total;
            first = false;

            DocumentReference ref = db().collection("orders").document();
            ids.add(ref.getId());

            Map<String, Object> order = new HashMap<>();
            order.put("buyerId", buyerId);
            order.put("buyerName", buyerName.isEmpty() ? "Customer" : buyerName);
            order.put("sellerId", entry.getKey());
            order.put("sellerName", lead.getSellerName().isEmpty() ? SampleProducts.SAMPLE_SELLER_NAME : lead.getSellerName());
            order.put("sellerRole", lead.getSellerRole());
            order.put("participants", Arrays.asList(buyerId, entry.getKey()));
            order.put("items", items);
            order.put("subtotal", subtotal);
            order.put("delivery", orderDelivery);
            order.put("discount", orderDiscount);
            order.put("total", total);
            order.put("paymentMethod", paymentMethod);
            order.put("paymentSimulated", true);
            order.put("deliveryAddress", deliveryAddress);
            order.put("status", Order.STATUS_PLACED);
            order.put("reviewed", false);
            order.put("createdAt", FieldValue.serverTimestamp());
            order.put("updatedAt", FieldValue.serverTimestamp());

            batch.set(ref, order);
        }

        if (buyerId != null) {
            long earned = buyerPointsFor(grandTotal);
            batch.update(
                    db().collection("users").document(buyerId),
                    "points", FieldValue.increment(earned),
                    "lifetimePoints", FieldValue.increment(earned)
            );
        }

        return batch.commit().continueWith(task -> {
            if (!task.isSuccessful() && task.getException() != null) {
                throw task.getException();
            }

            // Best effort, outside the batch: a deleted listing must not block checkout.
            for (CartItem item : popular) {
                db().collection("products")
                        .document(item.getProductId())
                        .update("popularity", FieldValue.increment(item.getQuantity()));
            }

            return ids;
        });
    }

    public static Task<DocumentSnapshot> get(String orderId) {
        return db().collection("orders").document(orderId).get();
    }

    /** Orders the signed-in user bought, newest first. */
    public static void loadAsBuyer(String uid, ListCallback callback) {
        load("buyerId", uid, callback);
    }

    /** Orders the signed-in user sold, newest first. */
    public static void loadAsSeller(String uid, ListCallback callback) {
        load("sellerId", uid, callback);
    }

    private static void load(String field, String uid, ListCallback callback) {
        if (uid == null) {
            callback.onLoaded(new ArrayList<>(), null);
            return;
        }

        db().collection("orders")
                .whereEqualTo(field, uid)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<Order> orders = new ArrayList<>();
                    for (DocumentSnapshot document : snapshot.getDocuments()) {
                        orders.add(Order.fromDocument(document));
                    }
                    Collections.sort(orders, (a, b) -> Long.compare(b.createdAt, a.createdAt));
                    callback.onLoaded(orders, null);
                })
                .addOnFailureListener(e -> callback.onLoaded(new ArrayList<>(), e));
    }

    /**
     * Seller moves the order to the next status. Delivered orders earn the seller points.
     * Orders from sample listings have no real seller, so the buyer can step them
     * through the statuses for demos.
     */
    public static Task<Void> updateStatus(Order order, String newStatus) {
        WriteBatch batch = db().batch();

        Map<String, Object> update = new HashMap<>();
        update.put("status", newStatus);
        update.put("updatedAt", FieldValue.serverTimestamp());
        batch.update(db().collection("orders").document(order.id), update);

        String uid = AuthSession.uid();
        if (Order.STATUS_DELIVERED.equals(newStatus) && uid != null && uid.equals(order.sellerId)) {
            long earned = sellerPointsFor(order.total);
            batch.update(
                    db().collection("users").document(uid),
                    "points", FieldValue.increment(earned),
                    "lifetimePoints", FieldValue.increment(earned)
            );
        }

        return batch.commit();
    }

    /** Buyer cancels an order that has not been packed yet. */
    public static Task<Void> cancel(Order order) {
        Map<String, Object> update = new HashMap<>();
        update.put("status", Order.STATUS_CANCELLED);
        update.put("updatedAt", FieldValue.serverTimestamp());
        return db().collection("orders").document(order.id).update(update);
    }

    /**
     * Saves a review (one per order, id = order id) and marks the order reviewed.
     * Seller ratings are worked out from the "reviews" collection.
     */
    public static Task<Void> submitReview(
            String orderId,
            String sellerId,
            String productTitle,
            int rating,
            String comment,
            String buyerName
    ) {
        WriteBatch batch = db().batch();

        Map<String, Object> review = new HashMap<>();
        review.put("orderId", orderId);
        review.put("sellerId", sellerId);
        review.put("buyerId", AuthSession.uid());
        review.put("buyerName", buyerName);
        review.put("productTitle", productTitle);
        review.put("rating", rating);
        review.put("comment", comment);
        review.put("createdAt", FieldValue.serverTimestamp());

        batch.set(db().collection("reviews").document(orderId), review);

        Map<String, Object> update = new HashMap<>();
        update.put("reviewed", true);
        batch.set(db().collection("orders").document(orderId), update, SetOptions.merge());

        return batch.commit();
    }

    /** Total of delivered orders. */
    public static long revenue(List<Order> orders) {
        long total = 0;
        for (Order order : orders) {
            if (Order.STATUS_DELIVERED.equals(order.status)) {
                total += order.total;
            }
        }
        return total;
    }

    /** Total of orders that are not cancelled (delivered + in progress). */
    public static long sales(List<Order> orders) {
        long total = 0;
        for (Order order : orders) {
            if (!Order.STATUS_CANCELLED.equals(order.status)) {
                total += order.total;
            }
        }
        return total;
    }
}
