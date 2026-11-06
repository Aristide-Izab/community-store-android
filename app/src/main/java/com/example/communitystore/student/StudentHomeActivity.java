package com.example.communitystore.student;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import com.example.communitystore.R;
import com.example.communitystore.core.access.BottomNavThemeManager;
import com.example.communitystore.core.access.QuickAccessThemeManager;
import com.example.communitystore.core.access.UserRole;
import com.example.communitystore.core.session.SessionManager;
import com.example.communitystore.shared.data.Formatters;
import com.example.communitystore.shared.data.Post;
import com.example.communitystore.shared.data.Product;
import com.example.communitystore.shared.data.ProductRepository;
import com.example.communitystore.shared.data.UserDirectory;
import com.example.communitystore.shared.orders.OrdersActivity;
import com.example.communitystore.shared.product.AddListingActivity;
import com.example.communitystore.shared.product.ProductInfoActivity;
import com.example.communitystore.shared.utils.BottomNavHelper;
import com.example.communitystore.shared.utils.ThemeUtils;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Home screen for Student accounts (Faculty uses the same screen through
 * FacultyHomeActivity; colours and labels follow the signed-in role).
 */
public class StudentHomeActivity extends AppCompatActivity {

    private TextView greetingText;
    private TextView roleText;
    private TextView pointsText;
    private TextView pointsHint;

    private final LinearLayout[] recCards = new LinearLayout[3];
    private final ImageView[] recImages = new ImageView[3];
    private final TextView[] recTitles = new TextView[3];
    private final TextView[] recPrices = new TextView[3];
    private final ImageView[] recHearts = new ImageView[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);

        greetingText = findViewById(R.id.greetingText);
        roleText = findViewById(R.id.roleText);
        pointsText = findViewById(R.id.pointsText);
        pointsHint = findViewById(R.id.pointsHint);

        recCards[0] = findViewById(R.id.productIphone);
        recCards[1] = findViewById(R.id.productTutor);
        recCards[2] = findViewById(R.id.productTextbook);
        recHearts[0] = findViewById(R.id.favouriteIphone);
        recHearts[1] = findViewById(R.id.favouriteTutor);
        recHearts[2] = findViewById(R.id.favouriteTextbook);
        recImages[0] = findViewById(R.id.rec1Image);
        recImages[1] = findViewById(R.id.rec2Image);
        recImages[2] = findViewById(R.id.rec3Image);
        recTitles[0] = findViewById(R.id.rec1Title);
        recTitles[1] = findViewById(R.id.rec2Title);
        recTitles[2] = findViewById(R.id.rec3Title);
        recPrices[0] = findViewById(R.id.rec1Price);
        recPrices[1] = findViewById(R.id.rec2Price);
        recPrices[2] = findViewById(R.id.rec3Price);

        applyRoleColours();
        roleText.setText(role().getLabel());
        String cached = new SessionManager(this).getDisplayName();
        greetingText.setText(Formatters.greeting() + ", "
                + (cached.isEmpty() ? role().getLabel() : Formatters.firstName(cached)));
        pointsHint.setText("Earn points with every order");

        QuickAccessThemeManager.apply(this, "post");

        setupQuickAccess();
        setupNavigation();
        setupFavouriteButtons();

        findViewById(R.id.highlight1).setOnClickListener(v -> openCommunity());
        findViewById(R.id.highlight2).setOnClickListener(v -> openCommunity());
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavThemeManager.apply(this, "home");
        loadProfile();
        loadRecommended();
        loadHighlights();
    }

    /**
     * The layout is shared with Faculty, so every role-coloured part that is not
     * handled by the bottom nav / quick access managers is coloured here.
     */
    private void applyRoleColours() {
        int color = ThemeUtils.primary(this);
        findViewById(R.id.homeRoot).setBackgroundColor(color);

        int[] verifiedLabels = {R.id.recVerified1, R.id.recVerified2, R.id.recVerified3};
        for (int id : verifiedLabels) {
            ((TextView) findViewById(id)).setTextColor(color);
        }
    }

    private UserRole role() {
        UserRole role = new SessionManager(this).getUserRole();
        return role == null ? UserRole.STUDENT : role;
    }

    private void loadProfile() {
        UserDirectory.loadCurrent(profile -> {
            if (profile == null) {
                return;
            }

            String name = UserDirectory.displayName(profile, role().getLabel());
            greetingText.setText(Formatters.greeting() + ", " + Formatters.firstName(name));
            pointsText.setText(NumberFormat.getIntegerInstance(Locale.US).format(UserDirectory.points(profile)));
        });
    }

    /** Newest real listings first, topped up with samples. */
    private void loadRecommended() {
        ProductRepository.loadMarketplace((products, error) -> {
            for (int i = 0; i < recCards.length; i++) {
                if (i >= products.size()) {
                    recCards[i].setVisibility(View.GONE);
                    continue;
                }

                Product product = products.get(i);
                recCards[i].setVisibility(View.VISIBLE);
                recImages[i].setImageResource(product.primaryImage());
                recTitles[i].setText(product.title);
                recPrices[i].setText(product.priceText());
                recCards[i].setOnClickListener(v -> ProductInfoActivity.open(this, product));
            }
        });
    }

    /** Shows the two newest community posts (keeps the sample cards if there are none). */
    private void loadHighlights() {
        FirebaseFirestore.getInstance()
                .collection("posts")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(2)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<DocumentSnapshot> docs = snapshot.getDocuments();
                    TextView[] titles = {findViewById(R.id.highlight1Title), findViewById(R.id.highlight2Title)};
                    TextView[] metas = {findViewById(R.id.highlight1Meta), findViewById(R.id.highlight2Meta)};

                    for (int i = 0; i < docs.size() && i < 2; i++) {
                        Post post = Post.fromDocument(docs.get(i));
                        titles[i].setText(post.title);
                        metas[i].setText(post.eventDate.isEmpty() ? post.category : post.eventDate);
                    }
                });
    }

    private void setupFavouriteButtons() {
        for (ImageView heart : recHearts) {
            setFavouriteStyle(heart, false);
            heart.setOnClickListener(v -> setFavouriteStyle(heart, !Boolean.TRUE.equals(heart.getTag())));
        }
    }

    private void setFavouriteStyle(ImageView heart, boolean liked) {
        heart.setTag(liked);
        ViewCompat.setBackgroundTintList(
                heart,
                ColorStateList.valueOf(liked ? Color.parseColor("#E53935") : Color.WHITE)
        );
        heart.setColorFilter(liked ? Color.WHITE : Color.parseColor("#111111"));
    }

    private void setupQuickAccess() {
        findViewById(R.id.postListingButton).setOnClickListener(v -> {
            QuickAccessThemeManager.apply(this, "post");
            AddListingActivity.openNew(this);
        });

        findViewById(R.id.newsButton).setOnClickListener(v -> {
            QuickAccessThemeManager.apply(this, "news");
            openCommunity();
        });

        findViewById(R.id.salesButton).setOnClickListener(v -> {
            QuickAccessThemeManager.apply(this, "sales");
            OrdersActivity.openSales(this);
        });

        findViewById(R.id.ordersButton).setOnClickListener(v -> {
            QuickAccessThemeManager.apply(this, "orders");
            OrdersActivity.openPurchases(this);
        });
    }

    private void openCommunity() {
        BottomNavHelper.open(this, BottomNavHelper.communityFor(this));
    }

    private void setupNavigation() {
        BottomNavHelper.setup(this, "home");

        findViewById(R.id.profileHeaderButton).setOnClickListener(v ->
                BottomNavHelper.open(this, BottomNavHelper.profileFor(this)));

        findViewById(R.id.viewRewardsButton).setOnClickListener(v ->
                startActivity(new Intent(this, BottomNavHelper.rewardsFor(this))));
    }
}
