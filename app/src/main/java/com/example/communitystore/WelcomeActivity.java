package com.example.communitystore;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    private Button getStartedButton;
    private TextView loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);

        getStartedButton =
                findViewById(R.id.getStartedButton);

        loginButton =
                findViewById(R.id.loginButton);

        getStartedButton.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            WelcomeActivity.this,
                            ChooseAccountTypeActivity.class
                    );

            startActivity(intent);
        });

        loginButton.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            WelcomeActivity.this,
                            LoginActivity.class
                    );

            startActivity(intent);
        });
    }
}
