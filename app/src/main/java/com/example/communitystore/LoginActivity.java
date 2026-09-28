package com.example.communitystore;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput;
    private EditText passwordInput;

    private Button loginButton;
    private Button microsoftButton;

    private TextView signUpText;
    private TextView backButton;
    private TextView forgotPasswordText;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        emailInput =
                findViewById(R.id.emailInput);

        passwordInput =
                findViewById(R.id.passwordInput);

        loginButton =
                findViewById(R.id.loginButton);

        microsoftButton =
                findViewById(R.id.microsoftButton);

        signUpText =
                findViewById(R.id.signUpText);

        backButton =
                findViewById(R.id.backButton);

        forgotPasswordText =
                findViewById(R.id.forgotPasswordText);

        backButton.setOnClickListener(
                v -> finish()
        );

        signUpText.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            LoginActivity.this,
                            ChooseAccountTypeActivity.class
                    );

            startActivity(intent);
        });

        forgotPasswordText.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            LoginActivity.this,
                            ForgotPasswordActivity.class
                    );

            String email =
                    emailInput.getText()
                            .toString()
                            .trim();

            if (!email.isEmpty()) {

                intent.putExtra(
                        "email",
                        email
                );
            }

            startActivity(intent);
        });

        loginButton.setOnClickListener(
                v -> loginUser()
        );

        microsoftButton.setOnClickListener(v ->

                Toast.makeText(
                        this,
                        "Microsoft login will be added later.",
                        Toast.LENGTH_SHORT
                ).show()
        );
    }

    private void loginUser() {

        String email =
                emailInput.getText()
                        .toString()
                        .trim();

        String password =
                passwordInput.getText()
                        .toString();

        if (email.isEmpty()) {

            emailInput.setError(
                    "Enter your email"
            );

            emailInput.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS
                .matcher(email)
                .matches()) {

            emailInput.setError(
                    "Enter a valid email"
            );

            emailInput.requestFocus();
            return;
        }

        if (password.isEmpty()) {

            passwordInput.setError(
                    "Enter your password"
            );

            passwordInput.requestFocus();
            return;
        }

        loginButton.setEnabled(false);
        loginButton.setText("Logging in...");

        auth.signInWithEmailAndPassword(
                        email,
                        password
                )
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {

                        Toast.makeText(
                                this,
                                "Invalid email or password.",
                                Toast.LENGTH_LONG
                        ).show();

                        resetLoginButton();
                        return;
                    }

                    FirebaseUser user =
                            auth.getCurrentUser();

                    if (user == null) {

                        resetLoginButton();
                        return;
                    }

                    loadUserAccount(user);
                });
    }

    private void loadUserAccount(
            FirebaseUser user
    ) {

        db.collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(document -> {

                    if (!document.exists()) {

                        checkVendorApplication(user);
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

                        Toast.makeText(
                                this,
                                "Your account is not active.",
                                Toast.LENGTH_LONG
                        ).show();

                        resetLoginButton();
                        return;
                    }

                    if (requiresEmailVerification(role)
                            && !user.isEmailVerified()) {

                        auth.signOut();

                        Toast.makeText(
                                this,
                                "Please verify your email first.",
                                Toast.LENGTH_LONG
                        ).show();

                        resetLoginButton();
                        return;
                    }

                    saveAccountType(role);

                    openMain(role);
                })

                .addOnFailureListener(e -> {

                    auth.signOut();

                    Toast.makeText(
                            this,
                            "Could not load account.",
                            Toast.LENGTH_LONG
                    ).show();

                    resetLoginButton();
                });
    }

    private void checkVendorApplication(
            FirebaseUser user
    ) {

        db.collection("vendors")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(document -> {

                    if (document.exists()) {

                        auth.signOut();

                        String status =
                                document.getString(
                                        "verificationStatus"
                                );

                        showPendingStatus(
                                "vendor",
                                status
                        );

                        return;
                    }

                    checkResidenceApplication(user);
                })

                .addOnFailureListener(e ->
                        checkResidenceApplication(user)
                );
    }

    private void checkResidenceApplication(
            FirebaseUser user
    ) {

        db.collection("residences")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(document -> {

                    auth.signOut();

                    if (document.exists()) {

                        String status =
                                document.getString(
                                        "verificationStatus"
                                );

                        showPendingStatus(
                                "residence",
                                status
                        );

                    } else {

                        Toast.makeText(
                                this,
                                "Account not found. Please complete registration first.",
                                Toast.LENGTH_LONG
                        ).show();

                        resetLoginButton();
                    }
                })

                .addOnFailureListener(e -> {

                    auth.signOut();

                    Toast.makeText(
                            this,
                            "Account not found.",
                            Toast.LENGTH_LONG
                    ).show();

                    resetLoginButton();
                });
    }

    private void showPendingStatus(
            String type,
            String status
    ) {

        if ("pending".equals(status)) {

            String message =
                    "vendor".equals(type)
                            ? "Your vendor application is still under review."
                            : "Your residence administrator application is still under review.";

            Toast.makeText(
                    this,
                    message,
                    Toast.LENGTH_LONG
            ).show();

        } else if ("rejected".equals(status)) {

            String message =
                    "vendor".equals(type)
                            ? "Your vendor application was rejected."
                            : "Your residence administrator application was rejected.";

            Toast.makeText(
                    this,
                    message,
                    Toast.LENGTH_LONG
            ).show();

        } else {

            Toast.makeText(
                    this,
                    "Your account is not active yet.",
                    Toast.LENGTH_LONG
            ).show();
        }

        resetLoginButton();
    }

    private boolean requiresEmailVerification(
            String role
    ) {

        return "student".equals(role)
                || "faculty".equals(role);
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

        SharedPreferences preferences =
                getSharedPreferences(
                        "CommunityStorePrefs",
                        MODE_PRIVATE
                );

        preferences.edit()
                .putString(
                        "accountType",
                        role
                )
                .apply();
    }

    private void openMain(
            String role
    ) {

        Intent intent =
                new Intent(
                        LoginActivity.this,
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

    private void resetLoginButton() {

        loginButton.setEnabled(true);
        loginButton.setText("Login");
    }
}
