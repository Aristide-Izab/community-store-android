package com.example.communitystore.auth;

import com.example.communitystore.R;
import com.example.communitystore.core.session.AuthSession;
import com.example.communitystore.shared.utils.ThemeUtils;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

/** Logout confirmation used by every account type. */
public class LogoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_logout);

        Button logoutButton = findViewById(R.id.logoutButton);
        Button cancelButton = findViewById(R.id.cancelButton);

        logoutButton.setBackgroundTintList(ColorStateList.valueOf(ThemeUtils.primary(this)));

        logoutButton.setOnClickListener(v -> AuthSession.logoutToWelcome(this));
        cancelButton.setOnClickListener(v -> finish());
    }
}
