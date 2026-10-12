package com.example.communitystore.vendor;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.communitystore.R;

public class VendorHomeActivity extends AppCompatActivity {

    private ImageView profileButton;

    private TextView viewRewardsButton;
    private TextView moreOrdersButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_vendor_home);

        profileButton = findViewById(R.id.profileButton);
        viewRewardsButton = findViewById(R.id.viewRewardsButton);
        moreOrdersButton = findViewById(R.id.moreOrdersButton);

        setupClicks();
    }

    private void setupClicks() {

        profileButton.setOnClickListener(v ->
                Toast.makeText(
                        VendorHomeActivity.this,
                        "Vendor Profile",
                        Toast.LENGTH_SHORT
                ).show()
        );

        viewRewardsButton.setOnClickListener(v ->
                Toast.makeText(
                        VendorHomeActivity.this,
                        "Vendor rewards will open here.",
                        Toast.LENGTH_SHORT
                ).show()
        );

        moreOrdersButton.setOnClickListener(v ->
                Toast.makeText(
                        VendorHomeActivity.this,
                        "Vendor orders will open here.",
                        Toast.LENGTH_SHORT
                ).show()
        );
    }
}