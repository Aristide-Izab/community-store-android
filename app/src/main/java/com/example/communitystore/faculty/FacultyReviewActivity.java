package com.example.communitystore.faculty;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.communitystore.R;
import com.google.android.material.button.MaterialButton;

public class FacultyReviewActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private ImageView imgProduct;
    private TextView tvProductName, tvReviewGuidelines;
    private RatingBar ratingBar;
    private EditText etReviewText;
    private CardView btnUploadMedia;
    private MaterialButton btnCancel, btnSubmitReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.faculty_review);

        initializeViews();
        getIntentExtras();
        setupListeners();
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        imgProduct = findViewById(R.id.imgProduct);
        tvProductName = findViewById(R.id.tvProductName);
        ratingBar = findViewById(R.id.ratingBar);
        etReviewText = findViewById(R.id.etReviewText);
        btnUploadMedia = findViewById(R.id.btnUploadMedia);
        btnCancel = findViewById(R.id.btnCancel);
        btnSubmitReview = findViewById(R.id.btnSubmitReview);
        tvReviewGuidelines = findViewById(R.id.tvReviewGuidelines);
    }

    private void getIntentExtras() {
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("product_name")) {
                tvProductName.setText(intent.getStringExtra("product_name"));
            }
            if (intent.hasExtra("image_res_id")) {
                imgProduct.setImageResource(intent.getIntExtra("image_res_id", R.drawable.textbook_java_2));
            }
        }
    }

    private void setupListeners() {
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());
        if (btnCancel != null) btnCancel.setOnClickListener(v -> finish());

        if (btnUploadMedia != null) {
            btnUploadMedia.setOnClickListener(v ->
                    Toast.makeText(this, "Select image/video to upload", Toast.LENGTH_SHORT).show()
            );
        }

        if (tvReviewGuidelines != null) {
            tvReviewGuidelines.setOnClickListener(v ->
                    Toast.makeText(this, "Keep reviews respectful and accurate.", Toast.LENGTH_LONG).show()
            );
        }

        if (btnSubmitReview != null) {
            btnSubmitReview.setOnClickListener(v -> {
                if (ratingBar.getRating() == 0) {
                    Toast.makeText(this, "Please select a star rating", Toast.LENGTH_SHORT).show();

                } else {
                    showSuccessDialog();
                }
            });
        }
    }

    private void showSuccessDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.faculty_review_success);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.setCancelable(false);

        MaterialButton btnContinue = dialog.findViewById(R.id.btnContinue);
        if (btnContinue != null) {
            btnContinue.setOnClickListener(v -> {
                dialog.dismiss();
                finish();
            });
        }

        dialog.show();
    }
}