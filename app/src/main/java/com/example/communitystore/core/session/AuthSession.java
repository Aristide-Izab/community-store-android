package com.example.communitystore.core.session;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.example.communitystore.auth.WelcomeActivity;
import com.example.communitystore.shared.cart.CartManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/** Shared sign-out logic so every account type logs out the same way. */
public final class AuthSession {

    private AuthSession() {
    }

    /** Signs out, clears the cached role/name and the cart. */
    public static void signOut(Context context) {
        FirebaseAuth.getInstance().signOut();
        new SessionManager(context).clearSession();
        new CartManager(context).clear();
    }

    /** Signs out and returns to the Welcome screen with a fresh back stack. */
    public static void logoutToWelcome(Activity activity) {
        signOut(activity);

        Intent intent = new Intent(activity, WelcomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
        activity.finish();
    }

    /** UID of the signed-in user, or null. */
    public static String uid() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user == null ? null : user.getUid();
    }
}
