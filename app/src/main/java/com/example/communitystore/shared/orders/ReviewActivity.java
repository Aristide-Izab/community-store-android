package com.example.communitystore.shared.orders;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.communitystore.R;
import com.example.communitystore.core.access.UserRole;
import com.example.communitystore.core.session.SessionManager;
import com.example.communitystore.shared.data.Order;
import com.example.communitystore.shared.data.OrderRepository;
import com.example.communitystore.shared.utils.ThemeUtils;
import com.google.android.material.button.MaterialButton;

/** Review a delivered order. Saved to "reviews/{orderId}"; feeds the seller's rating. */
public class ReviewActivity extends AppCompatActivity {

    private static final String EXTRA_ORDER_ID = "orderId";
    private static final String EXTRA_SELLER_ID = "sellerId";
    private static final String EXTRA_TITLE = "productTitle";
    private static final String EXTRA_IMAGE = "image";

    private RatingBar ratingBar;
    private EditText etReviewText;
    private MaterialButton btnSubmitReview;

    public static void open(Context context, Order order) {
        Intent intent = new Intent(context, ReviewActivity.class);
        intent.putExtra(EXTRA_ORDER_ID, order.id);
        intent.putExtra(EXTRA_SELLER_ID, order.sellerId);
        intent.putExtra(EXTRA_TITLE, order.firstTitle());
        intent.putExtra(EXTRA_IMAGE, OrdersActivity.imageFor(order));
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        int color = ThemeUtils.primary(this);
        findViewById(R.id.reviewRoot).setBackgroundColor(color);
        findViewById(R.id.topAppBar).setBackgroundColor(color);

        ImageView imgProduct = findViewById(R.id.imgProduct);
        TextView tvProductName = findViewById(R.id.tvProductName);
        TextView tvReviewGuidelines = findViewById(R.id.tvReviewGuidelines);
        ratingBar = findViewById(R.id.ratingBar);
        etReviewText = findViewById(R.id.etReviewText);
        btnSubmitReview = findViewById(R.id.btnSubmitReview);
        MaterialButton btnCancel = findViewById(R.id.btnCancel);
        View btnUploadMedia = findViewById(R.id.btnUploadMedia);

        tvProductName.setText(getIntent().getStringExtra(EXTRA_TITLE));
        imgProduct.setImageResource(getIntent().getIntExtra(EXTRA_IMAGE, R.drawable.ic_product_placeholder));

        btnSubmitReview.setBackgroundTintList(ColorStateList.valueOf(color));
        btnCancel.setTextColor(color);
        btnCancel.setStrokeColor(ColorStateList.valueOf(color));
        tvReviewGuidelines.setTextColor(color);
        ratingBar.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#FFB300")));

        // Photo/video reviews need Firebase Storage (paid plan), so they're hidden.
        if (btnUploadMedia != null) {
            btnUploadMedia.setVisibility(View.GONE);
        }

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        btnCancel.setOnClickListener(v -> finish());
        tvReviewGuidelines.setOnClickListener(v -> Toast.makeText(
                this,
                "Keep reviews respectful, honest and about the item or service.",
                Toast.LENGTH_LONG
        ).show());

        btnSubmitReview.setOnClickListener(v -> submit());
    }

    private void submit() {
        int rating = Math.round(ratingBar.getRating());

        if (rating == 0) {
            Toast.makeText(this, "Please select a star rating", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSubmitReview.setEnabled(false);

        OrderRepository.submitReview(
                        getIntent().getStringExtra(EXTRA_ORDER_ID),
                        getIntent().getStringExtra(EXTRA_SELLER_ID),
                        getIntent().getStringExtra(EXTRA_TITLE),
                        rating,
                        etReviewText.getText().toString().trim(),
                        new SessionManager(this).getDisplayName()
                )
                .addOnSuccessListener(unused -> showSuccessDialog())
                .addOnFailureListener(e -> {
                    btnSubmitReview.setEnabled(true);
                    Toast.makeText(this, "Couldn't save your review: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void showSuccessDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_review_success);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialog.setCancelable(false);

        // Same artwork as the original screens: orange tick for Faculty, blue for Students.
        ImageView successIcon = dialog.findViewById(R.id.imgSuccessIcon);
        if (successIcon != null) {
            UserRole role = new SessionManager(this).getUserRole();
            successIcon.setImageResource(role == UserRole.FACULTY
                    ? R.drawable.ic_success_circle
                    : R.drawable.ic_success_circlef);
            if (role == UserRole.VENDOR || role == UserRole.RESIDENCE) {
                successIcon.setImageTintList(ColorStateList.valueOf(ThemeUtils.primary(this)));
            }
        }

        MaterialButton btnContinue = dialog.findViewById(R.id.btnContinue);
        if (btnContinue != null) {
            btnContinue.setBackgroundTintList(ColorStateList.valueOf(ThemeUtils.primary(this)));
            btnContinue.setOnClickListener(v -> {
                dialog.dismiss();
                finish();
            });
        }

        dialog.show();
    }
}
