package com.example.communitystore.shared.market;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.communitystore.R;
import com.example.communitystore.shared.cart.CartActivity;
import com.example.communitystore.shared.product.ProductInfoActivity;
import com.example.communitystore.shared.utils.BottomNavHelper;
import com.example.communitystore.shared.utils.ThemeUtils;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MarketActivity extends AppCompatActivity {

    private static final int SORT_RELEVANT = 0;
    private static final int SORT_PRICE_LOW = 1;
    private static final int SORT_PRICE_HIGH = 2;
    private static final int SORT_NEWEST = 3;
    private static final int SORT_POPULAR = 4;

    private static final String[] CATEGORIES = {"Books", "Tech", "Services"};

    private final List<MarketProduct> catalog = new ArrayList<>();
    private final Set<String> selectedCategories = new LinkedHashSet<>();

    private String query = "";
    private String condition = "All";
    private Double minPrice = null;
    private Double maxPrice = null;
    private int sortMode = SORT_RELEVANT;

    private MarketProductAdapter adapter;
    private TextView emptyText;
    private TextView chipAll;
    private TextView chipBooks;
    private TextView chipTech;
    private TextView chipServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market);

        findViewById(R.id.marketRoot).setBackgroundColor(ThemeUtils.primary(this));

        loadCatalog();
        setupRecycler();
        setupSearch();
        setupChips();
        setupActions();

        BottomNavHelper.setup(this, "market");
        refresh();
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavHelper.setup(this, "market");
    }

    // TODO: replace sample listings with a Firestore "listings" query
    private void loadCatalog() {
        catalog.add(new MarketProduct("b1", "Java Textbook", "Books", 250, "Used",
                "Textbook in good condition. Ideal for students.", 4.5f, 40, 8,
                R.drawable.textbook_java_1, R.drawable.textbook_java_2));
        catalog.add(new MarketProduct("b2", "Intro to Java", "Books", 180, "Like New",
                "Second-hand copy, lightly highlighted.", 4.2f, 22, 6,
                R.drawable.textbook_java_2, R.drawable.textbook_java_1));
        catalog.add(new MarketProduct("b3", "Calculus Notes", "Books", 120, "New",
                "Printed summary notes for first-year calculus.", 4.0f, 10, 9,
                R.drawable.textbook_java_1));

        catalog.add(new MarketProduct("t1", "Iphone 15", "Tech", 16000, "Like New",
                "Excellent condition. Comes with original accessories.", 4.5f, 95, 7,
                R.drawable.iphone_15_image_1, R.drawable.iphone_15_image_2));
        catalog.add(new MarketProduct("t2", "Volkano Headphones", "Tech", MarketProduct.MESSAGE_FOR_PRICE, "New",
                "Wireless headphones. Message the seller for pricing.", 4.3f, 60, 5,
                R.drawable.volkano_headphones_1, R.drawable.volkano_headphones_2, R.drawable.volkano_headphones_3));
        catalog.add(new MarketProduct("t3", "Dell Laptop", "Tech", 7500, "Used",
                "Reliable student laptop, battery in good health.", 4.6f, 80, 4,
                R.drawable.dell_laptop_1, R.drawable.dell_laptop_2, R.drawable.dell_laptop_3, R.drawable.dell_laptop_4));
        catalog.add(new MarketProduct("t4", "HP Mouse", "Tech", 180, "New",
                "Wireless mouse, unopened.", 4.1f, 18, 3,
                R.drawable.hp_mouse_1, R.drawable.hp_mouse_2));
        catalog.add(new MarketProduct("t5", "Gaming Chair", "Tech", 1800, "Used",
                "Comfortable ergonomic chair.", 4.4f, 35, 2,
                R.drawable.game_chair_1, R.drawable.game_chair_2, R.drawable.game_chair_3, R.drawable.game_chair_4));

        catalog.add(new MarketProduct("s1", "Tutor", "Services", MarketProduct.MESSAGE_FOR_PRICE, "",
                "Private tutoring available. Contact the seller for more information.", 4.5f, 70, 10,
                R.drawable.tutor_1, R.drawable.tutor_2));
        catalog.add(new MarketProduct("s2", "Proofreading", "Services", 120, "",
                "Assignment and essay proofreading.", 4.0f, 25, 1,
                R.drawable.tutor_2));
        catalog.add(new MarketProduct("s3", "Laptop Repairs", "Services", MarketProduct.MESSAGE_FOR_PRICE, "",
                "Screen, battery and software repairs.", 4.7f, 50, 0,
                R.drawable.dell_laptop_2));
    }

    protected void openMyOrders() {
        Toast.makeText(this, "My Orders", Toast.LENGTH_SHORT).show();
    }

    private void setupRecycler() {
        RecyclerView recycler = findViewById(R.id.marketRecycler);
        emptyText = findViewById(R.id.emptyText);

        adapter = new MarketProductAdapter(new MarketProductAdapter.Listener() {
            @Override
            public void onProductClick(MarketProduct product) {
                openProduct(product);
            }

            @Override
            public void onMyOrdersClick() {
                openMyOrders();
            }
        });

        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return adapter.getSpanSize(position, 3);
            }
        });

        recycler.setLayoutManager(layoutManager);
        recycler.setAdapter(adapter);
    }

    private void setupSearch() {
        EditText searchInput = findViewById(R.id.searchInput);

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                query = s.toString().trim().toLowerCase(Locale.ROOT);
                refresh();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setupChips() {
        chipAll = findViewById(R.id.chipAll);
        chipBooks = findViewById(R.id.chipBooks);
        chipTech = findViewById(R.id.chipTech);
        chipServices = findViewById(R.id.chipServices);

        chipAll.setOnClickListener(v -> selectSingleCategory(null));
        chipBooks.setOnClickListener(v -> selectSingleCategory("Books"));
        chipTech.setOnClickListener(v -> selectSingleCategory("Tech"));
        chipServices.setOnClickListener(v -> selectSingleCategory("Services"));
    }

    private void selectSingleCategory(String category) {
        selectedCategories.clear();

        if (category != null) {
            selectedCategories.add(category);
        }

        refresh();
    }

    private void setupActions() {
        findViewById(R.id.profileHeaderButton).setOnClickListener(v -> {
            Intent intent = new Intent(this, BottomNavHelper.profileFor(this));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        findViewById(R.id.cartButton).setOnClickListener(v ->
                startActivity(new Intent(this, CartActivity.class)));

        findViewById(R.id.sortButton).setOnClickListener(v -> showSortSheet());
        findViewById(R.id.filterButton).setOnClickListener(v -> showFilterSheet());
    }

    private void refresh() {
        styleChips();

        List<MarketProduct> filtered = new ArrayList<>();
        for (MarketProduct product : catalog) {
            if (matches(product)) {
                filtered.add(product);
            }
        }

        List<MarketRow> rows = new ArrayList<>();

        if (query.isEmpty()) {
            rows.add(MarketRow.promo());
        }

        for (String category : CATEGORIES) {
            List<MarketProduct> group = new ArrayList<>();

            for (MarketProduct product : filtered) {
                if (product.category.equals(category)) {
                    group.add(product);
                }
            }

            if (group.isEmpty()) {
                continue;
            }

            Collections.sort(group, comparator());
            rows.add(MarketRow.header("Tech".equals(category) ? "Technology" : category));

            for (MarketProduct product : group) {
                rows.add(MarketRow.product(product));
            }
        }

        adapter.submit(rows);
        emptyText.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private boolean matches(MarketProduct p) {
        if (!selectedCategories.isEmpty() && !selectedCategories.contains(p.category)) {
            return false;
        }

        if (!query.isEmpty()
                && !p.title.toLowerCase(Locale.ROOT).contains(query)
                && !p.category.toLowerCase(Locale.ROOT).contains(query)) {
            return false;
        }

        if (!"All".equals(condition) && !condition.equals(p.condition)) {
            return false;
        }

        if (minPrice != null || maxPrice != null) {
            if (!p.hasFixedPrice()) {
                return false;
            }
            if (minPrice != null && p.price < minPrice) {
                return false;
            }
            if (maxPrice != null && p.price > maxPrice) {
                return false;
            }
        }

        return true;
    }

    private Comparator<MarketProduct> comparator() {
        switch (sortMode) {
            case SORT_PRICE_LOW:
                return (a, b) -> Double.compare(
                        a.hasFixedPrice() ? a.price : Double.MAX_VALUE,
                        b.hasFixedPrice() ? b.price : Double.MAX_VALUE);
            case SORT_PRICE_HIGH:
                return (a, b) -> Double.compare(
                        b.hasFixedPrice() ? b.price : -1,
                        a.hasFixedPrice() ? a.price : -1);
            case SORT_NEWEST:
                return (a, b) -> Integer.compare(b.postedOrder, a.postedOrder);
            case SORT_POPULAR:
                return (a, b) -> Integer.compare(b.popularity, a.popularity);
            case SORT_RELEVANT:
            default:
                return (a, b) -> Integer.compare(catalog.indexOf(a), catalog.indexOf(b));
        }
    }

    private void styleChips() {
        ThemeUtils.styleChip(chipAll, selectedCategories.isEmpty());
        ThemeUtils.styleChip(chipBooks, selectedCategories.size() == 1 && selectedCategories.contains("Books"));
        ThemeUtils.styleChip(chipTech, selectedCategories.size() == 1 && selectedCategories.contains("Tech"));
        ThemeUtils.styleChip(chipServices, selectedCategories.size() == 1 && selectedCategories.contains("Services"));
    }

    private void openProduct(MarketProduct product) {
        Intent intent = new Intent(this, ProductInfoActivity.class);
        intent.putExtra("productTitle", product.title);
        intent.putExtra("productPrice",
                product.hasFixedPrice()
                        ? String.format(Locale.US, "R%,.0f", product.price)
                        : "Message for price");
        intent.putExtra("productDescription", product.description);
        intent.putExtra("productImage", product.images[0]);
        intent.putExtra("productImages", product.images);
        startActivity(intent);
    }

    private void showSortSheet() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View sheet = getLayoutInflater().inflate(R.layout.sheet_market_sort, null);
        dialog.setContentView(sheet);
        makeSheetBackgroundTransparent(sheet);

        styleSheetHeader(sheet);
        ThemeUtils.stylePrimaryButton(sheet.findViewById(R.id.applySort));

        RadioGroup group = sheet.findViewById(R.id.sortGroup);
        group.check(sortIdFor(sortMode));

        sheet.findViewById(R.id.sortReset).setOnClickListener(v -> group.check(R.id.sortRelevant));

        sheet.findViewById(R.id.applySort).setOnClickListener(v -> {
            sortMode = sortModeFor(group.getCheckedRadioButtonId());
            refresh();
            dialog.dismiss();
        });

        dialog.show();
    }

    private int sortIdFor(int mode) {
        switch (mode) {
            case SORT_PRICE_LOW:
                return R.id.sortPriceLow;
            case SORT_PRICE_HIGH:
                return R.id.sortPriceHigh;
            case SORT_NEWEST:
                return R.id.sortNewest;
            case SORT_POPULAR:
                return R.id.sortPopular;
            default:
                return R.id.sortRelevant;
        }
    }

    private int sortModeFor(int radioId) {
        if (radioId == R.id.sortPriceLow) return SORT_PRICE_LOW;
        if (radioId == R.id.sortPriceHigh) return SORT_PRICE_HIGH;
        if (radioId == R.id.sortNewest) return SORT_NEWEST;
        if (radioId == R.id.sortPopular) return SORT_POPULAR;
        return SORT_RELEVANT;
    }

    private void showFilterSheet() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View sheet = getLayoutInflater().inflate(R.layout.sheet_market_filter, null);
        dialog.setContentView(sheet);
        makeSheetBackgroundTransparent(sheet);

        styleSheetHeader(sheet);
        ThemeUtils.stylePrimaryButton(sheet.findViewById(R.id.applyFilter));

        Set<String> draftCategories = new LinkedHashSet<>(selectedCategories);
        String[] draftCondition = {condition};

        TextView catBooks = sheet.findViewById(R.id.fCatBooks);
        TextView catTech = sheet.findViewById(R.id.fCatTech);
        TextView catServices = sheet.findViewById(R.id.fCatServices);

        TextView condAll = sheet.findViewById(R.id.fCondAll);
        TextView condNew = sheet.findViewById(R.id.fCondNew);
        TextView condLikeNew = sheet.findViewById(R.id.fCondLikeNew);
        TextView condUsed = sheet.findViewById(R.id.fCondUsed);

        EditText minInput = sheet.findViewById(R.id.fMin);
        EditText maxInput = sheet.findViewById(R.id.fMax);

        if (minPrice != null) minInput.setText(String.valueOf(minPrice.longValue()));
        if (maxPrice != null) maxInput.setText(String.valueOf(maxPrice.longValue()));

        Runnable restyle = () -> {
            ThemeUtils.styleChip(catBooks, draftCategories.contains("Books"));
            ThemeUtils.styleChip(catTech, draftCategories.contains("Tech"));
            ThemeUtils.styleChip(catServices, draftCategories.contains("Services"));
            ThemeUtils.styleChip(condAll, "All".equals(draftCondition[0]));
            ThemeUtils.styleChip(condNew, "New".equals(draftCondition[0]));
            ThemeUtils.styleChip(condLikeNew, "Like New".equals(draftCondition[0]));
            ThemeUtils.styleChip(condUsed, "Used".equals(draftCondition[0]));
        };
        restyle.run();

        catBooks.setOnClickListener(v -> toggle(draftCategories, "Books", restyle));
        catTech.setOnClickListener(v -> toggle(draftCategories, "Tech", restyle));
        catServices.setOnClickListener(v -> toggle(draftCategories, "Services", restyle));

        condAll.setOnClickListener(v -> { draftCondition[0] = "All"; restyle.run(); });
        condNew.setOnClickListener(v -> { draftCondition[0] = "New"; restyle.run(); });
        condLikeNew.setOnClickListener(v -> { draftCondition[0] = "Like New"; restyle.run(); });
        condUsed.setOnClickListener(v -> { draftCondition[0] = "Used"; restyle.run(); });

        sheet.findViewById(R.id.filterReset).setOnClickListener(v -> {
            draftCategories.clear();
            draftCondition[0] = "All";
            minInput.setText("");
            maxInput.setText("");
            restyle.run();
        });

        sheet.findViewById(R.id.applyFilter).setOnClickListener(v -> {
            Double min = parsePrice(minInput);
            Double max = parsePrice(maxInput);

            if (min != null && max != null && min > max) {
                Toast.makeText(this, "Min price can't be higher than max price", Toast.LENGTH_SHORT).show();
                return;
            }

            selectedCategories.clear();
            selectedCategories.addAll(draftCategories);
            condition = draftCondition[0];
            minPrice = min;
            maxPrice = max;

            refresh();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void toggle(Set<String> set, String value, Runnable restyle) {
        if (!set.remove(value)) {
            set.add(value);
        }
        restyle.run();
    }

    private Double parsePrice(EditText input) {
        String text = input.getText().toString().trim();

        if (text.isEmpty()) {
            return null;
        }

        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void styleSheetHeader(View sheet) {
        sheet.setBackground(ThemeUtils.topRounded(this, Color.WHITE, 24));
        View header = sheet.findViewById(R.id.sheetHeader);
        header.setBackground(ThemeUtils.topRounded(this, ThemeUtils.primary(this), 24));
    }

    private void makeSheetBackgroundTransparent(View sheet) {
        View parent = (View) sheet.getParent();

        if (parent != null) {
            parent.setBackgroundColor(Color.TRANSPARENT);
        }
    }
}
