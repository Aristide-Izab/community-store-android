package com.example.communitystore.auth;

import com.example.communitystore.R;
import com.example.communitystore.core.access.AccountRouter;
import com.example.communitystore.core.access.UserRole;
import com.example.communitystore.core.session.AuthSession;
import com.example.communitystore.core.session.SessionManager;
import com.example.communitystore.shared.utils.ThemeUtils;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Email verification for every account type (step 2 of registration).
 *
 * Also receives the Firebase App Link for verification and password reset
 * emails (see the intent filter in AndroidManifest.xml).
 *
 * Once the address is confirmed:
 *   Student / Faculty  -> account becomes active -> Account Verified -> Login
 *   Vendor / Residence -> document verification screen
 */
public class VerifyEmailActivity extends AppCompatActivity {

    private TextView backButton;
    private TextView resendText;
    private TextView statusText;
    private TextView verifyTitle;
    private TextView verifyIcon;
    private ProgressBar progressBar;

    private FirebaseAuth auth;

    private boolean handlingLink = false;
    private boolean completed = false;

    private UserRole role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_verify_student);

        auth = FirebaseAuth.getInstance();

        backButton = findViewById(R.id.backButton);
        resendText = findViewById(R.id.resendText);
        statusText = findViewById(R.id.statusText);
        verifyTitle = findViewById(R.id.verifyTitle);
        verifyIcon = findViewById(R.id.verifyIcon);
        progressBar = findViewById(R.id.progressBar);

        role = UserRole.fromString(getIntent().getStringExtra("accountType"));

        if (role == null) {
            role = new SessionManager(this).getUserRole();
        }

        configureAccountType();

        // Leaving this screen signs out. Logging in again continues the registration.
        backButton.setOnClickListener(v -> {
            Toast.makeText(
                    this,
                    "You can finish verifying later by logging in.",
                    Toast.LENGTH_LONG
            ).show();
            AuthSession.logoutToWelcome(this);
        });

        resendText.setOnClickListener(v -> resendVerificationEmail());

        handleIncomingIntent(getIntent());
    }

    private void configureAccountType() {

        int color = ThemeUtils.colorFor(this, role);
        String label = role == null ? "Community Store" : role.getLabel();

        verifyTitle.setText("Verify your " + label + " Account");
        verifyIcon.setTextColor(color);
        resendText.setTextColor(color);
        progressBar.setIndeterminateTintList(ColorStateList.valueOf(color));

        FirebaseUser user = auth.getCurrentUser();
        String email = getIntent().getStringExtra("email");

        if ((email == null || email.isEmpty()) && user != null) {
            email = user.getEmail();
        }

        String next = role != null && role.requiresDocuments()
                ? " After that you'll upload your verification document."
                : "";

        statusText.setText(
                (email == null || email.isEmpty()
                        ? "We've sent a verification link to your email."
                        : "We've sent a verification link to " + email + ".")
                        + " Open it on this phone to continue."
                        + next
        );
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
        handlingLink = false;
        handleIncomingIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!handlingLink && !completed) {
            checkVerificationStatus();
        }
    }

    private void handleIncomingIntent(Intent intent) {

        Uri uri = intent.getData();

        if (uri == null) {
            return;
        }

        String mode = uri.getQueryParameter("mode");
        String oobCode = uri.getQueryParameter("oobCode");

        if (oobCode == null) {

            String wrappedLink = uri.getQueryParameter("link");

            if (wrappedLink != null) {
                Uri innerUri = Uri.parse(wrappedLink);
                mode = innerUri.getQueryParameter("mode");
                oobCode = innerUri.getQueryParameter("oobCode");
            }
        }

        if (oobCode == null) {
            return;
        }

        handlingLink = true;

        if ("resetPassword".equals(mode)) {

            Intent resetIntent = new Intent(VerifyEmailActivity.this, ResetPasswordActivity.class);
            resetIntent.putExtra("oobCode", oobCode);
            startActivity(resetIntent);
            finish();
            return;
        }

        if ("verifyEmail".equals(mode)) {
            statusText.setText("Verifying your account...");
            applyVerificationCode(oobCode);
        }
    }

    private void applyVerificationCode(String verificationCode) {

        auth.applyActionCode(verificationCode)
                .addOnCompleteListener(task -> {

                    handlingLink = false;

                    if (!task.isSuccessful()) {
                        statusText.setText(
                                "This verification link is invalid or has expired. Tap Resend for a new one."
                        );
                        return;
                    }

                    checkVerificationStatus();
                });
    }

    private void checkVerificationStatus() {

        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {
            // Link was opened without a signed-in session: the email is now
            // verified, so Login will route the account to its next step.
            if (handlingLink || getIntent().getData() != null) {
                openAccountVerified(role);
            }
            return;
        }

        user.reload().addOnCompleteListener(task -> {

            FirebaseUser refreshed = auth.getCurrentUser();

            if (refreshed == null || !refreshed.isEmailVerified() || completed) {
                return;
            }

            completed = true;
            statusText.setText("Email verified! Setting up your account...");
            continueAfterVerification(refreshed);
        });
    }

    private void continueAfterVerification(FirebaseUser user) {

        AccountRouter.resolve(user, (destination, resolvedRole, profile) -> {

            UserRole finalRole = resolvedRole != null ? resolvedRole : role;

            switch (destination) {

                case DOCUMENTS:
                    AccountRouter.cacheProfile(this, finalRole, profile);
                    AccountRouter.openDocuments(this, finalRole);
                    break;

                case HOME:
                    // Verification does not log the user in: they sign in on the Login screen.
                    AuthSession.signOut(this);
                    openAccountVerified(finalRole);
                    break;

                case NO_PROFILE:
                    // The profile was never saved: finish it on the Register screen.
                    Intent resume = new Intent(this, RegisterActivity.class);
                    resume.putExtra(RegisterActivity.EXTRA_RESUME, true);
                    resume.putExtra(
                            RegisterActivity.EXTRA_ACCOUNT_TYPE,
                            finalRole == null ? UserRole.STUDENT.getValue() : finalRole.getValue()
                    );
                    startActivity(resume);
                    finish();
                    break;

                default:
                    completed = false;
                    statusText.setText("Could not finish setting up your account. Check your connection and try again.");
                    break;
            }
        });
    }

    private void openAccountVerified(UserRole verifiedRole) {

        Intent intent = new Intent(VerifyEmailActivity.this, AccountVerifiedActivity.class);
        intent.putExtra("accountType", verifiedRole == null ? null : verifiedRole.getValue());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void resendVerificationEmail() {

        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {
            Toast.makeText(this, "Please log in again to resend the email.", Toast.LENGTH_SHORT).show();
            return;
        }

        resendText.setEnabled(false);

        user.sendEmailVerification(RegisterActivity.verificationSettings())
                .addOnCompleteListener(task -> {
                    resendText.setEnabled(true);

                    Toast.makeText(
                            VerifyEmailActivity.this,
                            task.isSuccessful()
                                    ? "Verification email sent."
                                    : "Please wait a moment before requesting another email.",
                            Toast.LENGTH_SHORT
                    ).show();
                });

        progressBar.setVisibility(View.VISIBLE);
    }
}
