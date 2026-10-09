package com.example.communitystore.student;

import android.widget.Toast;

import com.example.communitystore.shared.profile.ProfileActivity;

public class StudentProfileActivity extends ProfileActivity {

    @Override
    protected void openMyOrders() {
        Toast.makeText(this, "My Orders", Toast.LENGTH_SHORT).show();
    }
}