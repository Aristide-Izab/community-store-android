package com.example.communitystore.auth;

import com.example.communitystore.R;
import com.example.communitystore.MainActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY =
            800;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_splash
        );

        auth =
                FirebaseAuth.getInstance();

        db =
                FirebaseFirestore.getInstance();

        new Handler(
                Looper.getMainLooper()
        )
                .postDelayed(
                        this::checkUser,
                        SPLASH_DELAY
                );
    }

    private void checkUser() {

        FirebaseUser user =
                auth.getCurrentUser();

        if (user == null) {

            openWelcome();
            return;
        }

        user.reload()
                .addOnCompleteListener(task -> {

                    FirebaseUser refreshed =
                            auth.getCurrentUser();

                    if (refreshed == null) {

                        openWelcome();
                        return;
                    }

                    /*
                     * Never automatically send
                     * unverified users to a
                     * verification screen.
                     */

                    if (!refreshed
                            .isEmailVerified()) {

                        auth.signOut();

                        openWelcome();
                        return;
                    }

                    loadUserProfile(
                            refreshed
                    );
                });
    }

    private void loadUserProfile(
            FirebaseUser user
    ) {

        db.collection("users")
                .document(
                        user.getUid()
                )
                .get()

                .addOnSuccessListener(document -> {

                    if (!document.exists()) {

                        auth.signOut();
                        openWelcome();

                        return;
                    }

                    Boolean verified =
                            document.getBoolean(
                                    "verified"
                            );

                    String role =
                            document.getString(
                                    "role"
                            );

                    if (verified == null
                            || !verified
                            || !isValidRole(role)) {

                        auth.signOut();

                        openWelcome();
                        return;
                    }

                    saveAccountType(
                            role
                    );

                    openMain(
                            role
                    );
                })

                .addOnFailureListener(e -> {

                    auth.signOut();
                    openWelcome();
                });
    }

    private boolean isValidRole(
            String role
    ) {

        return "student".equals(role)
                || "faculty".equals(role)
                || "vendor".equals(role)
                || "residence".equals(role);
    }

    private void saveAccountType(
            String role
    ) {

        getSharedPreferences(
                "CommunityStorePrefs",
                MODE_PRIVATE
        )
                .edit()

                .putString(
                        "accountType",
                        role
                )

                .apply();
    }

    private void openWelcome() {

        Intent intent =
                new Intent(
                        SplashActivity.this,
                        WelcomeActivity.class
                );

        startActivity(intent);
        finish();
    }

    private void openMain(
            String role
    ) {

        Intent intent =
                new Intent(
                        SplashActivity.this,
                        MainActivity.class
                );

        intent.putExtra(
                "accountType",
                role
        );

        intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);
        finish();
    }
}
