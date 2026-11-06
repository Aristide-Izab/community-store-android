package com.example.communitystore.shared.product;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.communitystore.R;
import com.example.communitystore.core.access.RoleScreens;
import com.example.communitystore.core.session.AuthSession;
import com.example.communitystore.shared.data.Product;
import com.example.communitystore.shared.data.ProductRepository;
import com.example.communitystore.shared.utils.ThemeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * The signed-in user's own listings (Active / Inactive) with edit,
 * activate/deactivate and delete. Used by Student, Faculty and Vendor.
 */
public class MyListingsActivity extends AppCompatActivity {

    private static final int GREEN_BG = Color.parseColor("#D1E7DD");
    private static final int GREEN_TEXT = Color.parseColor("#0F5132");
    private static final int GRAY_TEXT = Color.parseColor("#64748B");

    private final List<Product> products = new ArrayList<>();

    private TextView activeTab;
    private TextView inactiveTab;
    private View activeIndicator;
    private View inactiveIndicator;
    private LinearLayout productListContainer;
    private TextView emptyStateText;

    private boolean showingActive = true;
    private int themeColor;

    public static void open(Context context) {
        context.startActivity(new Intent(context, MyListingsActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_listings);

        themeColor = ThemeUtils.primary(this);

        findViewById(R.id.myListingsRoot).setBackgroundColor(themeColor);
        ((TextView) findViewById(R.id.screenTitle)).setText("My Listings");

        ImageView profileButton = findViewById(R.id.profileButton);
        profileButton.setImageTintList(ColorStateList.valueOf(themeColor));
        profileButton.setOnClickListener(v -> RoleScreens.openProfile(this));

        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        activeTab = findViewById(R.id.activeTab);
        inactiveTab = findViewById(R.id.inactiveTab);
        activeIndicator = findViewById(R.id.activeIndicator);
        inactiveIndicator = findViewById(R.id.inactiveIndicator);
        productListContainer = findViewById(R.id.productListContainer);
        emptyStateText = findViewById(R.id.emptyStateText);

        activeIndicator.setBackgroundColor(themeColor);

        activeTab.setOnClickListener(v -> {
            showingActive = true;
            render();
        });
        inactiveTab.setOnClickListener(v -> {
            showingActive = false;
            render();
        });

        View addButton = findViewById(R.id.addProductButton);
        addButton.setBackgroundTintList(ColorStateList.valueOf(themeColor));
        addButton.setOnClickListener(v -> AddListingActivity.openNew(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        load();
    }

    private void load() {
        emptyStateText.setText("Loading...");
        emptyStateText.setVisibility(View.VISIBLE);

        ProductRepository.loadBySeller(AuthSession.uid(), (list, error) -> {
            products.clear();
            products.addAll(list);
            render();

            if (error != null) {
                emptyStateText.setText("Couldn't load your listings. Check your connection.");
                emptyStateText.setVisibility(View.VISIBLE);
            }
        });
    }

    private void render() {
        activeTab.setTextColor(showingActive ? Color.BLACK : GRAY_TEXT);
        inactiveTab.setTextColor(showingActive ? GRAY_TEXT : Color.BLACK);
        activeIndicator.setVisibility(showingActive ? View.VISIBLE : View.INVISIBLE);
        inactiveIndicator.setVisibility(showingActive ? View.INVISIBLE : View.VISIBLE);

        productListContainer.removeAllViews();
        int visible = 0;

        for (Product product : products) {
            if (product.isActive() != showingActive) {
                continue;
            }
            productListContainer.addView(createRow(product));
            visible++;
        }

        emptyStateText.setVisibility(visible == 0 ? View.VISIBLE : View.GONE);
        emptyStateText.setText(showingActive
                ? "No active listings yet.\nAdd your first listing below."
                : "No inactive listings.");
    }

    private View createRow(Product product) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(0, dp(14), 0, dp(14));

        ImageView image = new ImageView(this);
        image.setImageResource(product.primaryImage());
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        GradientDrawable imageBackground = new GradientDrawable();
        imageBackground.setColor(Color.parseColor("#E2E8F0"));
        imageBackground.setCornerRadius(dp(8));
        image.setBackground(imageBackground);
        image.setClipToOutline(true);
        row.addView(image, new LinearLayout.LayoutParams(dp(56), dp(56)));

        LinearLayout details = new LinearLayout(this);
        details.setOrientation(LinearLayout.VERTICAL);
        details.setPadding(dp(14), 0, dp(8), 0);

        TextView title = new TextView(this);
        title.setText(product.title);
        title.setTextColor(Color.BLACK);
        title.setTextSize(14);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        details.addView(title);

        TextView price = new TextView(this);
        price.setText(product.priceText() + " • " + product.category);
        price.setTextColor(Color.DKGRAY);
        price.setTextSize(13);
        price.setPadding(0, dp(4), 0, 0);
        details.addView(price);

        TextView popularity = new TextView(this);
        popularity.setText(product.popularity + " ordered");
        popularity.setTextColor(GRAY_TEXT);
        popularity.setTextSize(12);
        details.addView(popularity);

        row.addView(details, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView status = new TextView(this);
        status.setText(product.isActive() ? "Active" : "Inactive");
        status.setTextSize(11);
        status.setTextColor(product.isActive() ? GREEN_TEXT : GRAY_TEXT);
        status.setGravity(Gravity.CENTER);
        status.setPadding(dp(12), dp(5), dp(12), dp(5));
        GradientDrawable statusBackground = new GradientDrawable();
        statusBackground.setColor(product.isActive() ? GREEN_BG : Color.parseColor("#E2E8F0"));
        statusBackground.setCornerRadius(dp(16));
        status.setBackground(statusBackground);
        row.addView(status);

        row.setOnClickListener(v -> showActions(product));

        LinearLayout wrapper = new LinearLayout(this);
        wrapper.setOrientation(LinearLayout.VERTICAL);
        wrapper.addView(row);

        View divider = new View(this);
        divider.setBackgroundColor(Color.parseColor("#E5E7EB"));
        wrapper.addView(divider, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(1)));
        return wrapper;
    }

    private void showActions(Product product) {
        String toggle = product.isActive() ? "Deactivate (hide from Market)" : "Activate (show in Market)";
        String[] options = {"View listing", "Edit", toggle, "Delete"};

        new AlertDialog.Builder(this)
                .setTitle(product.title)
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            ProductInfoActivity.open(this, product);
                            break;
                        case 1:
                            AddListingActivity.openEdit(this, product.id);
                            break;
                        case 2:
                            setStatus(product, product.isActive() ? Product.STATUS_INACTIVE : Product.STATUS_ACTIVE);
                            break;
                        default:
                            confirmDelete(product);
                            break;
                    }
                })
                .show();
    }

    private void setStatus(Product product, String status) {
        ProductRepository.setStatus(product.id, status)
                .addOnSuccessListener(unused -> load())
                .addOnFailureListener(e -> Toast.makeText(this, "Couldn't update: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void confirmDelete(Product product) {
        new AlertDialog.Builder(this)
                .setTitle("Delete listing?")
                .setMessage("\"" + product.title + "\" will be removed from the Market. Existing orders are kept.")
                .setPositiveButton("Delete", (d, w) -> ProductRepository.delete(product.id)
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(this, "Listing deleted.", Toast.LENGTH_SHORT).show();
                            load();
                        })
                        .addOnFailureListener(e -> Toast.makeText(this, "Couldn't delete: " + e.getMessage(), Toast.LENGTH_LONG).show()))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
