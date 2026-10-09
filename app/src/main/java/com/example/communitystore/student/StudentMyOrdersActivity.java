package com.example.communitystore.student;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.communitystore.R;

public class StudentMyOrdersActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private CardView cardOrder1, cardOrder2, cardOrder3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_my_orders);

        btnBack = findViewById(R.id.btnBack);
        cardOrder1 = findViewById(R.id.cardOrder1);
        cardOrder2 = findViewById(R.id.cardOrder2);
        cardOrder3 = findViewById(R.id.cardOrder3);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Navigate to Order Details for Java Textbook (Processing / Shipped)
        if (cardOrder1 != null) {
            cardOrder1.setOnClickListener(v -> openOrderDetails(
                    "Order #129-94874",
                    "Shipped",
                    "Oct 07 2026",
                    "Java First Edition",
                    "R250",
                    R.drawable.textbook_java_2
            ));
        }

        // Navigate to Order Details for Headphones (Delivered)
        if (cardOrder2 != null) {
            cardOrder2.setOnClickListener(v -> openOrderDetails(
                    "Order #129-94875",
                    "Delivered",
                    "Oct 01 2026",
                    "Headphones",
                    "R320",
                    R.drawable.volkano_headphones_1
            ));
        }

        // Navigate to Order Details for Gaming Chair (Delivered)
        if (cardOrder3 != null) {
            cardOrder3.setOnClickListener(v -> openOrderDetails(
                    "Order #129-94876",
                    "Delivered",
                    "Sep 28 2026",
                    "Gaming Chair",
                    "R1800",
                    R.drawable.game_chair_2
            ));
        }
    }

    private void openOrderDetails(String orderNum, String status, String estDelivery, String productName, String productPrice, int imageResId) {
        Intent intent = new Intent(StudentMyOrdersActivity.this, StudentOrderDetailsActivity.class);
        intent.putExtra("order_number", orderNum);
        intent.putExtra("status", status);
        intent.putExtra("estimated_delivery", estDelivery);
        intent.putExtra("product_name", productName);
        intent.putExtra("product_price", productPrice);
        intent.putExtra("image_res_id", imageResId);
        startActivity(intent);
    }
}