package com.example.communitystore.shared.cart;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.communitystore.shared.data.Product;
import com.example.communitystore.shared.data.SampleProducts;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Cart stored on the phone (cleared on logout).
 * Each line remembers its seller so checkout can create one order per seller.
 */
public class CartManager {

    private static final String PREF_NAME = "CommunityStoreCart";
    private static final String KEY_ITEMS = "items";

    private final SharedPreferences preferences;

    public CartManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public List<CartItem> getItems() {
        List<CartItem> items = new ArrayList<>();
        String raw = preferences.getString(KEY_ITEMS, "[]");

        try {
            JSONArray array = new JSONArray(raw);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                items.add(new CartItem(
                        object.optString("productId", ""),
                        object.optString("title", "Product"),
                        object.optInt("unitPrice", 0),
                        object.optInt("imageResource", 0),
                        Math.max(1, object.optInt("quantity", 1)),
                        object.optString("sellerId", SampleProducts.SAMPLE_SELLER_ID),
                        object.optString("sellerName", SampleProducts.SAMPLE_SELLER_NAME),
                        object.optString("sellerRole", "vendor"),
                        object.optString("category", "")
                ));
            }
        } catch (Exception ignored) {
        }

        return items;
    }

    /** Adds one unit of a listing (or increases its quantity). */
    public void addProduct(Product product) {
        if (product == null || !product.hasFixedPrice()) {
            return;
        }

        int price = (int) product.price;
        List<CartItem> items = getItems();

        for (CartItem item : items) {
            if (item.sameProductAs(product.id, product.title, price)) {
                item.setQuantity(item.getQuantity() + 1);
                saveItems(items);
                return;
            }
        }

        items.add(new CartItem(
                product.id,
                product.title,
                price,
                product.primaryImage(),
                1,
                product.sellerId,
                product.sellerName,
                product.sellerRole,
                product.category
        ));
        saveItems(items);
    }

    /** Older entry point (no seller details): treated as a sample listing. */
    public void addItem(String title, int unitPrice, int imageResource) {
        Product product = new Product();
        product.title = title;
        product.price = unitPrice;
        product.sellerId = SampleProducts.SAMPLE_SELLER_ID;
        product.sellerName = SampleProducts.SAMPLE_SELLER_NAME;
        product.sellerRole = "vendor";
        product.images = new int[]{imageResource};
        addProduct(product);
    }

    public void saveItems(List<CartItem> items) {
        JSONArray array = new JSONArray();

        try {
            for (CartItem item : items) {
                JSONObject object = new JSONObject();
                object.put("productId", item.getProductId());
                object.put("title", item.getTitle());
                object.put("unitPrice", item.getUnitPrice());
                object.put("imageResource", item.getImageResource());
                object.put("quantity", item.getQuantity());
                object.put("sellerId", item.getSellerId());
                object.put("sellerName", item.getSellerName());
                object.put("sellerRole", item.getSellerRole());
                object.put("category", item.getCategory());
                array.put(object);
            }
        } catch (Exception ignored) {
        }

        preferences.edit().putString(KEY_ITEMS, array.toString()).apply();
    }

    public int getSubtotal() {
        int subtotal = 0;
        for (CartItem item : getItems()) {
            subtotal += item.getLineTotal();
        }
        return subtotal;
    }

    public int getCount() {
        int count = 0;
        for (CartItem item : getItems()) {
            count += item.getQuantity();
        }
        return count;
    }

    public void clear() {
        preferences.edit().remove(KEY_ITEMS).apply();
    }
}
