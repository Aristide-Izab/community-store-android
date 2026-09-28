package com.example.communitystore;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
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
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private EditText fullNameInput;
    private EditText universityNameInput;
    private EditText emailInput;
    private EditText passwordInput;
    private EditText confirmPasswordInput;

    private TextView universityNameLabel;
    private TextView backButton;
    private TextView loginText;

    private CheckBox termsCheckBox;
    private Button createAccountButton;

    private FirebaseAuth auth;

    private String accountType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();

        accountType =
                getIntent().getStringExtra("accountType");

        fullNameInput =
                findViewById(R.id.fullNameInput);

        universityNameInput =
                findViewById(R.id.universityNameInput);

        universityNameLabel =
                findViewById(R.id.universityNameLabel);

        emailInput =
                findViewById(R.id.emailInput);

        passwordInput =
                findViewById(R.id.passwordInput);

        confirmPasswordInput =
                findViewById(R.id.confirmPasswordInput);

        termsCheckBox =
                findViewById(R.id.termsCheckBox);

        createAccountButton =
                findViewById(R.id.createAccountButton);

        backButton =
                findViewById(R.id.backButton);

        loginText =
                findViewById(R.id.loginText);

        configureAccountType();

        backButton.setOnClickListener(v -> finish());

        loginText.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            RegisterActivity.this,
                            LoginActivity.class
                    );

            startActivity(intent);
        });

        createAccountButton.setOnClickListener(
                v -> validateForm()
        );
    }

    private void configureAccountType() {

        boolean student =
                "student".equals(accountType);

        universityNameLabel.setVisibility(
                student ? View.VISIBLE : View.GONE
        );

        universityNameInput.setVisibility(
                student ? View.VISIBLE : View.GONE
        );

        if ("faculty".equals(accountType)) {

            createAccountButton.setText("Next");

            createAccountButton.setBackgroundTintList(
                    ColorStateList.valueOf(
                            Color.parseColor("#FF4B2B")
                    )
            );

        } else if ("vendor".equals(accountType)) {

            createAccountButton.setText("Next");

            createAccountButton.setBackgroundTintList(
                    ColorStateList.valueOf(
                            Color.parseColor("#FFA726")
                    )
            );

        } else if ("residence".equals(accountType)) {

            createAccountButton.setText("Next");

            createAccountButton.setBackgroundTintList(
                    ColorStateList.valueOf(
                            Color.parseColor("#4B00D1")
                    )
            );

        } else {

            createAccountButton.setText(
                    "Create Account"
            );

            createAccountButton.setBackgroundTintList(
                    ColorStateList.valueOf(
                            Color.parseColor("#168FC1")
                    )
            );
        }
    }

    private void validateForm() {

        String fullName =
                fullNameInput.getText()
                        .toString()
                        .trim();

        String universityName =
                universityNameInput.getText()
                        .toString()
                        .trim();

        String email =
                emailInput.getText()
                        .toString()
                        .trim();

        String password =
                passwordInput.getText()
                        .toString();

        String confirmPassword =
                confirmPasswordInput.getText()
                        .toString();

        if (fullName.isEmpty()) {

            fullNameInput.setError(
                    "Enter your full name"
            );

            fullNameInput.requestFocus();
            return;
        }

        if ("student".equals(accountType)
                && universityName.isEmpty()) {

            universityNameInput.setError(
                    "Enter your university name"
            );

            universityNameInput.requestFocus();
            return;
        }

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
                    "Enter a password"
            );

            passwordInput.requestFocus();
            return;
        }

        if (password.length() < 6) {

            passwordInput.setError(
                    "Password must be at least 6 characters"
            );

            passwordInput.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {

            confirmPasswordInput.setError(
                    "Passwords do not match"
            );

            confirmPasswordInput.requestFocus();
            return;
        }

        if (!termsCheckBox.isChecked()) {

            Toast.makeText(
                    this,
                    "Please agree to the Terms & Conditions",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        createFirebaseAccount(
                fullName,
                universityName,
                email,
                password
        );
    }

    private void createFirebaseAccount(
            String fullName,
            String universityName,
            String email,
            String password
    ) {

        createAccountButton.setEnabled(false);

        auth.createUserWithEmailAndPassword(
                        email,
                        password
                )
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {

                        Toast.makeText(
                                this,
                                task.getException() != null
                                        ? task.getException().getMessage()
                                        : "Registration failed",
                                Toast.LENGTH_LONG
                        ).show();

                        createAccountButton.setEnabled(true);
                        return;
                    }

                    FirebaseUser user =
                            auth.getCurrentUser();

                    if (user == null) {

                        createAccountButton.setEnabled(true);
                        return;
                    }

                    PendingRegistrationStore.save(
                            RegisterActivity.this,
                            user.getUid(),
                            fullName,
                            universityName,
                            email,
                            accountType
                    );

                    if ("vendor".equals(accountType)) {

                        createAccountButton.setEnabled(true);

                        startActivity(
                                new Intent(
                                        RegisterActivity.this,
                                        VendorVerificationActivity.class
                                )
                        );

                        return;
                    }

                    if ("residence".equals(accountType)) {

                        createAccountButton.setEnabled(true);

                        startActivity(
                                new Intent(
                                        RegisterActivity.this,
                                        ResidenceVerificationActivity.class
                                )
                        );

                        return;
                    }

                    sendVerificationEmail(
                            user,
                            email
                    );
                });
    }

    private void sendVerificationEmail(
            FirebaseUser user,
            String email
    ) {

        ActionCodeSettings settings =
                ActionCodeSettings.newBuilder()
                        .setUrl(
                                "https://community-store-de45f.firebaseapp.com"
                        )
                        .setHandleCodeInApp(true)
                        .setAndroidPackageName(
                                "com.example.communitystore",
                                false,
                                null
                        )
                        .build();

        user.sendEmailVerification(settings)
                .addOnCompleteListener(task -> {

                    createAccountButton.setEnabled(true);

                    if (!task.isSuccessful()) {

                        Toast.makeText(
                                this,
                                "Could not send verification email.",
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }

                    Intent intent =
                            new Intent(
                                    RegisterActivity.this,
                                    VerifyStudentActivity.class
                            );

                    intent.putExtra(
                            "email",
                            email
                    );

                    intent.putExtra(
                            "accountType",
                            accountType
                    );

                    startActivity(intent);
                });
    }
}