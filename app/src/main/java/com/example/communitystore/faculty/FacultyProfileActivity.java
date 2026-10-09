package com.example.communitystore.faculty;

import android.widget.Toast;

import com.example.communitystore.shared.profile.ProfileActivity;

public class FacultyProfileActivity extends ProfileActivity {

    @Override
    protected void openMyOrders() {
        Toast.makeText(this, "My Orders", Toast.LENGTH_SHORT).show();
    }
}