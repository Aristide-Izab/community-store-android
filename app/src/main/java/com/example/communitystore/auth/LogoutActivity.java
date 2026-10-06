package com.example.communitystore.auth;

import com.example.communitystore.R;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LogoutActivity extends AppCompatActivity {

    private Button logoutButton;
    private Button cancelButton;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);

        auth = FirebaseAuth.getInstance();

        logoutButton = findViewById(R.id.logoutButton);
        cancelButton = findViewById(R.id.cancelButton);

        logoutButton.setOnClickListener(v -> logoutUser());

        cancelButton.setOnClickListener(v -> finish());
    }

    private void logoutUser() {

        auth.signOut();

        Intent intent = new Intent(
                LogoutActivity.this,
                WelcomeActivity.class
        );

        intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);
        finish();
    }
}
