package com.example.communitystore.student;

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
import com.example.communitystore.core.access.BottomNavThemeManager;
import com.example.communitystore.shared.cart.CartActivity;
import com.example.communitystore.shared.data.Product;
import com.example.communitystore.shared.data.ProductRepository;
import com.example.communitystore.shared.orders.OrdersActivity;
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

public class StudentMarketActivity extends AppCompatActivity {

    private static final int SORT_RELEVANT = 0;
    private static final int SORT_PRICE_LOW = 1;
    private static final int SORT_PRICE_HIGH = 2;
    private static final int SORT_NEWEST = 3;
    private static final int SORT_POPULAR = 4;

    private static final String[] CATEGORIES = {"Books", "Tech", "Services", "Other"};

    private final List<StudentMarketProduct> catalog = new ArrayList<>();
    private final Set<String> selectedCategories = new LinkedHashSet<>();

    private String query = "";
    private String condition = "All";
    private Double minPrice = null;
    private Double maxPrice = null;
    private int sortMode = SORT_RELEVANT;

    private StudentMarketProductAdapter adapter;
    private TextView emptyText;
    private TextView chipAll;
    private TextView chipBooks;
    private TextView chipTech;
    private TextView chipServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_market);

        findViewById(R.id.marketRoot).setBackgroundColor(ThemeUtils.primary(this));

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
        BottomNavThemeManager.apply(this, "market");
        loadCatalog();
    }

    /** Real Firestore listings first, then the built-in sample listings. */
    private void loadCatalog() {
        emptyText.setText("Loading listings...");
        emptyText.setVisibility(View.VISIBLE);

        ProductRepository.loadMarketplace((products, error) -> {
            catalog.clear();

            for (Product product : products) {
                catalog.add(new StudentMarketProduct(product));
            }

            emptyText.setText("No listings match your search.");
            refresh();

            if (error != null) {
                Toast.makeText(
                        this,
                        "Couldn't load new listings. Showing samples only.",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    protected void openMyOrders() {
        OrdersActivity.openPurchases(this);
    }

    private void setupRecycler() {
        RecyclerView recycler = findViewById(R.id.marketRecycler);
        emptyText = findViewById(R.id.emptyText);

        adapter = new StudentMarketProductAdapter(new StudentMarketProductAdapter.Listener() {
            @Override
            public void onProductClick(StudentMarketProduct product) {
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
        findViewById(R.id.profileHeaderButton).setOnClickListener(v ->
                BottomNavHelper.open(this, BottomNavHelper.profileFor(this)));

        findViewById(R.id.cartButton).setOnClickListener(v ->
                startActivity(new Intent(this, CartActivity.class)));

        findViewById(R.id.sortButton).setOnClickListener(v -> showSortSheet());
        findViewById(R.id.filterButton).setOnClickListener(v -> showFilterSheet());
    }

    private void refresh() {
        styleChips();

        List<StudentMarketProduct> filtered = new ArrayList<>();
        for (StudentMarketProduct product : catalog) {
            if (matches(product)) {
                filtered.add(product);
            }
        }

        List<StudentMarketRow> rows = new ArrayList<>();

        if (query.isEmpty()) {
            rows.add(StudentMarketRow.promo());
        }

        for (String category : CATEGORIES) {
            List<StudentMarketProduct> group = new ArrayList<>();

            for (StudentMarketProduct product : filtered) {
                if (product.category.equals(category)) {
                    group.add(product);
                }
            }

            if (group.isEmpty()) {
                continue;
            }

            Collections.sort(group, comparator());
            rows.add(StudentMarketRow.header(sectionTitle(category)));

            for (StudentMarketProduct product : group) {
                rows.add(StudentMarketRow.product(product));
            }
        }

        adapter.submit(rows);
        emptyText.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private static String sectionTitle(String category) {
        if ("Tech".equals(category)) {
            return "Technology";
        }
        if ("Other".equals(category)) {
            return "More Finds";
        }
        return category;
    }

    private boolean matches(StudentMarketProduct p) {
        if (!selectedCategories.isEmpty() && !selectedCategories.contains(p.category)) {
            return false;
        }

        if (!query.isEmpty()
                && !p.title.toLowerCase(Locale.ROOT).contains(query)
                && !p.category.toLowerCase(Locale.ROOT).contains(query)
                && !p.source.category.toLowerCase(Locale.ROOT).contains(query)
                && !p.description.toLowerCase(Locale.ROOT).contains(query)) {
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

    private Comparator<StudentMarketProduct> comparator() {
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
                return (a, b) -> Long.compare(b.postedAt, a.postedAt);
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

    private void openProduct(StudentMarketProduct product) {
        ProductInfoActivity.open(this, product.source);
    }

    private void showSortSheet() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View sheet = getLayoutInflater().inflate(R.layout.student_market_sort, null);
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
        View sheet = getLayoutInflater().inflate(R.layout.student_market_filter, null);
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
