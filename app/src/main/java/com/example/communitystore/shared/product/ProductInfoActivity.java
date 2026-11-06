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
import com.example.communitystore.core.session.AuthSession;
import com.example.communitystore.shared.cart.CartActivity;
import com.example.communitystore.shared.cart.CartManager;
import com.example.communitystore.shared.chat.ChatActivity;
import com.example.communitystore.shared.checkout.CheckoutActivity;
import com.example.communitystore.shared.data.Formatters;
import com.example.communitystore.shared.data.Product;
import com.example.communitystore.shared.data.UserDirectory;
import com.example.communitystore.shared.seller.SellerProfileActivity;
import com.example.communitystore.shared.utils.ThemeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Listing details: photos, price, condition, seller, and actions
 * (add to cart, buy now, message seller). Works for Firestore listings
 * and built-in sample listings. Start with {@link #open}.
 */
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
    private TextView productMeta;
    private TextView sellerName;
    private TextView sellerInitial;
    private TextView sellerBadge;
    private TextView sellerRating;
    private TextView messageSellerButton;

    private boolean favourite = false;
    private int themeColor;
    private int[] currentProductImages;

    private Product product;

    public static void open(android.content.Context context, Product product) {
        Intent intent = new Intent(context, ProductInfoActivity.class);
        product.putInto(intent);
        context.startActivity(intent);
    }

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
        productMeta = findViewById(R.id.productMeta);
        sellerName = findViewById(R.id.sellerName);
        sellerInitial = findViewById(R.id.sellerInitial);
        sellerBadge = findViewById(R.id.sellerBadge);
        sellerRating = findViewById(R.id.sellerRating);
        messageSellerButton = findViewById(R.id.messageSellerButton);

        product = Product.fromIntent(getIntent());

        applyAccountTheme();
        bindProduct();
        bindSeller();
        setFavouriteStyle(false);

        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        favouriteButton.setOnClickListener(v -> {
            favourite = !favourite;
            setFavouriteStyle(favourite);
            Toast.makeText(
                    this,
                    favourite ? "Saved to favourites" : "Removed from favourites",
                    Toast.LENGTH_SHORT
            ).show();
        });

        addToCartButton.setOnClickListener(v -> addCurrentProductToCart());
        buyNowButton.setOnClickListener(v -> buyCurrentProductNow());
        messageSellerButton.setOnClickListener(v -> messageSeller());

        findViewById(R.id.sellerRow).setOnClickListener(v -> SellerProfileActivity.open(
                this,
                product.sellerId,
                product.sellerName,
                product.sellerRole
        ));
    }

    private boolean isOwnListing() {
        String uid = AuthSession.uid();
        return uid != null && uid.equals(product.sellerId);
    }

    private void setFavouriteStyle(boolean liked) {
        ViewCompat.setBackgroundTintList(
                favouriteButton,
                ColorStateList.valueOf(liked ? Color.parseColor("#E53935") : Color.WHITE)
        );
        favouriteButton.setColorFilter(liked ? Color.WHITE : Color.parseColor("#111111"));
    }

    private void bindProduct() {
        currentProductImages = product.imagesOrPlaceholder();

        productTitle.setText(product.title);
        productPrice.setText(product.priceText());
        productDescription.setText(product.description.isEmpty()
                ? "No description provided."
                : product.description);

        List<String> meta = new ArrayList<>();
        if (!product.condition.isEmpty()) {
            meta.add(product.condition);
        }
        if (!product.category.isEmpty()) {
            meta.add(product.category);
        }
        if (!product.location.isEmpty()) {
            meta.add(product.location);
        }
        if (product.sample) {
            meta.add("Sample listing");
        }

        if (meta.isEmpty()) {
            productMeta.setVisibility(View.GONE);
        } else {
            productMeta.setText(android.text.TextUtils.join(" • ", meta));
        }

        if (isOwnListing()) {
            messageSellerButton.setText("This is your listing");
            messageSellerButton.setEnabled(false);
            addToCartButton.setAlpha(0.4f);
            buyNowButton.setAlpha(0.4f);
        }

        setupImagePager();
    }

    private void bindSeller() {
        String name = product.sellerName.isEmpty() ? "Community seller" : product.sellerName;
        sellerName.setText(name);
        sellerInitial.setText(Formatters.initial(name));

        UserRole role = UserRole.fromString(product.sellerRole);
        String roleLabel = role == null ? "Seller" : role.getLabel();
        sellerBadge.setText(role != null && role.requiresDocuments()
                ? "Verified " + roleLabel
                : roleLabel + " seller");

        ViewCompat.setBackgroundTintList(
                sellerInitial,
                ColorStateList.valueOf(ThemeUtils.colorFor(this, role))
        );

        if (product.sample) {
            sellerRating.setText("Sample seller");
            return;
        }

        sellerRating.setText("Loading reviews...");
        UserDirectory.sellerRating(product.sellerId, (average, count) ->
                sellerRating.setText(UserDirectory.ratingText(average, count)));
    }

    private void setupImagePager() {
        productImagePager.setAdapter(new ProductImagePagerAdapter(currentProductImages));
        buildDots(0);
        updateImageCounter(0);

        productImagePager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                buildDots(position);
                updateImageCounter(position);
            }
        });
    }

    private void buildDots(int selectedPosition) {
        productDotsContainer.removeAllViews();

        for (int i = 0; i < currentProductImages.length; i++) {
            View dot = new View(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dp(8), dp(8));

            if (i > 0) {
                params.setMarginStart(dp(9));
            }

            dot.setLayoutParams(params);
            dot.setBackgroundResource(i == selectedPosition
                    ? R.drawable.bg_product_dot_active
                    : R.drawable.bg_product_dot_inactive);

            if (i == selectedPosition) {
                ViewCompat.setBackgroundTintList(dot, ColorStateList.valueOf(themeColor));
            }

            productDotsContainer.addView(dot);
        }
    }

    private void updateImageCounter(int position) {
        productImageCounter.setText((position + 1) + "/" + currentProductImages.length);
    }

    private boolean canBuy() {
        if (isOwnListing()) {
            Toast.makeText(this, "You can't buy your own listing.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!product.hasFixedPrice()) {
            Toast.makeText(
                    this,
                    "This listing has no fixed price. Message the seller to agree on one.",
                    Toast.LENGTH_LONG
            ).show();
            return false;
        }

        return true;
    }

    private void addCurrentProductToCart() {
        if (!canBuy()) {
            return;
        }

        new CartManager(this).addProduct(product);
        Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, CartActivity.class));
    }

    private void buyCurrentProductNow() {
        if (!canBuy()) {
            return;
        }

        // Buy Now = put this item in the cart and go straight to checkout.
        new CartManager(this).addProduct(product);
        startActivity(new Intent(this, CheckoutActivity.class));
    }

    private void messageSeller() {
        ChatActivity.open(
                this,
                product.sellerId,
                product.sellerName,
                product.sellerRole,
                product.title,
                "Hi! Is \"" + product.title + "\" still available?"
        );
    }

    private void applyAccountTheme() {
        themeColor = ThemeUtils.primary(this);

        ViewCompat.setBackgroundTintList(productHeader, ColorStateList.valueOf(themeColor));
        ViewCompat.setBackgroundTintList(addToCartButton, ColorStateList.valueOf(themeColor));
        ViewCompat.setBackgroundTintList(buyNowButton, ColorStateList.valueOf(themeColor));

        productPrice.setTextColor(themeColor);
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
