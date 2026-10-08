package com.example.communitystore.auth;

import com.example.communitystore.R;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AccountVerifiedActivity extends AppCompatActivity {

    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_auth_account_verified
        );

        loginButton =
                findViewById(R.id.loginButton);

        loginButton.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            AccountVerifiedActivity.this,
                            LoginActivity.class
                    );

            intent.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
                            |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);
            finish();
        });
    }
}