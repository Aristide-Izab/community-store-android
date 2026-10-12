package com.example.communitystore.vendor;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.communitystore.R;

public class VendorIncomeActivity extends AppCompatActivity {

    private TextView totalIncomeValue;
    private TextView totalOrdersValue;
    private TextView pendingPayoutValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_income);

        totalIncomeValue = findViewById(R.id.totalIncomeValue);
        totalOrdersValue = findViewById(R.id.totalOrdersValue);
        pendingPayoutValue = findViewById(R.id.pendingPayoutValue);

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
        findViewById(R.id.weekButton).setOnClickListener(v -> showWeek());
        findViewById(R.id.monthButton).setOnClickListener(v -> showMonth());
        findViewById(R.id.yearButton).setOnClickListener(v -> showYear());

        showMonth();
    }

    private void showWeek() {
        totalIncomeValue.setText("R6,250");
        totalOrdersValue.setText("32");
        pendingPayoutValue.setText("R120");
    }

    private void showMonth() {
        totalIncomeValue.setText("R24,750");
        totalOrdersValue.setText("128");
        pendingPayoutValue.setText("R340");
    }

    private void showYear() {
        totalIncomeValue.setText("R184,300");
        totalOrdersValue.setText("947");
        pendingPayoutValue.setText("R340");
    }
}
