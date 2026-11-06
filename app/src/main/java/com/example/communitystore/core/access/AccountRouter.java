package com.example.communitystore.core.access;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.example.communitystore.auth.ResidenceVerificationActivity;
import com.example.communitystore.auth.VendorVerificationActivity;
import com.example.communitystore.auth.VerifyEmailActivity;
import com.example.communitystore.core.session.AccountStatus;
import com.example.communitystore.core.session.SessionManager;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * Decides where a signed-in user should go, based on users/{uid}:
 * their role and verificationStatus.
 *
 * Used by Splash, Login and the email verification screen so the rules
 * live in one place.
 */
public final class AccountRouter {

    public enum Destination {
        HOME,
        VERIFY_EMAIL,
        DOCUMENTS,
        PENDING_REVIEW,
        REJECTED,
        NO_PROFILE,
        ERROR
    }

    public interface Callback {
        void onResolved(
                Destination destination,
                @Nullable UserRole role,
                @Nullable DocumentSnapshot profile
        );
    }

    private AccountRouter() {
    }

    /** Loads users/{uid} and works out the destination. */
    public static void resolve(FirebaseUser user, Callback callback) {

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(document -> evaluate(user, document, callback))
                .addOnFailureListener(e -> callback.onResolved(Destination.ERROR, null, null));
    }

    private static void evaluate(
            FirebaseUser user,
            DocumentSnapshot document,
            Callback callback
    ) {

        if (document == null || !document.exists()) {
            callback.onResolved(Destination.NO_PROFILE, null, document);
            return;
        }

        UserRole role = UserRole.fromString(document.getString("role"));

        if (role == null) {
            callback.onResolved(Destination.NO_PROFILE, null, document);
            return;
        }

        String status = document.getString("verificationStatus");
        boolean verified = Boolean.TRUE.equals(document.getBoolean("verified"));

        if (AccountStatus.EMAIL_PENDING.equals(status)) {

            if (!user.isEmailVerified()) {
                callback.onResolved(Destination.VERIFY_EMAIL, role, document);
                return;
            }

            // Email has been confirmed since the profile was created: move on.
            advanceAfterEmailVerified(user.getUid(), role, () -> callback.onResolved(
                    role.requiresDocuments() ? Destination.DOCUMENTS : Destination.HOME,
                    role,
                    document
            ));
            return;
        }

        if (AccountStatus.DOCUMENTS_PENDING.equals(status)) {
            callback.onResolved(Destination.DOCUMENTS, role, document);
            return;
        }

        if (AccountStatus.PENDING_REVIEW.equals(status)) {
            callback.onResolved(Destination.PENDING_REVIEW, role, document);
            return;
        }

        if (AccountStatus.REJECTED.equals(status)) {
            callback.onResolved(Destination.REJECTED, role, document);
            return;
        }

        if (AccountStatus.isActive(status) || (status == null && verified)) {

            if (!role.requiresDocuments() && !user.isEmailVerified()) {
                callback.onResolved(Destination.VERIFY_EMAIL, role, document);
                return;
            }

            callback.onResolved(Destination.HOME, role, document);
            return;
        }

        callback.onResolved(Destination.PENDING_REVIEW, role, document);
    }

    /**
     * Called once the email address is confirmed.
     * Student/Faculty become active; Vendor/Residence move to the document step.
     */
    public static void advanceAfterEmailVerified(
            String uid,
            UserRole role,
            Runnable onDone
    ) {

        Map<String, Object> update = new HashMap<>();
        update.put("emailVerifiedAt", FieldValue.serverTimestamp());

        if (role.requiresDocuments()) {
            update.put("verificationStatus", AccountStatus.DOCUMENTS_PENDING);
            update.put("verified", false);
        } else {
            update.put("verificationStatus", AccountStatus.ACTIVE);
            update.put("verified", true);
            update.put("verifiedAt", FieldValue.serverTimestamp());
        }

        // The security rules check the "email_verified" claim of the ID token,
        // which only updates when the token is refreshed, so force a refresh first.
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Task<?> refresh = user == null
                ? Tasks.forResult(null)
                : user.getIdToken(true);

        refresh.continueWithTask(task -> FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(uid)
                        .update(update))
                .addOnCompleteListener(task -> onDone.run());
    }

    /** Saves role and name locally so screens can theme straight away. */
    public static void cacheProfile(
            Context context,
            @Nullable UserRole role,
            @Nullable DocumentSnapshot profile
    ) {

        SessionManager session = new SessionManager(context);
        session.saveUserRole(role);

        if (profile != null) {
            // Vendors are shown by their business name everywhere (listings, chats, orders).
            session.saveDisplayName(
                    com.example.communitystore.shared.data.UserDirectory.displayName(profile, "")
            );
        }
    }

    /** Opens the email verification screen for an account that is mid-registration. */
    public static void openVerifyEmail(Activity activity, UserRole role) {
        Intent intent = new Intent(activity, VerifyEmailActivity.class);
        intent.putExtra("accountType", role == null ? null : role.getValue());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
        activity.finish();
    }

    /** Opens the Vendor or Residence document step. */
    public static void openDocuments(Activity activity, UserRole role) {
        Class<?> target = role == UserRole.RESIDENCE
                ? ResidenceVerificationActivity.class
                : VendorVerificationActivity.class;

        Intent intent = new Intent(activity, target);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
        activity.finish();
    }

    /** Friendly message for accounts that cannot sign in yet. */
    public static String blockedMessage(Destination destination, @Nullable UserRole role) {
        String type = role == UserRole.RESIDENCE ? "residence administrator" : "vendor";

        if (destination == Destination.REJECTED) {
            return "Your " + type + " application was not approved. "
                    + "Please contact Community Store support.";
        }

        return "Your " + type + " application is still under review. "
                + "You'll be able to log in once it is approved.";
    }
}
