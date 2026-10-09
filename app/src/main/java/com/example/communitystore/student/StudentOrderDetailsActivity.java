package com.example.communitystore.student;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.communitystore.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

public class StudentOrderDetailsActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView tvOrderNumber, tvStatusHeader, tvEstimatedDelivery, tvProductName, tvProductPrice;
    private ImageView imgProduct;
    private MaterialButton btnLeaveReview, btnContactSeller;

    private String productName = "Java First Edition";
    private int imageResId = R.drawable.textbook_java_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_order_page);

        initializeViews();
        getIntentExtras();
        setupListeners();
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        tvOrderNumber = findViewById(R.id.tvOrderNumber);
        tvStatusHeader = findViewById(R.id.tvStatusHeader);
        tvEstimatedDelivery = findViewById(R.id.tvEstimatedDelivery);
        tvProductName = findViewById(R.id.tvProductName);
        tvProductPrice = findViewById(R.id.tvProductPrice);
        imgProduct = findViewById(R.id.imgProduct);
        btnLeaveReview = findViewById(R.id.btnLeaveReview);
        btnContactSeller = findViewById(R.id.btnContactSeller);
    }

    private void getIntentExtras() {
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("order_number")) {
                tvOrderNumber.setText(intent.getStringExtra("order_number"));
            }
            if (intent.hasExtra("status")) {
                tvStatusHeader.setText(intent.getStringExtra("status"));
            }
            if (intent.hasExtra("estimated_delivery")) {
                tvEstimatedDelivery.setText(intent.getStringExtra("estimated_delivery"));
            }
            if (intent.hasExtra("product_name")) {
                productName = intent.getStringExtra("product_name");
                tvProductName.setText(productName);
            }
            if (intent.hasExtra("product_price")) {
                tvProductPrice.setText(intent.getStringExtra("product_price"));
            }
            if (intent.hasExtra("image_res_id")) {
                imageResId = intent.getIntExtra("image_res_id", R.drawable.textbook_java_2);
                imgProduct.setImageResource(imageResId);
            }
        }
    }

    private void setupListeners() {
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        if (btnLeaveReview != null) {
            btnLeaveReview.setOnClickListener(v -> {
                Intent reviewIntent = new Intent(StudentOrderDetailsActivity.this, StudentReviewActivity.class);
                reviewIntent.putExtra("product_name", productName);
                reviewIntent.putExtra("image_res_id", imageResId);
                startActivity(reviewIntent);
            });
        }

        if (btnContactSeller != null) {
            btnContactSeller.setOnClickListener(v -> showContactSellerBottomSheet());
        }
    }

    private void showContactSellerBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.contact_seller_student, null);
        bottomSheetDialog.setContentView(dialogView);

        EditText etCallbackNote = dialogView.findViewById(R.id.etCallbackNote);
        MaterialButton btnDismiss = dialogView.findViewById(R.id.btnDismissDialog);
        MaterialButton btnSend = dialogView.findViewById(R.id.btnSendCallbackRequest);

        if (btnDismiss != null) {
            btnDismiss.setOnClickListener(v -> bottomSheetDialog.dismiss());
        }

        if (btnSend != null) {
            btnSend.setOnClickListener(v -> {
                String note = etCallbackNote != null ? etCallbackNote.getText().toString().trim() : "";
                Toast.makeText(this, "Callback requested! Seller will contact you soon.", Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
            });
        }

        bottomSheetDialog.show();
    }
}