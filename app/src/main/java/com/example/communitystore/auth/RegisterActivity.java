package com.example.communitystore.auth;

import com.example.communitystore.R;
import com.example.communitystore.BuildConfig;
import com.example.communitystore.core.access.UserRole;
import com.example.communitystore.core.session.AccountStatus;
import com.example.communitystore.core.session.AuthSession;
import com.example.communitystore.core.session.SessionManager;
import com.example.communitystore.shared.utils.ThemeUtils;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * Step 1 of registration for every account type.
 *
 * Creates the Firebase Auth account, immediately saves users/{uid} with
 * verificationStatus = "email_pending", then sends the verification email.
 * Because the profile is in Firestore from the start, a user who closes the
 * app mid-registration can log in later and continue where they stopped.
 *
 * "Resume" mode (extra "resume" = true) is used when an Auth account exists
 * but its profile was never saved: the password fields are hidden and only
 * the profile is created for the signed-in user.
 */
public class RegisterActivity extends AppCompatActivity {

    public static final String EXTRA_ACCOUNT_TYPE = "accountType";
    public static final String EXTRA_RESUME = "resume";

    private EditText fullNameInput;
    private EditText universityNameInput;
    private EditText emailInput;
    private EditText passwordInput;
    private EditText confirmPasswordInput;

    private TextView universityNameLabel;
    private TextView registerTitle;
    private TextView registerSubtitle;
    private TextView backButton;
    private TextView loginText;

    private CheckBox termsCheckBox;
    private Button createAccountButton;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private UserRole role;
    private boolean resumeMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_register);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        role = UserRole.fromString(getIntent().getStringExtra(EXTRA_ACCOUNT_TYPE));

        if (role == null) {
            role = UserRole.STUDENT;
        }

        resumeMode = getIntent().getBooleanExtra(EXTRA_RESUME, false)
                && auth.getCurrentUser() != null;

        fullNameInput = findViewById(R.id.fullNameInput);
        universityNameInput = findViewById(R.id.universityNameInput);
        universityNameLabel = findViewById(R.id.universityNameLabel);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        termsCheckBox = findViewById(R.id.termsCheckBox);
        createAccountButton = findViewById(R.id.createAccountButton);
        backButton = findViewById(R.id.backButton);
        loginText = findViewById(R.id.loginText);
        registerTitle = findViewById(R.id.registerTitle);
        registerSubtitle = findViewById(R.id.registerSubtitle);

        configureAccountType();

        if (resumeMode) {
            configureResumeMode();
        }

        backButton.setOnClickListener(v -> finish());

        loginText.setOnClickListener(v -> {
            if (resumeMode) {
                AuthSession.signOut(this);
            }
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });

        createAccountButton.setOnClickListener(v -> validateForm());
    }

    private void configureAccountType() {

        int color = ThemeUtils.colorFor(this, role);

        boolean student = role == UserRole.STUDENT;

        universityNameLabel.setVisibility(student ? View.VISIBLE : View.GONE);
        universityNameInput.setVisibility(student ? View.VISIBLE : View.GONE);

        registerTitle.setText("Create your " + role.getLabel() + " account");

        switch (role) {
            case FACULTY:
                registerSubtitle.setText("Shop and connect with the campus community");
                emailInput.setHint("name.surname@cput.ac.za");
                break;
            case VENDOR:
                registerSubtitle.setText("Step 1 of 3: create your login. Next you'll verify your email and business.");
                emailInput.setHint("you@yourbusiness.co.za");
                break;
            case RESIDENCE:
                registerSubtitle.setText("Step 1 of 3: create your login. Next you'll verify your email and residence role.");
                emailInput.setHint("name.surname@cput.ac.za");
                break;
            case STUDENT:
            default:
                registerSubtitle.setText("Buy and sell with students on your campus");
                emailInput.setHint("example@mycput.ac.za");
                break;
        }

        createAccountButton.setText("Create Account");
        createAccountButton.setBackgroundTintList(ColorStateList.valueOf(color));
        termsCheckBox.setButtonTintList(ColorStateList.valueOf(color));
    }

    private void configureResumeMode() {

        FirebaseUser user = auth.getCurrentUser();

        registerTitle.setText("Finish your " + role.getLabel() + " account");
        registerSubtitle.setText("Your login already exists. Add your details to continue.");

        if (user != null && user.getEmail() != null) {
            emailInput.setText(user.getEmail());
        }

        emailInput.setEnabled(false);

        hide(R.id.passwordLabel);
        hide(R.id.passwordLayout);
        hide(R.id.confirmPasswordLabel);
        hide(R.id.confirmPasswordLayout);

        createAccountButton.setText("Continue");
    }

    private void hide(int id) {
        View view = findViewById(id);
        if (view != null) {
            view.setVisibility(View.GONE);
        }
    }

    private void validateForm() {

        String fullName = fullNameInput.getText().toString().trim();
        String universityName = universityNameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString();
        String confirmPassword = confirmPasswordInput.getText().toString();

        if (fullName.isEmpty()) {
            fullNameInput.setError("Enter your full name");
            fullNameInput.requestFocus();
            return;
        }

        if (role == UserRole.STUDENT && universityName.isEmpty()) {
            universityNameInput.setError("Enter your university name");
            universityNameInput.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            emailInput.setError("Enter your email");
            emailInput.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Enter a valid email");
            emailInput.requestFocus();
            return;
        }

        if (!resumeMode) {

            if (password.isEmpty()) {
                passwordInput.setError("Enter a password");
                passwordInput.requestFocus();
                return;
            }

            if (password.length() < 6) {
                passwordInput.setError("Password must be at least 6 characters");
                passwordInput.requestFocus();
                return;
            }

            if (!password.equals(confirmPassword)) {
                confirmPasswordInput.setError("Passwords do not match");
                confirmPasswordInput.requestFocus();
                return;
            }
        }

        if (!termsCheckBox.isChecked()) {
            Toast.makeText(this, "Please agree to the Terms & Conditions", Toast.LENGTH_SHORT).show();
            return;
        }

        setBusy(true);

        if (resumeMode) {
            saveProfile(auth.getCurrentUser(), fullName, universityName, email);
        } else {
            createFirebaseAccount(fullName, universityName, email, password);
        }
    }

    private void createFirebaseAccount(
            String fullName,
            String universityName,
            String email,
            String password
    ) {

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {

                        String message = task.getException() instanceof FirebaseAuthUserCollisionException
                                ? "An account with this email already exists. Log in to continue your registration."
                                : task.getException() != null
                                ? task.getException().getMessage()
                                : "Registration failed";

                        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                        setBusy(false);
                        return;
                    }

                    FirebaseUser user = auth.getCurrentUser();

                    if (user == null) {
                        setBusy(false);
                        return;
                    }

                    saveProfile(user, fullName, universityName, email);
                });
    }

    private void saveProfile(
            FirebaseUser user,
            String fullName,
            String universityName,
            String email
    ) {

        if (user == null) {
            setBusy(false);
            Toast.makeText(this, "Your session expired. Please log in again.", Toast.LENGTH_LONG).show();
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("displayName", fullName);
        // The email stays in Firebase Auth only: users/{uid} is readable by other
        // signed-in users (seller profiles, leaderboards), so it holds no contact details.
        data.put("role", role.getValue());
        data.put("verified", false);
        data.put("verificationStatus", AccountStatus.EMAIL_PENDING);
        data.put("profileImageUrl", "");
        data.put("points", 0);
        data.put("ratingTotal", 0);
        data.put("ratingCount", 0);
        data.put("createdAt", FieldValue.serverTimestamp());

        if (role == UserRole.STUDENT) {
            data.put("universityName", universityName);
        }

        user.updateProfile(new UserProfileChangeRequest.Builder()
                .setDisplayName(fullName)
                .build());

        db.collection("users")
                .document(user.getUid())
                .set(data)
                .addOnSuccessListener(unused -> {
                    SessionManager session = new SessionManager(this);
                    session.saveUserRole(role);
                    session.saveDisplayName(fullName);

                    if (user.isEmailVerified()) {
                        // Resume mode for an address that is already confirmed.
                        openVerifyScreen(email);
                    } else {
                        sendVerificationEmail(user, email);
                    }
                })
                .addOnFailureListener(e -> {
                    setBusy(false);
                    Toast.makeText(
                            this,
                            "Your login was created but your profile could not be saved ("
                                    + e.getMessage()
                                    + "). Log in to try again.",
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void sendVerificationEmail(FirebaseUser user, String email) {

        user.sendEmailVerification(verificationSettings())
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {
                        Toast.makeText(
                                this,
                                "Could not send the verification email. Use Resend on the next screen.",
                                Toast.LENGTH_LONG
                        ).show();
                    }

                    openVerifyScreen(email);
                });
    }

    private void openVerifyScreen(String email) {
        setBusy(false);

        Intent intent = new Intent(RegisterActivity.this, VerifyEmailActivity.class);
        intent.putExtra("email", email);
        intent.putExtra(EXTRA_ACCOUNT_TYPE, role.getValue());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    static ActionCodeSettings verificationSettings() {
        return ActionCodeSettings.newBuilder()
                .setUrl(BuildConfig.FIREBASE_AUTH_URL)
                .setHandleCodeInApp(true)
                .setAndroidPackageName("com.example.communitystore", false, null)
                .build();
    }

    private void setBusy(boolean busy) {
        createAccountButton.setEnabled(!busy);
        if (busy) {
            createAccountButton.setText("Please wait...");
        } else {
            createAccountButton.setText(resumeMode ? "Continue" : "Create Account");
        }
    }
}
