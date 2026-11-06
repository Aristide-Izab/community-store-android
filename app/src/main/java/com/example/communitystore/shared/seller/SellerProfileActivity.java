package com.example.communitystore.shared.seller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import com.example.communitystore.R;
import com.example.communitystore.core.access.UserRole;
import com.example.communitystore.shared.chat.ChatActivity;
import com.example.communitystore.shared.data.Formatters;
import com.example.communitystore.shared.data.Product;
import com.example.communitystore.shared.data.ProductRepository;
import com.example.communitystore.shared.data.SampleProducts;
import com.example.communitystore.shared.data.UserDirectory;
import com.example.communitystore.shared.product.ProductInfoActivity;
import com.example.communitystore.shared.utils.ThemeUtils;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Public profile of a seller: details, rating, active listings and reviews. */
public class SellerProfileActivity extends AppCompatActivity {

    private static final String EXTRA_SELLER_ID = "sellerId";
    private static final String EXTRA_SELLER_NAME = "sellerName";
    private static final String EXTRA_SELLER_ROLE = "sellerRole";

    private static final String FOLLOW_PREFS = "CommunityStoreFollowing";

    private LinearLayout followButton;
    private LinearLayout listingsTab;
    private LinearLayout reviewsTab;
    private LinearLayout listingsContainer;
    private LinearLayout reviewsContainer;
    private LinearLayout reviewsList;
    private TextView listingsTabText;
    private TextView reviewsTabText;
    private TextView listingsEmpty;

    private String sellerId;
    private String sellerName;
    private UserRole sellerRole;

    private int themeColor;
    private boolean following = false;

    public static void open(Context context, String sellerId, String sellerName, String sellerRole) {
        Intent intent = new Intent(context, SellerProfileActivity.class);
        intent.putExtra(EXTRA_SELLER_ID, sellerId);
        intent.putExtra(EXTRA_SELLER_NAME, sellerName);
        intent.putExtra(EXTRA_SELLER_ROLE, sellerRole);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_profile);

        sellerId = getIntent().getStringExtra(EXTRA_SELLER_ID);
        if (sellerId == null || sellerId.isEmpty()) {
            sellerId = SampleProducts.SAMPLE_SELLER_ID;
        }
        sellerName = getIntent().getStringExtra(EXTRA_SELLER_NAME);
        if (sellerName == null || sellerName.isEmpty()) {
            sellerName = isSample() ? SampleProducts.SAMPLE_SELLER_NAME : "Community seller";
        }
        sellerRole = UserRole.fromString(getIntent().getStringExtra(EXTRA_SELLER_ROLE));

        followButton = findViewById(R.id.followButton);
        listingsTab = findViewById(R.id.listingsTab);
        reviewsTab = findViewById(R.id.reviewsTab);
        listingsContainer = findViewById(R.id.listingsContainer);
        reviewsContainer = findViewById(R.id.reviewsContainer);
        reviewsList = findViewById(R.id.reviewsList);
        listingsTabText = findViewById(R.id.listingsTabText);
        reviewsTabText = findViewById(R.id.reviewsTabText);
        listingsEmpty = findViewById(R.id.listingsEmpty);

        following = followPrefs().getStringSet("ids", new HashSet<>()).contains(sellerId);

        applyAccountTheme();
        bindHeader(sellerName);
        showListings();

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
        findViewById(R.id.messageButton).setOnClickListener(v -> ChatActivity.open(
                this,
                sellerId,
                sellerName,
                sellerRole == null ? "" : sellerRole.getValue(),
                null,
                null
        ));

        followButton.setOnClickListener(v -> toggleFollow());
        listingsTab.setOnClickListener(v -> showListings());
        reviewsTab.setOnClickListener(v -> showReviews());

        loadProfile();
        loadListings();
        loadReviews();
    }

    private boolean isSample() {
        return SampleProducts.SAMPLE_SELLER_ID.equals(sellerId);
    }

    private void bindHeader(String name) {
        ((TextView) findViewById(R.id.sellerNameText)).setText(name);

        TextView avatar = findViewById(R.id.sellerAvatarText);
        avatar.setText(Formatters.initial(name));
        ViewCompat.setBackgroundTintList(avatar, ColorStateList.valueOf(ThemeUtils.colorFor(this, sellerRole)));

        String roleText;
        if (isSample()) {
            roleText = "Sample seller";
        } else if (sellerRole != null && sellerRole.requiresDocuments()) {
            roleText = "Verified " + sellerRole.getLabel();
        } else {
            roleText = sellerRole == null ? "Seller" : sellerRole.getLabel() + " seller";
        }
        ((TextView) findViewById(R.id.sellerRoleText)).setText(roleText);

        findViewById(R.id.sellerVerifiedBadge).setVisibility(
                sellerRole != null && sellerRole.requiresDocuments() ? View.VISIBLE : View.GONE
        );
    }

    private void loadProfile() {
        if (isSample()) {
            ((TextView) findViewById(R.id.statOrders)).setText("—");
            return;
        }

        UserDirectory.load(sellerId, profile -> {
            if (profile == null) {
                return;
            }

            if (sellerRole == null) {
                sellerRole = UserRole.fromString(profile.getString("role"));
            }

            sellerName = UserDirectory.displayName(profile, sellerName);
            bindHeader(sellerName);
            ((TextView) findViewById(R.id.statOrders)).setText(String.valueOf(UserDirectory.points(profile)));
        });
    }

    private void loadListings() {
        if (isSample()) {
            List<Product> samples = SampleProducts.all();
            showListingRows(samples);
            return;
        }

        ProductRepository.loadActiveBySeller(sellerId, (products, error) -> showListingRows(products));
    }

    private void showListingRows(List<Product> products) {
        ((TextView) findViewById(R.id.statListings)).setText(String.valueOf(products.size()));

        // Keep the "empty" label (first child) and replace the rest.
        while (listingsContainer.getChildCount() > 1) {
            listingsContainer.removeViewAt(1);
        }

        listingsEmpty.setText("No active listings right now.");
        listingsEmpty.setVisibility(products.isEmpty() ? View.VISIBLE : View.GONE);

        LayoutInflater inflater = LayoutInflater.from(this);

        for (Product product : products) {
            View row = inflater.inflate(R.layout.item_seller_listing, listingsContainer, false);

            ((ImageView) row.findViewById(R.id.listingImage)).setImageResource(product.primaryImage());
            ((TextView) row.findViewById(R.id.listingTitle)).setText(product.title);

            TextView price = row.findViewById(R.id.listingPrice);
            price.setText(product.priceText());
            price.setTextColor(themeColor);

            List<String> meta = new ArrayList<>();
            if (!product.condition.isEmpty()) {
                meta.add(product.condition);
            }
            meta.add(product.category);
            ((TextView) row.findViewById(R.id.listingMeta)).setText(android.text.TextUtils.join(" • ", meta));

            ImageView heart = row.findViewById(R.id.listingFavourite);
            setFavouriteStyle(heart, false);
            heart.setOnClickListener(v -> setFavouriteStyle(heart, !Boolean.TRUE.equals(heart.getTag())));

            row.setOnClickListener(v -> ProductInfoActivity.open(this, product));
            listingsContainer.addView(row);
        }
    }

    private void loadReviews() {
        TextView average = findViewById(R.id.reviewsAverage);
        TextView stars = findViewById(R.id.reviewsStars);
        TextView count = findViewById(R.id.reviewsCount);
        TextView headerRating = findViewById(R.id.sellerRatingText);
        TextView statReviews = findViewById(R.id.statReviews);

        if (isSample()) {
            headerRating.setText("Sample seller");
            statReviews.setText("0");
            count.setText("Sample listings can't be reviewed.");
            return;
        }

        FirebaseFirestore.getInstance()
                .collection("reviews")
                .whereEqualTo("sellerId", sellerId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<DocumentSnapshot> reviews = new ArrayList<>(snapshot.getDocuments());
                    java.util.Collections.sort(reviews, (a, b) -> {
                        long ta = a.getTimestamp("createdAt") == null ? 0 : a.getTimestamp("createdAt").toDate().getTime();
                        long tb = b.getTimestamp("createdAt") == null ? 0 : b.getTimestamp("createdAt").toDate().getTime();
                        return Long.compare(tb, ta);
                    });

                    double total = 0;
                    for (DocumentSnapshot review : reviews) {
                        total += Product.number(review.get("rating"), 0);
                    }

                    int n = reviews.size();
                    double avg = n == 0 ? 0 : total / n;

                    statReviews.setText(String.valueOf(n));
                    headerRating.setText(UserDirectory.ratingText(avg, n));
                    average.setText(String.format(java.util.Locale.US, "%.1f", avg));
                    stars.setText(starText((int) Math.round(avg)));
                    count.setText(n == 0 ? "No reviews yet" : n + (n == 1 ? " review" : " reviews"));

                    reviewsList.removeAllViews();
                    for (DocumentSnapshot review : reviews) {
                        reviewsList.addView(reviewCard(review));
                    }
                })
                .addOnFailureListener(e -> headerRating.setText("No reviews yet"));
    }

    private View reviewCard(DocumentSnapshot review) {
        TextView card = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.topMargin = dp(12);
        card.setLayoutParams(params);
        card.setBackgroundResource(R.drawable.bg_review_card);
        card.setPadding(dp(16), dp(16), dp(16), dp(16));
        card.setTextColor(Color.parseColor("#222222"));
        card.setTextSize(13);

        String name = review.getString("buyerName");
        String comment = review.getString("comment");
        String product = review.getString("productTitle");
        int rating = (int) Product.number(review.get("rating"), 0);

        StringBuilder text = new StringBuilder();
        text.append(name == null || name.isEmpty() ? "Customer" : name)
                .append("     ")
                .append(starText(rating));
        if (product != null && !product.isEmpty()) {
            text.append("\n").append(product);
        }
        text.append("\n\n").append(comment == null || comment.isEmpty() ? "No comment." : comment);
        card.setText(text.toString());
        return card;
    }

    private static String starText(int rating) {
        StringBuilder stars = new StringBuilder();
        for (int i = 1; i <= 5; i++) {
            stars.append(i <= rating ? "★" : "☆");
        }
        return stars.toString();
    }

    private void setFavouriteStyle(ImageView heart, boolean liked) {
        heart.setTag(liked);
        ViewCompat.setBackgroundTintList(
                heart,
                ColorStateList.valueOf(liked ? Color.parseColor("#E53935") : Color.WHITE)
        );
        heart.setColorFilter(liked ? Color.WHITE : Color.parseColor("#555555"));
    }

    private void showListings() {
        listingsContainer.setVisibility(View.VISIBLE);
        reviewsContainer.setVisibility(View.GONE);
        setTabStyle(listingsTab, listingsTabText, true);
        setTabStyle(reviewsTab, reviewsTabText, false);
    }

    private void showReviews() {
        listingsContainer.setVisibility(View.GONE);
        reviewsContainer.setVisibility(View.VISIBLE);
        setTabStyle(listingsTab, listingsTabText, false);
        setTabStyle(reviewsTab, reviewsTabText, true);
    }

    private void setTabStyle(LinearLayout tab, TextView label, boolean selected) {
        GradientDrawable background = new GradientDrawable();
        background.setShape(GradientDrawable.RECTANGLE);
        background.setCornerRadius(dp(8));
        background.setColor(selected ? Color.WHITE : Color.parseColor("#E2E4E8"));
        tab.setBackground(background);
        label.setTextColor(selected ? themeColor : Color.parseColor("#222222"));
    }

    private SharedPreferences followPrefs() {
        return getSharedPreferences(FOLLOW_PREFS, MODE_PRIVATE);
    }

    /** Following is saved on this phone. */
    private void toggleFollow() {
        following = !following;

        Set<String> ids = new HashSet<>(followPrefs().getStringSet("ids", new HashSet<>()));
        if (following) {
            ids.add(sellerId);
        } else {
            ids.remove(sellerId);
        }
        followPrefs().edit().putStringSet("ids", ids).apply();

        setFollowButtonStyle();
    }

    private void applyAccountTheme() {
        themeColor = ThemeUtils.primary(this);

        ViewCompat.setBackgroundTintList(findViewById(R.id.sellerHeader), ColorStateList.valueOf(themeColor));
        ViewCompat.setBackgroundTintList(findViewById(R.id.messageButton), ColorStateList.valueOf(themeColor));
        ((ImageView) findViewById(R.id.sellerVerifiedBadge)).setColorFilter(themeColor);

        setFollowButtonStyle();
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
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
