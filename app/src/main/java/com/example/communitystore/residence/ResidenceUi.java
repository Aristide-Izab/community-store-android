package com.example.communitystore.residence;

import com.example.communitystore.R;

import android.graphics.Color;
import android.view.View;

import androidx.activity.ComponentActivity;
import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

/**
 * Shared helpers for the Residence account screens.
 */
public final class ResidenceUi {

    public interface ProfileCallback {
        void onLoaded(@Nullable DocumentSnapshot profile);
    }

    private ResidenceUi() {
    }

    /**
     * Draws the purple header behind the status bar and keeps
     * content clear of the system bars and keyboard.
     */
    public static void applyEdgeToEdge(
            ComponentActivity activity,
            View root,
            @Nullable View bottomBar
    ) {

        EdgeToEdge.enable(
                activity,
                SystemBarStyle.dark(Color.TRANSPARENT),
                SystemBarStyle.light(
                        Color.TRANSPARENT,
                        Color.TRANSPARENT
                )
        );

        View bottomTarget =
                bottomBar != null
                        ? bottomBar
                        : root;

        ViewCompat.setOnApplyWindowInsetsListener(root, (view, windowInsets) -> {

            Insets bars =
                    windowInsets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );

            Insets ime =
                    windowInsets.getInsets(
                            WindowInsetsCompat.Type.ime()
                    );

            root.setPadding(
                    bars.left,
                    bars.top,
                    bars.right,
                    bottomBar == null
                            ? Math.max(bars.bottom, ime.bottom)
                            : 0
            );

            if (bottomBar != null) {

                bottomTarget.setPadding(
                        0,
                        0,
                        0,
                        bars.bottom
                );
            }

            return WindowInsetsCompat.CONSUMED;
        });
    }

    public static String greeting() {

        int hour =
                Calendar.getInstance()
                        .get(Calendar.HOUR_OF_DAY);

        if (hour < 12) {
            return "Good morning";
        }

        if (hour < 18) {
            return "Good afternoon";
        }

        return "Good evening";
    }

    /**
     * Returns the first name from a display name, or "Resident" when unknown.
     */
    public static String firstName(
            @Nullable String displayName
    ) {

        if (displayName == null
                || displayName.trim().isEmpty()) {

            return "Resident";
        }

        return displayName.trim().split("\\s+")[0];
    }

    /**
     * Loads users/{uid} for the signed-in user. Passes null when signed out or on failure.
     */
    public static void loadProfile(
            ProfileCallback callback
    ) {

        FirebaseUser user =
                FirebaseAuth.getInstance()
                        .getCurrentUser();

        if (user == null) {

            callback.onLoaded(null);
            return;
        }

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(callback::onLoaded)
                .addOnFailureListener(e ->
                        callback.onLoaded(null)
                );
    }
}
