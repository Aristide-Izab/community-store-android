package com.example.communitystore.vendor;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.communitystore.R;
import com.example.communitystore.shared.cart.CartManager;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class VendorManageProductsActivity extends AppCompatActivity {

    private static final int ORANGE = Color.parseColor("#FFA726");
    private static final int GREEN_BG = Color.parseColor("#D1E7DD");
    private static final int GREEN_TEXT = Color.parseColor("#0F5132");
    private static final int GRAY_TEXT = Color.parseColor("#64748B");

    private TextView activeTab;
    private TextView inactiveTab;
    private View activeIndicator;
    private View inactiveIndicator;
    private LinearLayout productListContainer;
    private TextView emptyStateText;

    private boolean showingActive = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_manage_products);

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
        findViewById(R.id.profileButton).setOnClickListener(v ->
                startActivity(new Intent(this, VendorProfileActivity.class))
        );

        activeTab = findViewById(R.id.activeTab);
        inactiveTab = findViewById(R.id.inactiveTab);
        activeIndicator = findViewById(R.id.activeIndicator);
        inactiveIndicator = findViewById(R.id.inactiveIndicator);
        productListContainer = findViewById(R.id.productListContainer);
        emptyStateText = findViewById(R.id.emptyStateText);

        activeTab.setOnClickListener(v -> {
            showingActive = true;
            renderProducts();
        });

        inactiveTab.setOnClickListener(v -> {
            showingActive = false;
            renderProducts();
        });

        findViewById(R.id.addProductButton).setOnClickListener(v ->
                startActivity(new Intent(this, VendorAddProductActivity.class))
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        renderProducts();
    }

    private void renderProducts() {
        updateTabs();
        productListContainer.removeAllViews();

        List<VendorProduct> products = VendorProductStore.getProducts(this);
        int visibleCount = 0;

        for (VendorProduct product : products) {
            if (product.isActive() != showingActive) {
                continue;
            }

            productListContainer.addView(createProductRow(product));
            visibleCount++;
        }

        emptyStateText.setVisibility(visibleCount == 0 ? View.VISIBLE : View.GONE);
        emptyStateText.setText(
                showingActive
                        ? "No active products yet.\nAdd your first product below."
                        : "No inactive products."
        );
    }

    private void updateTabs() {
        activeTab.setTextColor(showingActive ? Color.BLACK : GRAY_TEXT);
        inactiveTab.setTextColor(showingActive ? GRAY_TEXT : Color.BLACK);
        activeIndicator.setVisibility(showingActive ? View.VISIBLE : View.INVISIBLE);
        inactiveIndicator.setVisibility(showingActive ? View.INVISIBLE : View.VISIBLE);
    }

    private View createProductRow(VendorProduct product) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(0, dp(14), 0, dp(14));

        TextView icon = new TextView(this);
        icon.setText("▣");
        icon.setTextSize(24);
        icon.setTextColor(GRAY_TEXT);
        icon.setGravity(Gravity.CENTER);
        GradientDrawable iconBackground = new GradientDrawable();
        iconBackground.setColor(Color.parseColor("#E2E8F0"));
        iconBackground.setCornerRadius(dp(8));
        icon.setBackground(iconBackground);
        row.addView(icon, new LinearLayout.LayoutParams(dp(56), dp(56)));

        LinearLayout details = new LinearLayout(this);
        details.setOrientation(LinearLayout.VERTICAL);
        details.setPadding(dp(14), 0, dp(8), 0);

        TextView title = new TextView(this);
        title.setText(product.getTitle());
        title.setTextColor(Color.BLACK);
        title.setTextSize(14);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        details.addView(title);

        TextView price = new TextView(this);
        price.setText(formatPrice(product.getPrice()));
        price.setTextColor(Color.DKGRAY);
        price.setTextSize(14);
        price.setPadding(0, dp(4), 0, 0);
        details.addView(price);

        LinearLayout.LayoutParams detailsParams =
                new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        row.addView(details, detailsParams);

        LinearLayout actions = new LinearLayout(this);
        actions.setOrientation(LinearLayout.VERTICAL);
        actions.setGravity(Gravity.END);

        Button cartButton = new Button(this);
        cartButton.setText("Add to Cart");
        cartButton.setTextSize(11);
        cartButton.setTextColor(Color.WHITE);
        cartButton.setAllCaps(false);
        cartButton.setBackgroundTintList(ColorStateList.valueOf(ORANGE));
        cartButton.setMinHeight(0);
        cartButton.setMinimumHeight(0);
        cartButton.setPadding(dp(10), 0, dp(10), 0);
        cartButton.setOnClickListener(v -> {
            new CartManager(this).addItem(
                    product.getTitle(),
                    product.getPrice(),
                    0
            );
            Toast.makeText(
                    this,
                    "Added '" + product.getTitle() + "' to cart",
                    Toast.LENGTH_SHORT
            ).show();
        });
        actions.addView(cartButton, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                dp(36)
        ));

        TextView status = new TextView(this);
        status.setText(product.getStatus());
        status.setTextSize(11);
        status.setTextColor(product.isActive() ? GREEN_TEXT : GRAY_TEXT);
        status.setGravity(Gravity.CENTER);
        status.setPadding(dp(12), dp(5), dp(12), dp(5));
        GradientDrawable statusBackground = new GradientDrawable();
        statusBackground.setColor(product.isActive() ? GREEN_BG : Color.parseColor("#E2E8F0"));
        statusBackground.setCornerRadius(dp(16));
        status.setBackground(statusBackground);
        LinearLayout.LayoutParams statusParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        statusParams.topMargin = dp(6);
        actions.addView(status, statusParams);

        row.addView(actions);

        View divider = new View(this);
        divider.setBackgroundColor(Color.parseColor("#E5E7EB"));
        LinearLayout wrapper = new LinearLayout(this);
        wrapper.setOrientation(LinearLayout.VERTICAL);
        wrapper.addView(row);
        wrapper.addView(divider, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(1)
        ));

        return wrapper;
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private String formatPrice(int value) {
        return "R" + NumberFormat.getIntegerInstance(Locale.US).format(value);
    }
}
