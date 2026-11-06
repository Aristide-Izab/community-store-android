package com.example.communitystore.student;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.communitystore.R;
import com.example.communitystore.auth.LogoutActivity;
import com.example.communitystore.core.access.BottomNavThemeManager;
import com.example.communitystore.core.access.UserRole;
import com.example.communitystore.core.session.AuthSession;
import com.example.communitystore.core.session.SessionManager;
import com.example.communitystore.shared.data.OrderRepository;
import com.example.communitystore.shared.data.UserDirectory;
import com.example.communitystore.shared.orders.OrdersActivity;
import com.example.communitystore.shared.product.MyListingsActivity;
import com.example.communitystore.shared.profile.ProfileActions;
import com.example.communitystore.shared.utils.BottomNavHelper;
import com.example.communitystore.shared.utils.ThemeUtils;
import com.google.firebase.firestore.FirebaseFirestore;

/** Profile for Student accounts (Faculty uses the same screen). */
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
        findViewById(R.id.statRewardsContainer).setOnClickListener(v -> openRewards());

        UserRole role = new SessionManager(this).getUserRole();
        profileRole.setText(role == null ? UserRole.STUDENT.getLabel() : role.getLabel());

        String cached = new SessionManager(this).getDisplayName();
        if (!cached.isEmpty()) {
            profileName.setText(cached);
        }

        buildMenu();
        BottomNavHelper.setup(this, "profile");
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavThemeManager.apply(this, "profile");
        loadProfile();
    }

    private void openRewards() {
        startActivity(new Intent(this, BottomNavHelper.rewardsFor(this)));
    }

    private void buildMenu() {
        LinearLayout container = findViewById(R.id.menuContainer);

        String[] items = {
                "Edit Profile",
                "My Orders",
                "My Listings",
                "My Sales",
                "Rewards",
                "Payment Options",
                "Delivery Address",
                "Settings",
                "Help and Support"
        };

        for (String label : items) {
            View row = getLayoutInflater().inflate(R.layout.student_item_profile_menu, container, false);
            ((TextView) row.findViewById(R.id.menuLabel)).setText(label);
            row.setOnClickListener(v -> onMenu(label));
            container.addView(row);
        }
    }

    private void onMenu(String label) {
        switch (label) {
            case "Edit Profile":
                ProfileActions.editName(this, profileName.getText().toString(), profileName::setText);
                break;
            case "My Orders":
                OrdersActivity.openPurchases(this);
                break;
            case "My Listings":
                MyListingsActivity.open(this);
                break;
            case "My Sales":
                OrdersActivity.openSales(this);
                break;
            case "Rewards":
                openRewards();
                break;
            case "Payment Options":
                ProfileActions.paymentOptions(this);
                break;
            case "Delivery Address":
                ProfileActions.editDeliveryAddress(this);
                break;
            case "Settings":
                ProfileActions.settings(this);
                break;
            default:
                ProfileActions.help(this);
                break;
        }
    }

    private void loadProfile() {
        String uid = AuthSession.uid();

        UserDirectory.loadCurrent(profile -> {
            if (profile == null) {
                return;
            }
            profileName.setText(UserDirectory.displayName(profile, profileName.getText().toString()));
            statRewards.setText(String.valueOf(UserDirectory.points(profile)));
        });

        OrderRepository.loadAsBuyer(uid, (orders, error) ->
                statOrders.setText(String.valueOf(orders.size())));

        if (uid != null) {
            FirebaseFirestore.getInstance()
                    .collection("reviews")
                    .whereEqualTo("buyerId", uid)
                    .get()
                    .addOnSuccessListener(snapshot -> statReviews.setText(String.valueOf(snapshot.size())));
        }
    }
}
