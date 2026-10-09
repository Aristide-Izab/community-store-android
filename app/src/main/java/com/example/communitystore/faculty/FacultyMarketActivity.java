package com.example.communitystore.faculty;

import android.widget.Toast;

import com.example.communitystore.shared.market.MarketActivity;

public class FacultyMarketActivity extends MarketActivity {

    @Override
    protected void openMyOrders() {
        Toast.makeText(this, "My Orders", Toast.LENGTH_SHORT).show();
    }
}