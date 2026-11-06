package com.example.communitystore.shared.data;

import android.content.Intent;

import com.example.communitystore.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A marketplace listing, stored in Firestore "products/{id}".
 *
 * Sample listings (sample = true) are built into the app so the marketplace
 * never looks empty during demos; they are not stored in Firestore.
 */
public class Product {

    public static final String STATUS_ACTIVE = "active";
    public static final String STATUS_INACTIVE = "inactive";
    public static final String STATUS_SOLD = "sold";

    /** Categories sellers can choose from (Add Listing screens). */
    public static final String[] CATEGORIES = {
            "Textbooks",
            "Electronics",
            "Clothing",
            "Furniture",
            "Food",
            "Services",
            "Other"
    };

    /** Market sections shown to buyers. */
    public static final String MARKET_BOOKS = "Books";
    public static final String MARKET_TECH = "Tech";
    public static final String MARKET_SERVICES = "Services";
    public static final String MARKET_OTHER = "Other";

    public String id = "";
    public String title = "";
    public String category = "Other";
    /** Rand amount; -1 means "message for price". */
    public long price = 0;
    public String condition = "";
    public String description = "";
    public String location = "";
    public String status = STATUS_ACTIVE;
    public String sellerId = "";
    public String sellerName = "";
    public String sellerRole = "";
    public long createdAt = 0;
    public int popularity = 0;
    public boolean sample = false;
    /** Local drawable images (sample listings and placeholders). */
    public int[] images = new int[0];

    public boolean hasFixedPrice() {
        return price >= 0;
    }

    public boolean isActive() {
        return STATUS_ACTIVE.equalsIgnoreCase(status);
    }

    public String priceText() {
        return Formatters.price(price);
    }

    /** Market section this listing appears under. */
    public String marketCategory() {
        return marketCategoryFor(category);
    }

    public static String marketCategoryFor(String category) {
        if (category == null) {
            return MARKET_OTHER;
        }

        String c = category.trim().toLowerCase(Locale.ROOT);

        if (c.contains("book") || c.contains("notes")) {
            return MARKET_BOOKS;
        }
        if (c.contains("electronic") || c.contains("tech")) {
            return MARKET_TECH;
        }
        if (c.contains("service")) {
            return MARKET_SERVICES;
        }
        return MARKET_OTHER;
    }

    /** Condition grouped the way the market filter shows it (New / Like New / Used). */
    public String filterCondition() {
        if (condition == null) {
            return "";
        }
        if ("New".equalsIgnoreCase(condition) || "Like New".equalsIgnoreCase(condition)) {
            return condition;
        }
        return condition.isEmpty() ? "" : "Used";
    }

    public int primaryImage() {
        return images != null && images.length > 0 ? images[0] : R.drawable.ic_product_placeholder;
    }

    public int[] imagesOrPlaceholder() {
        return images != null && images.length > 0
                ? images
                : new int[]{R.drawable.ic_product_placeholder};
    }

    public static Product fromDocument(DocumentSnapshot document) {
        Product p = new Product();
        p.id = document.getId();
        p.title = text(document.getString("title"), "Listing");
        p.category = text(document.getString("category"), "Other");

        p.price = number(document.get("price"), 0);

        p.condition = text(document.getString("condition"), "");
        p.description = text(document.getString("description"), "");
        p.location = text(document.getString("location"), "");
        p.status = text(document.getString("status"), STATUS_ACTIVE).toLowerCase(Locale.ROOT);
        p.sellerId = text(document.getString("sellerId"), "");
        p.sellerName = text(document.getString("sellerName"), "");
        p.sellerRole = text(document.getString("sellerRole"), "");

        p.popularity = (int) number(document.get("popularity"), 0);

        Timestamp created = document.getTimestamp("createdAt");
        p.createdAt = created == null ? System.currentTimeMillis() : created.toDate().getTime();

        p.images = new int[0];
        return p;
    }

    /** Fields written when creating or editing a listing. */
    public Map<String, Object> toMap(boolean isNew) {
        Map<String, Object> data = new HashMap<>();
        data.put("title", title);
        data.put("category", category);
        data.put("price", price);
        data.put("condition", condition);
        data.put("description", description);
        data.put("location", location);
        data.put("status", status);
        data.put("sellerId", sellerId);
        data.put("sellerName", sellerName);
        data.put("sellerRole", sellerRole);
        data.put("imageUrls", new java.util.ArrayList<String>());
        data.put("updatedAt", FieldValue.serverTimestamp());

        if (isNew) {
            data.put("popularity", 0);
            data.put("createdAt", FieldValue.serverTimestamp());
        }

        return data;
    }

    // ---- Passing a product between screens ----

    public static final String EXTRA_ID = "productId";
    public static final String EXTRA_TITLE = "productTitle";
    public static final String EXTRA_PRICE_TEXT = "productPrice";
    public static final String EXTRA_PRICE_VALUE = "productPriceValue";
    public static final String EXTRA_DESCRIPTION = "productDescription";
    public static final String EXTRA_IMAGE = "productImage";
    public static final String EXTRA_IMAGES = "productImages";
    public static final String EXTRA_CATEGORY = "productCategory";
    public static final String EXTRA_CONDITION = "productCondition";
    public static final String EXTRA_LOCATION = "productLocation";
    public static final String EXTRA_SELLER_ID = "sellerId";
    public static final String EXTRA_SELLER_NAME = "sellerName";
    public static final String EXTRA_SELLER_ROLE = "sellerRole";
    public static final String EXTRA_SAMPLE = "productSample";

    public void putInto(Intent intent) {
        intent.putExtra(EXTRA_ID, id);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_PRICE_TEXT, priceText());
        intent.putExtra(EXTRA_PRICE_VALUE, price);
        intent.putExtra(EXTRA_DESCRIPTION, description);
        intent.putExtra(EXTRA_IMAGE, primaryImage());
        intent.putExtra(EXTRA_IMAGES, imagesOrPlaceholder());
        intent.putExtra(EXTRA_CATEGORY, category);
        intent.putExtra(EXTRA_CONDITION, condition);
        intent.putExtra(EXTRA_LOCATION, location);
        intent.putExtra(EXTRA_SELLER_ID, sellerId);
        intent.putExtra(EXTRA_SELLER_NAME, sellerName);
        intent.putExtra(EXTRA_SELLER_ROLE, sellerRole);
        intent.putExtra(EXTRA_SAMPLE, sample);
    }

    public static Product fromIntent(Intent intent) {
        Product p = new Product();
        p.id = text(intent.getStringExtra(EXTRA_ID), "");
        p.title = text(intent.getStringExtra(EXTRA_TITLE), "Listing");
        p.price = intent.getLongExtra(EXTRA_PRICE_VALUE, parsePrice(intent.getStringExtra(EXTRA_PRICE_TEXT)));
        p.description = text(intent.getStringExtra(EXTRA_DESCRIPTION), "");
        p.category = text(intent.getStringExtra(EXTRA_CATEGORY), "");
        p.condition = text(intent.getStringExtra(EXTRA_CONDITION), "");
        p.location = text(intent.getStringExtra(EXTRA_LOCATION), "");
        p.sellerId = text(intent.getStringExtra(EXTRA_SELLER_ID), SampleProducts.SAMPLE_SELLER_ID);
        p.sellerName = text(intent.getStringExtra(EXTRA_SELLER_NAME), SampleProducts.SAMPLE_SELLER_NAME);
        p.sellerRole = text(intent.getStringExtra(EXTRA_SELLER_ROLE), "vendor");
        p.sample = intent.getBooleanExtra(EXTRA_SAMPLE, p.id.isEmpty() || SampleProducts.SAMPLE_SELLER_ID.equals(p.sellerId));

        int[] images = intent.getIntArrayExtra(EXTRA_IMAGES);
        if (images == null || images.length == 0) {
            int single = intent.getIntExtra(EXTRA_IMAGE, 0);
            images = single == 0 ? new int[0] : new int[]{single};
        }
        p.images = images;
        return p;
    }

    /** "R1,250" -> 1250; anything else (e.g. "Message for price") -> -1. */
    public static long parsePrice(String text) {
        if (text == null) {
            return -1;
        }
        try {
            return Long.parseLong(text.replace("R", "").replace(",", "").replace(" ", "").trim());
        } catch (Exception e) {
            return -1;
        }
    }

    /** Reads a Firestore number that may have been saved as a number or text. */
    public static long number(Object value, long fallback) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        if (value instanceof String) {
            try {
                return Math.round(Double.parseDouble(((String) value).replace(",", "").trim()));
            } catch (NumberFormatException ignored) {
                return fallback;
            }
        }
        return fallback;
    }

    private static String text(String value, String fallback) {
        return value == null ? fallback : value;
    }
}
