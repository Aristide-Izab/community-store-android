package com.example.communitystore.shared.seller;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import com.example.communitystore.R;
import com.example.communitystore.core.access.UserRole;
import com.example.communitystore.core.session.SessionManager;
import com.example.communitystore.shared.product.ProductInfoActivity;

public class SellerProfileActivity extends AppCompatActivity {

    private LinearLayout sellerHeader;
    private LinearLayout messageButton;
    private LinearLayout followButton;

    private LinearLayout listingsTab;
    private LinearLayout reviewsTab;
    private LinearLayout listingsContainer;
    private LinearLayout reviewsContainer;

    private TextView listingsTabText;
    private TextView reviewsTabText;
    private TextView sellerLaptopPrice;
    private TextView sellerMousePrice;
    private TextView sellerHeadphonesPrice;
    private TextView sellerChairPrice;

    private ImageView backButton;
    private ImageView sellerVerifiedBadge;
    private ImageView sellerFavouriteLaptop;
    private ImageView sellerFavouriteMouse;
    private ImageView sellerFavouriteHeadphones;
    private ImageView sellerFavouriteChair;

    private LinearLayout productLaptop;
    private LinearLayout productMouse;
    private LinearLayout productHeadphones;
    private LinearLayout productChair;

    private int themeColor;
    private boolean following = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_profile);

        sellerHeader = findViewById(R.id.sellerHeader);
        messageButton = findViewById(R.id.messageButton);
        followButton = findViewById(R.id.followButton);

        listingsTab = findViewById(R.id.listingsTab);
        reviewsTab = findViewById(R.id.reviewsTab);
        listingsContainer = findViewById(R.id.listingsContainer);
        reviewsContainer = findViewById(R.id.reviewsContainer);

        listingsTabText = findViewById(R.id.listingsTabText);
        reviewsTabText = findViewById(R.id.reviewsTabText);

        sellerLaptopPrice = findViewById(R.id.sellerLaptopPrice);
        sellerMousePrice = findViewById(R.id.sellerMousePrice);
        sellerHeadphonesPrice = findViewById(R.id.sellerHeadphonesPrice);
        sellerChairPrice = findViewById(R.id.sellerChairPrice);

        backButton = findViewById(R.id.backButton);
        sellerVerifiedBadge = findViewById(R.id.sellerVerifiedBadge);
        sellerFavouriteLaptop = findViewById(R.id.sellerFavouriteLaptop);
        sellerFavouriteMouse = findViewById(R.id.sellerFavouriteMouse);
        sellerFavouriteHeadphones = findViewById(R.id.sellerFavouriteHeadphones);
        sellerFavouriteChair = findViewById(R.id.sellerFavouriteChair);

        productLaptop = findViewById(R.id.productLaptop);
        productMouse = findViewById(R.id.productMouse);
        productHeadphones = findViewById(R.id.productHeadphones);
        productChair = findViewById(R.id.productChair);

        applyAccountTheme();
        showListings();
        setupFavouriteButtons();

        backButton.setOnClickListener(v -> finish());

        messageButton.setOnClickListener(v ->
                Toast.makeText(
                        this,
                        "Message John Smith",
                        Toast.LENGTH_SHORT
                ).show()
        );

        followButton.setOnClickListener(v -> {
            following = !following;
            setFollowButtonStyle();
        });

        listingsTab.setOnClickListener(v -> showListings());
        reviewsTab.setOnClickListener(v -> showReviews());

        productLaptop.setOnClickListener(v ->
                openProduct(
                        "Dell Laptop",
                        "R8,499",
                        "Powerful and reliable Dell laptop in excellent condition.",
                        R.drawable.dell_laptop_1,
                        R.drawable.dell_laptop_2,
                        R.drawable.dell_laptop_3,
                        R.drawable.dell_laptop_4
                )
        );

        productMouse.setOnClickListener(v ->
                openProduct(
                        "Bluetooth Mouse",
                        "R600",
                        "Wireless HP Bluetooth mouse in excellent condition.",
                        R.drawable.hp_mouse_1,
                        R.drawable.hp_mouse_2
                )
        );

        productHeadphones.setOnClickListener(v ->
                openProduct(
                        "Headphones",
                        "R950",
                        "Comfortable Volkano over-ear headphones with excellent sound.",
                        R.drawable.volkano_headphones_1,
                        R.drawable.volkano_headphones_2,
                        R.drawable.volkano_headphones_3
                )
        );

        productChair.setOnClickListener(v ->
                openProduct(
                        "Gaming Chair",
                        "R2,500",
                        "Comfortable Razer gaming chair in excellent condition.",
                        R.drawable.game_chair_1,
                        R.drawable.game_chair_2,
                        R.drawable.game_chair_3,
                        R.drawable.game_chair_4
                )
        );
    }

    private void setupFavouriteButtons() {
        configureFavouriteButton(sellerFavouriteLaptop);
        configureFavouriteButton(sellerFavouriteMouse);
        configureFavouriteButton(sellerFavouriteHeadphones);
        configureFavouriteButton(sellerFavouriteChair);
    }

    private void configureFavouriteButton(ImageView heart) {
        setFavouriteStyle(heart, false);

        heart.setOnClickListener(v -> {
            boolean liked = Boolean.TRUE.equals(heart.getTag());
            setFavouriteStyle(heart, !liked);
        });
    }

    private void setFavouriteStyle(ImageView heart, boolean liked) {
        heart.setTag(liked);

        ViewCompat.setBackgroundTintList(
                heart,
                ColorStateList.valueOf(
                        liked ? Color.parseColor("#E53935") : Color.WHITE
                )
        );

        heart.setColorFilter(
                liked ? Color.WHITE : Color.parseColor("#555555")
        );
    }

    private void showListings() {
        listingsContainer.setVisibility(LinearLayout.VISIBLE);
        reviewsContainer.setVisibility(LinearLayout.GONE);

        setTabStyle(listingsTab, listingsTabText, true);
        setTabStyle(reviewsTab, reviewsTabText, false);
    }

    private void showReviews() {
        listingsContainer.setVisibility(LinearLayout.GONE);
        reviewsContainer.setVisibility(LinearLayout.VISIBLE);

        setTabStyle(listingsTab, listingsTabText, false);
        setTabStyle(reviewsTab, reviewsTabText, true);
    }

    private void setTabStyle(
            LinearLayout tab,
            TextView label,
            boolean selected
    ) {
        GradientDrawable background = new GradientDrawable();
        background.setShape(GradientDrawable.RECTANGLE);
        background.setCornerRadius(dp(8));
        background.setColor(
                selected
                        ? Color.WHITE
                        : Color.parseColor("#E2E4E8")
        );

        tab.setBackground(background);
        label.setTextColor(
                selected
                        ? themeColor
                        : Color.parseColor("#222222")
        );
    }

    private void openProduct(
            String title,
            String price,
            String description,
            int... images
    ) {
        int image = images.length > 0
                ? images[0]
                : R.drawable.dell_laptop_1;

        Intent intent = new Intent(this, ProductInfoActivity.class);
        intent.putExtra("productTitle", title);
        intent.putExtra("productPrice", price);
        intent.putExtra("productDescription", description);
        intent.putExtra("productImage", image);
        intent.putExtra(
                "productImages",
                images.length > 0
                        ? images
                        : new int[]{image}
        );

        startActivity(intent);
    }

    private void applyAccountTheme() {
        SessionManager sessionManager = new SessionManager(this);
        themeColor = getThemeColor(sessionManager.getUserRole());

        ViewCompat.setBackgroundTintList(
                sellerHeader,
                ColorStateList.valueOf(themeColor)
        );

        ViewCompat.setBackgroundTintList(
                messageButton,
                ColorStateList.valueOf(themeColor)
        );

        sellerLaptopPrice.setTextColor(themeColor);
        sellerMousePrice.setTextColor(themeColor);
        sellerHeadphonesPrice.setTextColor(themeColor);
        sellerChairPrice.setTextColor(themeColor);
        sellerVerifiedBadge.setColorFilter(themeColor);

        setFollowButtonStyle();
        getWindow().setStatusBarColor(themeColor);
    }

    private void setFollowButtonStyle() {
        TextView text = (TextView) followButton.getChildAt(0);
        text.setText(following ? "Following" : "Follow");
        text.setTextColor(following ? Color.WHITE : themeColor);

        GradientDrawable background = new GradientDrawable();
        background.setShape(GradientDrawable.RECTANGLE);
        background.setCornerRadius(dp(8));
        background.setColor(following ? themeColor : Color.WHITE);
        background.setStroke(dp(1), themeColor);

        followButton.setBackground(background);
    }

    private int dp(int value) {
        return Math.round(
                value * getResources().getDisplayMetrics().density
        );
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
}
