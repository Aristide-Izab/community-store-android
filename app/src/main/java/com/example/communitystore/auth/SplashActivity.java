package com.example.communitystore.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.communitystore.R;
import com.example.communitystore.core.access.AccessManager;
import com.example.communitystore.core.access.AccountRouter;
import com.example.communitystore.core.session.AuthSession;
import com.example.communitystore.core.session.SessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Launcher screen. Restores a signed-in session and sends the user to:
 * their dashboard, or the registration step they stopped at, or Welcome.
 */
public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 800;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_splash);

        auth = FirebaseAuth.getInstance();

        new Handler(Looper.getMainLooper()).postDelayed(this::checkUser, SPLASH_DELAY);
    }

    private void checkUser() {

        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {
            new SessionManager(this).clearSession();
            openWelcome();
            return;
        }

        user.reload().addOnCompleteListener(task -> {

            FirebaseUser refreshed = auth.getCurrentUser();

            if (refreshed == null) {
                AuthSession.signOut(this);
                openWelcome();
                return;
            }

            AccountRouter.resolve(refreshed, (destination, role, profile) -> {

                switch (destination) {

                    case HOME:
                        AccountRouter.cacheProfile(this, role, profile);
                        AccessManager.openHome(SplashActivity.this, role);
                        break;

                    case VERIFY_EMAIL:
                        AccountRouter.cacheProfile(this, role, profile);
                        AccountRouter.openVerifyEmail(this, role);
                        break;

                    case DOCUMENTS:
                        AccountRouter.cacheProfile(this, role, profile);
                        AccountRouter.openDocuments(this, role);
                        break;

                    default:
                        // Pending review, rejected, missing profile or offline:
                        // Login explains what is going on.
                        AuthSession.signOut(this);
                        openWelcome();
                        break;
                }
            });
        });
    }

    private void openWelcome() {

        Intent intent = new Intent(SplashActivity.this, WelcomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
