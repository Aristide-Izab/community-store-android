package com.example.communitystore.auth;

import com.example.communitystore.R;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    private TextInputEditText newPasswordInput;
    private TextInputEditText confirmPasswordInput;

    private TextView strengthText;
    private TextView backButton;

    private Button resetPasswordButton;

    private FirebaseAuth auth;

    private String oobCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        auth = FirebaseAuth.getInstance();

        oobCode = getIntent().getStringExtra("oobCode");

        newPasswordInput =
                findViewById(R.id.newPasswordInput);

        confirmPasswordInput =
                findViewById(R.id.confirmPasswordInput);

        strengthText =
                findViewById(R.id.strengthText);

        resetPasswordButton =
                findViewById(R.id.resetPasswordButton);

        backButton =
                findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> finish());

        if (oobCode == null || oobCode.isEmpty()) {

            Toast.makeText(
                    this,
                    "Invalid password reset link.",
                    Toast.LENGTH_LONG
            ).show();

            finish();
            return;
        }

        auth.verifyPasswordResetCode(oobCode)
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {

                        Toast.makeText(
                                ResetPasswordActivity.this,
                                "This reset link is invalid or expired.",
                                Toast.LENGTH_LONG
                        ).show();

                        finish();
                        return;
                    }

                    showEmailVerifiedDialog();
                });

        newPasswordInput.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after
                    ) {
                    }

                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count
                    ) {
                        updateStrength(s.toString());
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s
                    ) {
                    }
                }
        );

        resetPasswordButton.setOnClickListener(v ->
                resetPassword()
        );
    }

    private void updateStrength(String password) {

        if (password.isEmpty()) {
            strengthText.setText(
                    "Password strength:"
            );
            return;
        }

        int score = 0;

        if (password.length() >= 6) score++;
        if (password.length() >= 10) score++;
        if (password.matches(".*[A-Z].*")) score++;
        if (password.matches(".*[0-9].*")) score++;
        if (password.matches(
                ".*[!@#$%^&*()_+\\-=].*"
        )) score++;

        if (score <= 2) {
            strengthText.setText(
                    "Password strength: Weak"
            );
        } else if (score <= 4) {
            strengthText.setText(
                    "Password strength: Good"
            );
        } else {
            strengthText.setText(
                    "Password strength: Excellent"
            );
        }
    }

    private void resetPassword() {

        String password =
                newPasswordInput.getText() != null
                        ? newPasswordInput.getText()
                        .toString()
                        : "";

        String confirmPassword =
                confirmPasswordInput.getText() != null
                        ? confirmPasswordInput.getText()
                        .toString()
                        : "";

        if (password.length() < 6) {

            newPasswordInput.setError(
                    "Password must be at least 6 characters"
            );

            return;
        }

        if (!password.equals(confirmPassword)) {

            confirmPasswordInput.setError(
                    "Passwords do not match"
            );

            return;
        }

        resetPasswordButton.setEnabled(false);
        resetPasswordButton.setText("Resetting...");

        auth.confirmPasswordReset(
                        oobCode,
                        password
                )
                .addOnCompleteListener(task -> {

                    resetPasswordButton.setEnabled(true);
                    resetPasswordButton.setText(
                            "Reset Password"
                    );

                    if (task.isSuccessful()) {

                        showPasswordResetDialog();

                    } else {

                        Toast.makeText(
                                ResetPasswordActivity.this,
                                "Password reset failed: " +
                                        (task.getException() != null
                                                ? task.getException()
                                                .getMessage()
                                                : ""),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    private void showEmailVerifiedDialog() {

        View view =
                getLayoutInflater().inflate(
                        R.layout.dialog_email_verified,
                        null
                );

        AlertDialog dialog =
                new AlertDialog.Builder(this)
                        .setView(view)
                        .setCancelable(false)
                        .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(
                            android.graphics.Color.TRANSPARENT
                    )
            );
        }

        Button continueButton =
                view.findViewById(
                        R.id.continueButton
                );

        continueButton.setOnClickListener(v ->
                dialog.dismiss()
        );

        dialog.show();
    }

    private void showPasswordResetDialog() {

        View view =
                getLayoutInflater().inflate(
                        R.layout.dialog_password_reset_success,
                        null
                );

        AlertDialog dialog =
                new AlertDialog.Builder(this)
                        .setView(view)
                        .setCancelable(false)
                        .create();

        Button loginButton =
                view.findViewById(
                        R.id.loginNowButton
                );

        loginButton.setOnClickListener(v -> {

            Intent intent = new Intent(
                    ResetPasswordActivity.this,
                    LoginActivity.class
            );

            intent.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);

            dialog.dismiss();
            finish();
        });

        dialog.show();
    }
}
