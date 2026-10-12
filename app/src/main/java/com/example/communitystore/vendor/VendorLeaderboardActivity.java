package com.example.communitystore.vendor;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.communitystore.R;

public class VendorLeaderboardActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_leaderboard);

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
        findViewById(R.id.salesRankCard).setOnClickListener(v ->
                startActivity(new Intent(this, VendorSalesRankActivity.class))
        );
    }
}
