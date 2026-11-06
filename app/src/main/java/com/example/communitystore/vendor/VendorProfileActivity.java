package com.example.communitystore.vendor;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.communitystore.R;
import com.example.communitystore.auth.LogoutActivity;
import com.example.communitystore.core.session.AuthSession;
import com.example.communitystore.core.session.SessionManager;
import com.example.communitystore.shared.data.OrderRepository;
import com.example.communitystore.shared.data.UserDirectory;
import com.example.communitystore.shared.orders.OrdersActivity;
import com.example.communitystore.shared.product.MyListingsActivity;
import com.example.communitystore.shared.profile.ProfileActions;
import com.example.communitystore.shared.seller.SellerProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

/** Vendor profile with stats and account menu. */
public class VendorProfileActivity extends AppCompatActivity {

    private TextView nameText;
    private TextView emailText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_profile);

        nameText = findViewById(R.id.nameText);
        emailText = findViewById(R.id.emailText);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        emailText.setText(user == null || user.getEmail() == null ? "" : user.getEmail());

        String cached = new SessionManager(this).getDisplayName();
        if (!cached.isEmpty()) {
            nameText.setText(cached);
        }

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
        findViewById(R.id.salesHubRow).setOnClickListener(v -> OrdersActivity.openSales(this));
        findViewById(R.id.myProductsRow).setOnClickListener(v -> MyListingsActivity.open(this));
        findViewById(R.id.editProfileRow).setOnClickListener(v -> editBusinessName());
        findViewById(R.id.paymentRow).setOnClickListener(v -> ProfileActions.paymentOptions(this));
        findViewById(R.id.rewardsRow).setOnClickListener(v -> showRewardsAndRatings());
        findViewById(R.id.settingsRow).setOnClickListener(v -> ProfileActions.settings(this));
        findViewById(R.id.supportRow).setOnClickListener(v -> showGuidelines());
        findViewById(R.id.logoutButton).setOnClickListener(v ->
                startActivity(new Intent(this, LogoutActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProfile();
    }

    private void loadProfile() {
        String uid = AuthSession.uid();

        UserDirectory.loadCurrent(profile -> {
            if (profile == null) {
                return;
            }
            nameText.setText(UserDirectory.displayName(profile, "Vendor"));
            ((TextView) findViewById(R.id.statRewards)).setText("Rewards\n" + UserDirectory.points(profile));
        });

        OrderRepository.loadAsSeller(uid, (orders, error) ->
                ((TextView) findViewById(R.id.statSales)).setText("Sales\n" + orders.size()));

        UserDirectory.sellerRating(uid, (average, count) ->
                ((TextView) findViewById(R.id.statReviews)).setText("Reviews\n" + count));
    }

    /** Vendors are shown by their business name, saved in users/{uid}.businessName. */
    private void editBusinessName() {
        android.widget.EditText input = new android.widget.EditText(this);
        input.setText(nameText.getText());
        input.setSelection(input.getText().length());

        android.widget.FrameLayout frame = new android.widget.FrameLayout(this);
        int pad = Math.round(20 * getResources().getDisplayMetrics().density);
        frame.setPadding(pad, pad / 2, pad, 0);
        frame.addView(input);

        new AlertDialog.Builder(this)
                .setTitle("Business name")
                .setMessage("Shown to buyers on your listings, orders and chats. Existing listings keep the old name until you edit them.")
                .setView(frame)
                .setPositiveButton("Save", (d, w) -> {
                    String name = input.getText().toString().trim();
                    String uid = AuthSession.uid();
                    if (name.isEmpty() || uid == null) {
                        return;
                    }

                    java.util.Map<String, Object> update = new java.util.HashMap<>();
                    update.put("businessName", name);

                    FirebaseFirestore.getInstance().collection("users").document(uid).update(update);
                    FirebaseFirestore.getInstance().collection("vendors").document(uid).update(update);

                    new SessionManager(this).saveDisplayName(name);
                    nameText.setText(name);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showRewardsAndRatings() {
        String[] options = {"Rewards & perks", "My public store page (ratings)"};

        new AlertDialog.Builder(this)
                .setTitle("Rewards & Ratings")
                .setItems(options, (d, which) -> {
                    if (which == 0) {
                        startActivity(new Intent(this, VendorLeaderboardActivity.class));
                    } else {
                        SellerProfileActivity.open(this, AuthSession.uid(), nameText.getText().toString(), "vendor");
                    }
                })
                .show();
    }

    private void showGuidelines() {
        new AlertDialog.Builder(this)
                .setTitle("Vendor Support & Guidelines")
                .setMessage("• List only items you can supply, with honest descriptions and prices.\n\n"
                        + "• Update each order: Packed → Out for delivery → Delivered. "
                        + "Delivered orders count as income and earn you points.\n\n"
                        + "• Reply to buyer messages and requests promptly.\n\n"
                        + "• Payments in this version are simulated; no money is collected.\n\n"
                        + "• Questions? Use Help and Support on the Student/Faculty app or contact the Community Store team.")
                .setPositiveButton("OK", null)
                .show();
    }
}
