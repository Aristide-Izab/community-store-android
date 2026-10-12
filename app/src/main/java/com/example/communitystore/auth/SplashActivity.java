package com.example.communitystore.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.communitystore.R;
import com.example.communitystore.core.access.AccessManager;
import com.example.communitystore.core.access.UserRole;
import com.example.communitystore.core.session.SessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 800;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_auth_splash
        );

        auth =
                FirebaseAuth.getInstance();

        db =
                FirebaseFirestore.getInstance();


        sessionManager = new SessionManager(this);

        new Handler(Looper.getMainLooper()).postDelayed(
                this::checkUser,
                SPLASH_DELAY
        );
    }

    private void checkUser() {

        FirebaseUser user =
                auth.getCurrentUser();

        if (user == null) {

            sessionManager.clearSession();

            openWelcome();

            return;
        }

        user.reload()
                .addOnCompleteListener(task -> {

                    FirebaseUser refreshed =
                            auth.getCurrentUser();

                    if (refreshed == null) {

                        sessionManager.clearSession();

                        openWelcome();

                        return;
                    }

                    /*
                     * Student and Faculty accounts
                     * require Firebase email verification.
                     *
                     * Vendor and Residence accounts may
                     * use a different approval process,
                     * so their access is validated using
                     * the Firestore profile below.
                     */

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

                        sessionManager.clearSession();

                        openWelcome();

                        return;
                    }

                    Boolean verified =
                            document.getBoolean(
                                    "verified"
                            );

                    String roleString =
                            document.getString(
                                    "role"
                            );

                    UserRole role =
                            UserRole.fromString(
                                    roleString
                            );

                    if (verified == null
                            || !verified
                            || role == null) {

                        auth.signOut();

                        sessionManager.clearSession();

                        openWelcome();

                        return;
                    }

                    if (requiresEmailVerification(role)
                            && !user.isEmailVerified()) {

                        auth.signOut();

                        sessionManager.clearSession();

                        openWelcome();

                        return;
                    }

                    sessionManager.saveUserRole(
                            role
                    );

                    AccessManager.openHome(
                            SplashActivity.this,
                            role
                    );
                })

                .addOnFailureListener(e -> {

                    auth.signOut();

                    sessionManager.clearSession();

                    openWelcome();
                });
    }

    private boolean requiresEmailVerification(
            UserRole role
    ) {

        return role == UserRole.STUDENT
                || role == UserRole.FACULTY;
    }

    private void openWelcome() {

        Intent intent =
                new Intent(
                        SplashActivity.this,
                        WelcomeActivity.class
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
