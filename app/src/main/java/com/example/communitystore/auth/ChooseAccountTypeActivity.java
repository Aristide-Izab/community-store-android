package com.example.communitystore.auth;

import com.example.communitystore.R;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;

public class ChooseAccountTypeActivity extends AppCompatActivity {

    private MaterialCardView studentCard;
    private MaterialCardView facultyCard;
    private MaterialCardView vendorCard;
    private MaterialCardView residenceCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_choose_account_type
        );

        studentCard =
                findViewById(R.id.studentCard);

        facultyCard =
                findViewById(R.id.facultyCard);

        vendorCard =
                findViewById(R.id.vendorCard);

        residenceCard =
                findViewById(R.id.residenceCard);

        studentCard.setOnClickListener(
                v -> openRegister("student")
        );

        facultyCard.setOnClickListener(
                v -> openRegister("faculty")
        );

        vendorCard.setOnClickListener(
                v -> openRegister("vendor")
        );

        residenceCard.setOnClickListener(
                v -> openRegister("residence")
        );
    }

    private void openRegister(String accountType) {

        Intent intent =
                new Intent(
                        ChooseAccountTypeActivity.this,
                        RegisterActivity.class
                );

        intent.putExtra(
                "accountType",
                accountType
        );

        startActivity(intent);
    }
}