package com.example.communitystore.auth;

import com.example.communitystore.R;
import com.example.communitystore.BuildConfig;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText emailInput;
    private Button verifyEmailButton;
    private TextView backButton;
    private TextView backToLoginText;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        auth = FirebaseAuth.getInstance();

        emailInput = findViewById(R.id.emailInput);
        verifyEmailButton = findViewById(R.id.verifyEmailButton);
        backButton = findViewById(R.id.backButton);
        backToLoginText = findViewById(R.id.backToLoginText);

        String existingEmail =
                getIntent().getStringExtra("email");

        if (existingEmail != null) {
            emailInput.setText(existingEmail);
        }

        backButton.setOnClickListener(v -> finish());

        backToLoginText.setOnClickListener(v -> finish());

        verifyEmailButton.setOnClickListener(v ->
                sendResetEmail()
        );
    }

    private void sendResetEmail() {

        String email =
                emailInput.getText().toString().trim();

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

        verifyEmailButton.setEnabled(false);
        verifyEmailButton.setText("Sending...");

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

        auth.sendPasswordResetEmail(email, settings)
                .addOnCompleteListener(task -> {

                    verifyEmailButton.setEnabled(true);
                    verifyEmailButton.setText("Verify Email");

                    if (task.isSuccessful()) {

                        Toast.makeText(
                                ForgotPasswordActivity.this,
                                "Reset link sent. Check your email.",
                                Toast.LENGTH_LONG
                        ).show();

                    } else {

                        Toast.makeText(
                                ForgotPasswordActivity.this,
                                "Could not send reset email: " +
                                        (task.getException() != null
                                                ? task.getException().getMessage()
                                                : ""),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}
