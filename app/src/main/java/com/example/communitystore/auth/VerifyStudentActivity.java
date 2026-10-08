package com.example.communitystore.auth;

import com.example.communitystore.R;
import com.example.communitystore.BuildConfig;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class VerifyStudentActivity extends AppCompatActivity {

    private TextView backButton;
    private TextView resendText;
    private TextView statusText;
    private TextView verifyTitle;
    private TextView verifyIcon;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private boolean handlingLink = false;

    private String accountType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_verify_student);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        backButton = findViewById(R.id.backButton);
        resendText = findViewById(R.id.resendText);
        statusText = findViewById(R.id.statusText);
        verifyTitle = findViewById(R.id.verifyTitle);
        verifyIcon = findViewById(R.id.verifyIcon);

        accountType =
                getIntent().getStringExtra("accountType");

        if (accountType == null) {
            accountType =
                    PendingRegistrationStore.getRole(this);
        }

        configureAccountType();

        backButton.setOnClickListener(v -> finish());

        resendText.setOnClickListener(
                v -> resendVerificationEmail()
        );

        handleIncomingIntent(getIntent());
    }

    private void configureAccountType() {

        if ("faculty".equals(accountType)) {

            verifyTitle.setText(
                    "Verify your Faculty Account"
            );

            verifyIcon.setTextColor(
                    Color.parseColor("#FF4B2B")
            );

            resendText.setTextColor(
                    Color.parseColor("#FF4B2B")
            );

        } else if ("vendor".equals(accountType)) {

            verifyTitle.setText(
                    "Verify your Vendor Account"
            );

        } else if ("residence".equals(accountType)) {

            verifyTitle.setText(
                    "Verify your Residence Account"
            );

        } else {

            verifyTitle.setText(
                    "Verify your Student Account"
            );

            verifyIcon.setTextColor(
                    Color.parseColor("#168FC1")
            );

            resendText.setTextColor(
                    Color.parseColor("#168FC1")
            );
        }
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

        if (!handlingLink) {
            checkVerificationStatus();
        }
    }

    private void handleIncomingIntent(Intent intent) {

        Uri uri = intent.getData();

        if (uri == null) {
            return;
        }

        String mode =
                uri.getQueryParameter("mode");

        String oobCode =
                uri.getQueryParameter("oobCode");

        if (oobCode == null) {

            String wrappedLink =
                    uri.getQueryParameter("link");

            if (wrappedLink != null) {

                Uri innerUri =
                        Uri.parse(wrappedLink);

                mode =
                        innerUri.getQueryParameter("mode");

                oobCode =
                        innerUri.getQueryParameter("oobCode");
            }
        }

        if (oobCode == null) {
            return;
        }

        handlingLink = true;

        if ("resetPassword".equals(mode)) {

            Intent resetIntent =
                    new Intent(
                            VerifyStudentActivity.this,
                            ResetPasswordActivity.class
                    );

            resetIntent.putExtra(
                    "oobCode",
                    oobCode
            );

            startActivity(resetIntent);
            finish();

            return;
        }

        if ("verifyEmail".equals(mode)) {

            statusText.setText(
                    "Verifying your account..."
            );

            applyVerificationCode(oobCode);
        }
    }

    private void applyVerificationCode(
            String verificationCode
    ) {

        auth.applyActionCode(verificationCode)
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {

                        Toast.makeText(
                                VerifyStudentActivity.this,
                                "Verification failed.",
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }

                    FirebaseUser user =
                            auth.getCurrentUser();

                    if (user == null) {

                        /*
                         * Email verification succeeded,
                         * but no Firebase session exists.
                         * Send user to login.
                         */

                        openLoginAfterVerification();
                        return;
                    }

                    user.reload()
                            .addOnCompleteListener(
                                    reloadTask -> {

                                        FirebaseUser refreshed =
                                                auth.getCurrentUser();

                                        if (refreshed != null
                                                && refreshed.isEmailVerified()) {

                                            activateUser(refreshed);
                                        }
                                    }
                            );
                });
    }

    private void checkVerificationStatus() {

        FirebaseUser user =
                auth.getCurrentUser();

        if (user == null) {
            return;
        }

        user.reload()
                .addOnCompleteListener(task -> {

                    FirebaseUser refreshed =
                            auth.getCurrentUser();

                    if (refreshed != null
                            && refreshed.isEmailVerified()) {

                        activateUser(refreshed);

                    } else {

                        statusText.setText(
                                "Open the verification link sent to your email."
                        );
                    }
                });
    }

    private void activateUser(
            FirebaseUser user
    ) {

        db.collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(document -> {

                    /*
                     * If the verified user already exists,
                     * just sign out and send them to Login.
                     */

                    if (document.exists()) {

                        PendingRegistrationStore.clear(this);

                        openLoginAfterVerification();
                        return;
                    }

                    createVerifiedUser(user);
                })
                .addOnFailureListener(e ->

                        Toast.makeText(
                                VerifyStudentActivity.this,
                                "Could not activate account.",
                                Toast.LENGTH_LONG
                        ).show()
                );
    }

    private void createVerifiedUser(
            FirebaseUser user
    ) {

        if (!PendingRegistrationStore.belongsTo(
                this,
                user.getUid()
        )) {

            /*
             * Email has been verified, but local
             * registration details are unavailable.
             */

            openLoginAfterVerification();
            return;
        }

        String role =
                PendingRegistrationStore.getRole(this);

        Map<String, Object> data =
                new HashMap<>();

        data.put(
                "displayName",
                PendingRegistrationStore
                        .getDisplayName(this)
        );

        data.put(
                "email",
                PendingRegistrationStore
                        .getEmail(this)
        );

        data.put(
                "role",
                role
        );

        data.put(
                "verified",
                true
        );

        data.put(
                "profileImageUrl",
                ""
        );

        data.put(
                "createdAt",
                FieldValue.serverTimestamp()
        );

        data.put(
                "verifiedAt",
                FieldValue.serverTimestamp()
        );

        if ("student".equals(role)) {

            data.put(
                    "universityName",
                    PendingRegistrationStore
                            .getUniversityName(this)
            );
        }

        db.collection("users")
                .document(user.getUid())
                .set(data)
                .addOnSuccessListener(unused -> {

                    PendingRegistrationStore.clear(
                            VerifyStudentActivity.this
                    );

                    /*
                     * IMPORTANT:
                     *
                     * Verification DOES NOT log
                     * the user into the app.
                     *
                     * We sign out and force them
                     * through the Login screen.
                     */

                    openLoginAfterVerification();
                })
                .addOnFailureListener(e ->

                        Toast.makeText(
                                VerifyStudentActivity.this,
                                "Could not create account profile: "
                                        + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show()
                );
    }

    private void openLoginAfterVerification() {

        String role =
                PendingRegistrationStore.getRole(this);

        /*
         * Sign the user out.
         * Verification does NOT give access to the app.
         */
        auth.signOut();

        getSharedPreferences(
                "CommunityStorePrefs",
                MODE_PRIVATE
        )
                .edit()
                .remove("accountType")
                .apply();

        Intent intent =
                new Intent(
                        VerifyStudentActivity.this,
                        AccountVerifiedActivity.class
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

    private void resendVerificationEmail() {

        FirebaseUser user =
                auth.getCurrentUser();

        if (user == null) {

            Toast.makeText(
                    this,
                    "Please register again.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        ActionCodeSettings settings =
                ActionCodeSettings.newBuilder()

                        .setUrl(
                                BuildConfig.FIREBASE_AUTH_URL
                        )

                        .setHandleCodeInApp(true)

                        .setAndroidPackageName(
                                "com.example.communitystore",
                                false,
                                null
                        )

                        .build();

        user.sendEmailVerification(settings)
                .addOnSuccessListener(unused ->

                        Toast.makeText(
                                VerifyStudentActivity.this,
                                "Verification email sent.",
                                Toast.LENGTH_SHORT
                        ).show()
                );
    }
}
