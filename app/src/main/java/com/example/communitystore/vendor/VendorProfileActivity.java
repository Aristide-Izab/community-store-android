package com.example.communitystore.vendor;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.communitystore.R;
import com.example.communitystore.auth.LoginActivity;
import com.example.communitystore.core.session.SessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class VendorProfileActivity extends AppCompatActivity {

    private TextView nameText;
    private TextView emailText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_profile);

        nameText = findViewById(R.id.nameText);
        emailText = findViewById(R.id.emailText);

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
        findViewById(R.id.salesHubRow).setOnClickListener(v ->
                startActivity(new Intent(this, VendorIncomeActivity.class))
        );
        findViewById(R.id.rewardsRow).setOnClickListener(v ->
                startActivity(new Intent(this, VendorLeaderboardActivity.class))
        );
        findViewById(R.id.logoutButton).setOnClickListener(v -> logout());

        loadProfile();
    }

    private void loadProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }

        emailText.setText(user.getEmail() == null ? "Vendor" : user.getEmail());

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(document -> {
                    String name = document.getString("displayName");
                    if (name == null || name.trim().isEmpty()) {
                        name = "Vendor";
                    }
                    nameText.setText(name);
                })
                .addOnFailureListener(e -> nameText.setText("Vendor"));
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        new SessionManager(this).clearSession();

        Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
