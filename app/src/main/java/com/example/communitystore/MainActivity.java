package com.example.communitystore;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.communitystore.auth.LogoutActivity;

public class MainActivity extends AppCompatActivity {

    private Button openLogoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        openLogoutButton = findViewById(R.id.openLogoutButton);

        openLogoutButton.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    LogoutActivity.class
            );

            startActivity(intent);
        });
    }
}