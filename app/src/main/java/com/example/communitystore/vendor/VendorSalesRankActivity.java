package com.example.communitystore.vendor;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.communitystore.R;

public class VendorSalesRankActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_sales_rank);
        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }
}
