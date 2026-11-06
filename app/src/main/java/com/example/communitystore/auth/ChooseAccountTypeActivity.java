package com.example.communitystore.auth;

import com.example.communitystore.R;
import com.example.communitystore.core.session.AuthSession;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;

public class ChooseAccountTypeActivity extends AppCompatActivity {

    private MaterialCardView studentCard;
    private MaterialCardView facultyCard;
    private MaterialCardView vendorCard;
    private MaterialCardView residenceCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_auth_choose_account_type
        );

        studentCard =
                findViewById(R.id.studentCard);

        facultyCard =
                findViewById(R.id.facultyCard);

        vendorCard =
                findViewById(R.id.vendorCard);

        residenceCard =
                findViewById(R.id.residenceCard);

        findViewById(R.id.backButton).setOnClickListener(v -> goBack());

        // System back / gesture behaves the same as the on-screen back button.
        getOnBackPressedDispatcher().addCallback(this, new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                goBack();
            }
        });

        studentCard.setOnClickListener(
                v -> openRegister("student")
        );

        facultyCard.setOnClickListener(
                v -> openRegister("faculty")
        );

        vendorCard.setOnClickListener(
                v -> openRegister("vendor")
        );

        residenceCard.setOnClickListener(
                v -> openRegister("residence")
        );
    }

    /**
     * Normal visit: return to the previous screen (Welcome or Login).
     * "Finish your registration" visit: the user is signed in with an
     * unfinished account, so sign out first and return to Welcome.
     */
    private void goBack() {
        if (getIntent().getBooleanExtra(RegisterActivity.EXTRA_RESUME, false)) {
            AuthSession.logoutToWelcome(this);
        } else {
            finish();
        }
    }

    private void openRegister(String accountType) {

        Intent intent =
                new Intent(
                        ChooseAccountTypeActivity.this,
                        RegisterActivity.class
                );

        intent.putExtra(
                RegisterActivity.EXTRA_ACCOUNT_TYPE,
                accountType
        );

        // Passed on when finishing a registration whose profile was never saved.
        intent.putExtra(
                RegisterActivity.EXTRA_RESUME,
                getIntent().getBooleanExtra(RegisterActivity.EXTRA_RESUME, false)
        );

        startActivity(intent);
    }
}