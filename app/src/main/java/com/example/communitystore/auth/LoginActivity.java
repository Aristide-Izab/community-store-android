package com.example.communitystore.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.communitystore.R;
import com.example.communitystore.core.access.AccessManager;
import com.example.communitystore.core.access.AccountRouter;
import com.example.communitystore.core.session.AuthSession;
import com.example.communitystore.core.session.SessionManager;
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

    private SessionManager sessionManager;

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_auth_login
        );

        auth =
                FirebaseAuth.getInstance();

        db =
                FirebaseFirestore.getInstance();

        sessionManager =
                new SessionManager(this);

        emailInput =
                findViewById(
                        R.id.emailInput
                );

        passwordInput =
                findViewById(
                        R.id.passwordInput
                );

        loginButton =
                findViewById(
                        R.id.loginButton
                );

        microsoftButton =
                findViewById(
                        R.id.microsoftButton
                );

        signUpText =
                findViewById(
                        R.id.signUpText
                );

        backButton =
                findViewById(
                        R.id.backButton
                );

        forgotPasswordText =
                findViewById(
                        R.id.forgotPasswordText
                );

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
                        "Microsoft sign-in isn't available yet. Please use your email and password.",
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

        loginButton.setText(
                "Logging in..."
        );

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

        AccountRouter.resolve(user, (destination, role, profile) -> {

            switch (destination) {

                case HOME:
                    AccountRouter.cacheProfile(this, role, profile);
                    AccessManager.openHome(LoginActivity.this, role);
                    break;

                case VERIFY_EMAIL:
                    AccountRouter.cacheProfile(this, role, profile);
                    Toast.makeText(
                            this,
                            "Please verify your email address to continue.",
                            Toast.LENGTH_LONG
                    ).show();
                    AccountRouter.openVerifyEmail(this, role);
                    break;

                case DOCUMENTS:
                    AccountRouter.cacheProfile(this, role, profile);
                    Toast.makeText(
                            this,
                            "Almost done! Submit your verification document to activate your account.",
                            Toast.LENGTH_LONG
                    ).show();
                    AccountRouter.openDocuments(this, role);
                    break;

                case PENDING_REVIEW:
                case REJECTED:
                    AuthSession.signOut(this);
                    Toast.makeText(
                            this,
                            AccountRouter.blockedMessage(destination, role),
                            Toast.LENGTH_LONG
                    ).show();
                    resetLoginButton();
                    break;

                case NO_PROFILE:
                    resumeRegistration();
                    break;

                case ERROR:
                default:
                    AuthSession.signOut(this);
                    Toast.makeText(
                            this,
                            "Could not load your account. Check your connection and try again.",
                            Toast.LENGTH_LONG
                    ).show();
                    resetLoginButton();
                    break;
            }
        });
    }

    /**
     * The login exists but no profile was saved (for example the app was
     * closed straight after creating the account). Let the user pick their
     * account type and finish the profile instead of being stuck.
     */
    private void resumeRegistration() {

        Toast.makeText(
                this,
                "Your registration wasn't finished. Choose your account type to complete it.",
                Toast.LENGTH_LONG
        ).show();

        Intent intent = new Intent(this, ChooseAccountTypeActivity.class);
        intent.putExtra(RegisterActivity.EXTRA_RESUME, true);
        startActivity(intent);
        resetLoginButton();
    }

    private void resetLoginButton() {

        loginButton.setEnabled(true);

        loginButton.setText(
                "Login"
        );
    }
}