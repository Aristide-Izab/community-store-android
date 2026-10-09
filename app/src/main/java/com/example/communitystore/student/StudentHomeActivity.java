package com.example.communitystore.student;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import com.example.communitystore.R;
import com.example.communitystore.core.access.BottomNavThemeManager;
import com.example.communitystore.core.access.QuickAccessThemeManager;
import com.example.communitystore.shared.product.ProductInfoActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class StudentHomeActivity extends AppCompatActivity {

    private TextView greetingText;

    private LinearLayout postListingButton;
    private LinearLayout newsButton;
    private LinearLayout salesButton;
    private LinearLayout ordersButton;

    private LinearLayout navHome;
    private LinearLayout navMarket;
    private LinearLayout navCommunity;
    private LinearLayout navMessages;
    private LinearLayout navProfile;

    private LinearLayout productIphone;
    private LinearLayout productTutor;
    private LinearLayout productTextbook;

    private ImageView favouriteIphone;
    private ImageView favouriteTutor;
    private ImageView favouriteTextbook;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        greetingText = findViewById(R.id.greetingText);

        postListingButton = findViewById(R.id.postListingButton);
        newsButton = findViewById(R.id.newsButton);
        salesButton = findViewById(R.id.salesButton);
        ordersButton = findViewById(R.id.ordersButton);

        navHome = findViewById(R.id.navHome);
        navMarket = findViewById(R.id.navMarket);
        navCommunity = findViewById(R.id.navCommunity);
        navMessages = findViewById(R.id.navMessages);
        navProfile = findViewById(R.id.navProfile);

        productIphone = findViewById(R.id.productIphone);
        productTutor = findViewById(R.id.productTutor);
        productTextbook = findViewById(R.id.productTextbook);

        favouriteIphone = findViewById(R.id.favouriteIphone);
        favouriteTutor = findViewById(R.id.favouriteTutor);
        favouriteTextbook = findViewById(R.id.favouriteTextbook);

        loadStudentName();

        BottomNavThemeManager.apply(this, "home");
        QuickAccessThemeManager.apply(this, "post");

        setupQuickAccess();
        setupNavigation();
        setupProducts();
        setupFavouriteButtons();
    }

    private void setupProducts() {
        productIphone.setOnClickListener(v ->
                openProduct(
                        "Iphone 15",
                        "R16,000",
                        "Excellent condition. Comes with original accessories.",
                        R.drawable.iphone_15_image_1,
                        R.drawable.iphone_15_image_2
                )
        );

        productTutor.setOnClickListener(v ->
                openProduct(
                        "Tutor",
                        "Message for price",
                        "Private tutoring available for students. Contact the seller for availability.",
                        R.drawable.tutor_1,
                        R.drawable.tutor_2
                )
        );

        productTextbook.setOnClickListener(v ->
                openProduct(
                        "Textbook",
                        "R250",
                        "Textbook in good condition. Ideal for students.",
                        R.drawable.textbook_java_1,
                        R.drawable.textbook_java_2
                )
        );
    }

    private void setupFavouriteButtons() {
        configureFavouriteButton(favouriteIphone);
        configureFavouriteButton(favouriteTutor);
        configureFavouriteButton(favouriteTextbook);
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
                liked ? Color.WHITE : Color.parseColor("#111111")
        );
    }

    private void openProduct(
            String title,
            String price,
            String description,
            int... imageResources
    ) {
        int imageResource = imageResources.length > 0
                ? imageResources[0]
                : R.drawable.dell_laptop_1;

        Intent intent = new Intent(this, ProductInfoActivity.class);
        intent.putExtra("productTitle", title);
        intent.putExtra("productPrice", price);
        intent.putExtra("productDescription", description);
        intent.putExtra("productImage", imageResource);
        intent.putExtra(
                "productImages",
                imageResources.length > 0
                        ? imageResources
                        : new int[]{imageResource}
        );

        startActivity(intent);
    }

    private void setupQuickAccess() {
        postListingButton.setOnClickListener(v -> {
            QuickAccessThemeManager.apply(this, "post");
            Toast.makeText(this, "Post a Listing", Toast.LENGTH_SHORT).show();
        });

        newsButton.setOnClickListener(v -> {
            QuickAccessThemeManager.apply(this, "news");
            Toast.makeText(this, "News", Toast.LENGTH_SHORT).show();
        });

        salesButton.setOnClickListener(v -> {
            QuickAccessThemeManager.apply(this, "sales");
            Toast.makeText(this, "Sales", Toast.LENGTH_SHORT).show();
        });

        ordersButton.setOnClickListener(v -> {
            QuickAccessThemeManager.apply(this, "orders");
            Toast.makeText(this, "Orders", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupNavigation() {
        navHome.setOnClickListener(v ->
                BottomNavThemeManager.apply(this, "home")
        );

        navMarket.setOnClickListener(v ->
                BottomNavThemeManager.apply(this, "market")
        );

        // This correctly launches the student community board activity
        navCommunity.setOnClickListener(v -> {
            BottomNavThemeManager.apply(this, "community");
            Intent intent = new Intent(StudentHomeActivity.this, StudentCommunityBoardActivity.class);
            startActivity(intent);
        });

        navMessages.setOnClickListener(v ->
                BottomNavThemeManager.apply(this, "messages")
        );

        navProfile.setOnClickListener(v ->
                BottomNavThemeManager.apply(this, "profile")
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavThemeManager.apply(this, "home");
    }

    private void loadStudentName() {
        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {
            greetingText.setText("Good morning, Student");
            return;
        }

        db.collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(document -> {
                    String name = document.getString("displayName");

                    if (name == null || name.trim().isEmpty()) {
                        name = document.getString("fullName");
                    }

                    if (name == null || name.trim().isEmpty()) {
                        name = "Student";
                    }

                    greetingText.setText("Good morning, " + name);
                })
                .addOnFailureListener(e ->
                        greetingText.setText("Good morning, Student")
                );
    }
}