package com.example.communitystore.shared.data;

import androidx.annotation.Nullable;

import com.example.communitystore.core.session.AuthSession;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Reads users/{uid} profiles, points and seller ratings. */
public final class UserDirectory {

    public interface ProfileCallback {
        void onLoaded(@Nullable DocumentSnapshot profile);
    }

    public interface RatingCallback {
        void onLoaded(double average, int count);
    }

    public interface RankingCallback {
        void onLoaded(List<DocumentSnapshot> users, @Nullable Exception error);
    }

    private UserDirectory() {
    }

    public static void load(String uid, ProfileCallback callback) {
        if (uid == null || uid.isEmpty()) {
            callback.onLoaded(null);
            return;
        }

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(callback::onLoaded)
                .addOnFailureListener(e -> callback.onLoaded(null));
    }

    public static void loadCurrent(ProfileCallback callback) {
        load(AuthSession.uid(), callback);
    }

    /** Name to show for a profile: business / residence name for sellers, else display name. */
    public static String displayName(@Nullable DocumentSnapshot profile, String fallback) {
        if (profile == null) {
            return fallback;
        }

        String business = profile.getString("businessName");
        if (business != null && !business.trim().isEmpty()) {
            return business.trim();
        }

        String name = profile.getString("displayName");
        if (name != null && !name.trim().isEmpty()) {
            return name.trim();
        }

        return fallback;
    }

    public static long points(@Nullable DocumentSnapshot profile) {
        return profile == null ? 0 : Product.number(profile.get("points"), 0);
    }

    /**
     * Adds points to the signed-in user's own profile.
     * Users only ever change their own points (see firestore.rules).
     */
    public static void addPointsToCurrentUser(long points) {
        String uid = AuthSession.uid();
        if (uid == null || points <= 0) {
            return;
        }

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .update("points", FieldValue.increment(points));
    }

    /** Average review rating of a seller (from "reviews"). */
    public static void sellerRating(String sellerId, RatingCallback callback) {
        if (sellerId == null || sellerId.isEmpty() || SampleProducts.SAMPLE_SELLER_ID.equals(sellerId)) {
            callback.onLoaded(0, 0);
            return;
        }

        FirebaseFirestore.getInstance()
                .collection("reviews")
                .whereEqualTo("sellerId", sellerId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    double total = 0;
                    int count = 0;

                    for (DocumentSnapshot review : snapshot.getDocuments()) {
                        total += Product.number(review.get("rating"), 0);
                        count++;
                    }

                    callback.onLoaded(count == 0 ? 0 : total / count, count);
                })
                .addOnFailureListener(e -> callback.onLoaded(0, 0));
    }

    /** Users of one role, highest points first. */
    public static void ranking(String role, RankingCallback callback) {
        FirebaseFirestore.getInstance()
                .collection("users")
                .whereEqualTo("role", role)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<DocumentSnapshot> users = new ArrayList<>(snapshot.getDocuments());
                    Collections.sort(users, (a, b) -> Long.compare(points(b), points(a)));
                    callback.onLoaded(users, null);
                })
                .addOnFailureListener(e -> callback.onLoaded(new ArrayList<>(), e));
    }

    public static String ratingText(double average, int count) {
        if (count == 0) {
            return "No reviews yet";
        }
        return String.format(java.util.Locale.US, "%.1f (%d review%s)", average, count, count == 1 ? "" : "s");
    }
}
