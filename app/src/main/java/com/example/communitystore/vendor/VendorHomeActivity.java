package com.example.communitystore.vendor;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.communitystore.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class VendorHomeActivity extends AppCompatActivity {

    private TextView greetingText;
    private TextView productCountText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_home);

        greetingText = findViewById(R.id.greetingText);
        productCountText = findViewById(R.id.productCountText);

        findViewById(R.id.profileButton).setOnClickListener(v ->
                startActivity(new Intent(this, VendorProfileActivity.class))
        );
        findViewById(R.id.viewRewardsButton).setOnClickListener(v ->
                startActivity(new Intent(this, VendorLeaderboardActivity.class))
        );
        findViewById(R.id.productsStatCard).setOnClickListener(v ->
                startActivity(new Intent(this, VendorManageProductsActivity.class))
        );
        findViewById(R.id.incomeStatCard).setOnClickListener(v ->
                startActivity(new Intent(this, VendorIncomeActivity.class))
        );
        findViewById(R.id.messagesQuickCard).setOnClickListener(v ->
                startActivity(new Intent(this, VendorMessagesActivity.class))
        );
        findViewById(R.id.leaderboardQuickCard).setOnClickListener(v ->
                startActivity(new Intent(this, VendorLeaderboardActivity.class))
        );
        findViewById(R.id.moreOrdersButton).setOnClickListener(v ->
                startActivity(new Intent(this, VendorIncomeActivity.class))
        );

        loadVendorName();
    }

    @Override
    protected void onResume() {
        super.onResume();
        productCountText.setText(String.valueOf(VendorProductStore.countActive(this)));
    }

    private void loadVendorName() {
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
                        name = "Vendor";
                    }
                    greetingText.setText("Good morning, " + firstName(name));
                });
    }

    private String firstName(String name) {
        String trimmed = name.trim();
        int space = trimmed.indexOf(' ');
        return space > 0 ? trimmed.substring(0, space) : trimmed;
    }
}
