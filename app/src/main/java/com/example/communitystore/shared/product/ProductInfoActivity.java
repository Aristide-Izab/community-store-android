package com.example.communitystore.shared.product;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.communitystore.R;
import com.example.communitystore.core.access.UserRole;
import com.example.communitystore.core.session.SessionManager;
import com.example.communitystore.shared.cart.CartActivity;
import com.example.communitystore.shared.cart.CartManager;
import com.example.communitystore.shared.checkout.CheckoutActivity;
import com.example.communitystore.shared.seller.SellerProfileActivity;

public class ProductInfoActivity extends AppCompatActivity {

    private LinearLayout productHeader;
    private LinearLayout addToCartButton;
    private LinearLayout buyNowButton;
    private LinearLayout productDotsContainer;

    private ViewPager2 productImagePager;
    private ImageView favouriteButton;
    private TextView productTitle;
    private TextView productPrice;
    private TextView productDescription;
    private TextView productImageCounter;

    private boolean favourite = false;
    private int themeColor;
    private int[] currentProductImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_info);

        productHeader = findViewById(R.id.productHeader);
        addToCartButton = findViewById(R.id.addToCartButton);
        buyNowButton = findViewById(R.id.buyNowButton);
        productDotsContainer = findViewById(R.id.productDotsContainer);
        productImagePager = findViewById(R.id.productImagePager);
        favouriteButton = findViewById(R.id.favouriteButton);
        productTitle = findViewById(R.id.productTitle);
        productPrice = findViewById(R.id.productPrice);
        productDescription = findViewById(R.id.productDescription);
        productImageCounter = findViewById(R.id.productImageCounter);

        applyAccountTheme();
        loadProduct();
        setFavouriteStyle(false);

        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        favouriteButton.setOnClickListener(v -> {
            favourite = !favourite;
            setFavouriteStyle(favourite);
        });

        addToCartButton.setOnClickListener(v -> addCurrentProductToCart());
        buyNowButton.setOnClickListener(v -> buyCurrentProductNow());

        findViewById(R.id.sellerRow).setOnClickListener(v ->
                startActivity(new Intent(this, SellerProfileActivity.class))
        );
    }

    private void setFavouriteStyle(boolean liked) {
        ViewCompat.setBackgroundTintList(
                favouriteButton,
                ColorStateList.valueOf(
                        liked ? Color.parseColor("#E53935") : Color.WHITE
                )
        );

        favouriteButton.setColorFilter(
                liked ? Color.WHITE : Color.parseColor("#111111")
        );
    }

    private void loadProduct() {
        String title = getIntent().getStringExtra("productTitle");
        String price = getIntent().getStringExtra("productPrice");
        String description = getIntent().getStringExtra("productDescription");

        int fallbackImage = getIntent().getIntExtra(
                "productImage",
                R.drawable.dell_laptop_1
        );

        currentProductImages = getIntent().getIntArrayExtra("productImages");
        if (currentProductImages == null || currentProductImages.length == 0) {
            currentProductImages = getDefaultProductImages(title, fallbackImage);
        }

        if (title != null && !title.trim().isEmpty()) {
            productTitle.setText(title);
        }

        if (price != null && !price.trim().isEmpty()) {
            productPrice.setText(price);
        }

        if (description != null && !description.trim().isEmpty()) {
            productDescription.setText(description);
        }

        setupImagePager();
    }

    private int[] getDefaultProductImages(String title, int fallbackImage) {
        if (title == null) {
            return new int[]{fallbackImage};
        }

        String normalizedTitle = title.trim().toLowerCase();

        if (normalizedTitle.contains("iphone")) {
            return new int[]{R.drawable.iphone_15_image_1, R.drawable.iphone_15_image_2};
        }

        if (normalizedTitle.contains("textbook") || normalizedTitle.contains("java first edition")) {
            return new int[]{R.drawable.textbook_java_1, R.drawable.textbook_java_2};
        }

        return new int[]{fallbackImage};
    }

    private void setupImagePager() {
        productImagePager.setAdapter(new ProductImagePagerAdapter(currentProductImages));
        buildDots(0);
        updateImageCounter(0);

        productImagePager.registerOnPageChangeCallback(
                new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        super.onPageSelected(position);
                        buildDots(position);
                        updateImageCounter(position);
                    }
                }
        );
    }

    private void buildDots(int selectedPosition) {
        productDotsContainer.removeAllViews();

        for (int i = 0; i < currentProductImages.length; i++) {
            View dot = new View(this);

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(dp(8), dp(8));

            if (i > 0) {
                params.setMarginStart(dp(9));
            }

            dot.setLayoutParams(params);
            dot.setBackgroundResource(
                    i == selectedPosition
                            ? R.drawable.bg_product_dot_active
                            : R.drawable.bg_product_dot_inactive
            );

            if (i == selectedPosition) {
                ViewCompat.setBackgroundTintList(
                        dot,
                        ColorStateList.valueOf(themeColor)
                );
            }

            productDotsContainer.addView(dot);
        }
    }

    private void updateImageCounter(int position) {
        productImageCounter.setText(
                (position + 1) + "/" + currentProductImages.length
        );
    }

    private void addCurrentProductToCart() {
        int price = parsePrice(productPrice.getText().toString());

        if (price <= 0) {
            Toast.makeText(
                    this,
                    "This product requires contacting the seller for pricing",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        CartManager cartManager = new CartManager(this);

        cartManager.addItem(
                productTitle.getText().toString(),
                price,
                currentProductImages[0]
        );

        startActivity(
                new Intent(this, CartActivity.class)
        );
    }

    private void buyCurrentProductNow() {
        int price = parsePrice(productPrice.getText().toString());

        if (price <= 0) {
            Toast.makeText(
                    this,
                    "This product requires contacting the seller for pricing",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        Intent intent = new Intent(this, CheckoutActivity.class);
        intent.putExtra("subtotalAmount", price);
        intent.putExtra("deliveryAmount", 50);
        intent.putExtra("totalAmount", price + 50);
        startActivity(intent);
    }

    private int parsePrice(String price) {
        try {
            return Integer.parseInt(
                    price.replace("R", "")
                            .replace(",", "")
                            .replace(" ", "")
                            .trim()
            );
        } catch (Exception ignored) {
            return 0;
        }
    }

    private void applyAccountTheme() {
        SessionManager sessionManager = new SessionManager(this);
        themeColor = getThemeColor(sessionManager.getUserRole());

        ViewCompat.setBackgroundTintList(
                productHeader,
                ColorStateList.valueOf(themeColor)
        );

        ViewCompat.setBackgroundTintList(
                addToCartButton,
                ColorStateList.valueOf(themeColor)
        );

        ViewCompat.setBackgroundTintList(
                buyNowButton,
                ColorStateList.valueOf(themeColor)
        );

        productPrice.setTextColor(themeColor);
        getWindow().setStatusBarColor(themeColor);
    }

    private int getThemeColor(UserRole role) {
        if (role == null) {
            return Color.parseColor("#168FC1");
        }

        switch (role) {
            case STUDENT:
                return Color.parseColor("#168FC1");
            case FACULTY:
                return Color.parseColor("#FF4B2B");
            case VENDOR:
                return Color.parseColor("#FFA726");
            case RESIDENCE:
                return Color.parseColor("#4B00D1");
            default:
                return Color.parseColor("#168FC1");
        }
    }

    private int dp(int value) {
        return Math.round(
                value * getResources().getDisplayMetrics().density
        );
    }
}
