package com.example.communitystore.student;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.communitystore.R;
import com.example.communitystore.auth.LogoutActivity;
import com.example.communitystore.core.access.UserRole;
import com.example.communitystore.core.session.SessionManager;
import com.example.communitystore.shared.utils.BottomNavHelper;
import com.example.communitystore.shared.utils.ThemeUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class StudentProfileActivity extends AppCompatActivity {

    private TextView profileName;
    private TextView profileRole;
    private TextView statOrders;
    private TextView statReviews;
    private TextView statRewards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_profile);

        findViewById(R.id.profileRoot).setBackgroundColor(ThemeUtils.primary(this));

        profileName = findViewById(R.id.profileName);
        profileRole = findViewById(R.id.profileRole);
        statOrders = findViewById(R.id.statOrders);
        statReviews = findViewById(R.id.statReviews);
        statRewards = findViewById(R.id.statRewards);

        TextView logOut = findViewById(R.id.logOutButton);
        ThemeUtils.stylePrimaryButton(logOut);
        logOut.setOnClickListener(v -> startActivity(new Intent(this, LogoutActivity.class)));

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
        findViewById(R.id.statRewardsContainer).setOnClickListener(v ->
                startActivity(new Intent(this, StudentLeaderboardActivity.class)));

        setupRoleLabel();
        buildMenu();
        loadProfile();

        BottomNavHelper.setup(this, "profile");
        connectCommunity();
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavHelper.setup(this, "profile");
        connectCommunity();
    }

    protected void openMyOrders() {
        startActivity(new Intent(this, StudentMyOrdersActivity.class));
    }

    private void setupRoleLabel() {
        UserRole role = new SessionManager(this).getUserRole();

        if (role != null) {
            String value = role.getValue();
            profileRole.setText(value.substring(0, 1).toUpperCase() + value.substring(1));
        }
    }

    private void buildMenu() {
        LinearLayout container = findViewById(R.id.menuContainer);

        String[] items = {
                "My Orders",
                "Authentication",
                "Payment Options",
                "Delivery Address",
                "Settings",
                "Help and Support"
        };

        for (String label : items) {
            View row = getLayoutInflater().inflate(R.layout.student_item_profile_menu, container, false);
            ((TextView) row.findViewById(R.id.menuLabel)).setText(label);


            row.setOnClickListener(v -> {
                if ("My Orders".equals(label)) {
                    openMyOrders();
                } else {
                    Toast.makeText(this, label, Toast.LENGTH_SHORT).show();
                }
            });

            container.addView(row);
        }
    }

    private void loadProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            return;
        }

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(document -> {
                    String name = document.getString("displayName");

                    if (name == null || name.trim().isEmpty()) {
                        name = document.getString("fullName");
                    }

                    if (name != null && !name.trim().isEmpty()) {
                        profileName.setText(name);
                    }

                    setStat(statOrders, document.getLong("orderCount"));
                    setStat(statReviews, document.getLong("reviewCount"));
                    setStat(statRewards, document.getLong("loyaltyPoints"));
                });
    }

    private void setStat(TextView view, Long value) {
        if (value != null) {
            view.setText(String.valueOf(value));
        }
    }

    private void connectCommunity() {
        findViewById(R.id.navCommunity).setOnClickListener(v ->
                startActivity(new Intent(this, StudentCommunityBoardActivity.class)));
    }
}
