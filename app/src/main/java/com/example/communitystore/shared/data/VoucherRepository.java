package com.example.communitystore.shared.data;

import androidx.annotation.Nullable;

import com.example.communitystore.core.session.AuthSession;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Reward vouchers bought with points, stored in users/{uid}/vouchers/{code}.
 * "SAVE20" is a built-in demo voucher anyone can use.
 */
public final class VoucherRepository {

    public static final String DEMO_CODE = "SAVE20";
    public static final long DEMO_AMOUNT = 20;

    public static class Voucher {
        public String code = "";
        public String label = "";
        public long amount = 0;
    }

    public interface VouchersCallback {
        void onLoaded(Map<String, Voucher> vouchersByCode);
    }

    private VoucherRepository() {
    }

    private static FirebaseFirestore db() {
        return FirebaseFirestore.getInstance();
    }

    @Nullable
    private static DocumentReference user() {
        String uid = AuthSession.uid();
        return uid == null ? null : db().collection("users").document(uid);
    }

    /** Unused vouchers of the signed-in user, keyed by upper-case code. */
    public static void loadUnused(VouchersCallback callback) {
        DocumentReference user = user();
        if (user == null) {
            callback.onLoaded(new HashMap<>());
            return;
        }

        user.collection("vouchers")
                .whereEqualTo("used", false)
                .get()
                .addOnSuccessListener(snapshot -> {
                    Map<String, Voucher> map = new HashMap<>();
                    for (DocumentSnapshot document : snapshot.getDocuments()) {
                        Voucher voucher = new Voucher();
                        voucher.code = document.getId();
                        voucher.label = document.getString("label") == null ? "" : document.getString("label");
                        voucher.amount = Product.number(document.get("amount"), 0);
                        map.put(voucher.code.toUpperCase(Locale.ROOT), voucher);
                    }
                    callback.onLoaded(map);
                })
                .addOnFailureListener(e -> callback.onLoaded(new HashMap<>()));
    }

    /** Spends points and creates a voucher. The task result is the voucher code. */
    public static Task<String> redeem(String label, long amount, long pointsCost) {
        DocumentReference user = user();
        String code = "CS-" + randomCode();

        Map<String, Object> voucher = new HashMap<>();
        voucher.put("label", label);
        voucher.put("amount", amount);
        voucher.put("pointsCost", pointsCost);
        voucher.put("used", false);
        voucher.put("createdAt", FieldValue.serverTimestamp());

        WriteBatch batch = db().batch();
        batch.set(user.collection("vouchers").document(code), voucher);
        batch.update(user, "points", FieldValue.increment(-pointsCost));

        return batch.commit().continueWith(task -> {
            if (!task.isSuccessful() && task.getException() != null) {
                throw task.getException();
            }
            return code;
        });
    }

    public static void markUsed(String code) {
        DocumentReference user = user();
        if (user == null || code == null || DEMO_CODE.equalsIgnoreCase(code)) {
            return;
        }

        Map<String, Object> update = new HashMap<>();
        update.put("used", true);
        update.put("usedAt", FieldValue.serverTimestamp());
        user.collection("vouchers").document(code).update(update);
    }

    private static String randomCode() {
        String letters = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(letters.charAt(random.nextInt(letters.length())));
        }
        return code.toString();
    }
}
