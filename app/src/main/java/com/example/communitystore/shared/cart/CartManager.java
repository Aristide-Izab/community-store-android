package com.example.communitystore.shared.cart;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
                        object.optString("title", "Product"),
                        object.optInt("unitPrice", 0),
                        object.optInt("imageResource", 0),
                        Math.max(1, object.optInt("quantity", 1))
                ));
            }
        } catch (Exception ignored) {
        }

        return items;
    }

    public void addItem(String title, int unitPrice, int imageResource) {
        List<CartItem> items = getItems();

        for (CartItem item : items) {
            if (item.getTitle().equalsIgnoreCase(title) && item.getUnitPrice() == unitPrice) {
                item.setQuantity(item.getQuantity() + 1);
                saveItems(items);
                return;
            }
        }

        items.add(new CartItem(title, unitPrice, imageResource, 1));
        saveItems(items);
    }

    public void saveItems(List<CartItem> items) {
        JSONArray array = new JSONArray();

        try {
            for (CartItem item : items) {
                JSONObject object = new JSONObject();
                object.put("title", item.getTitle());
                object.put("unitPrice", item.getUnitPrice());
                object.put("imageResource", item.getImageResource());
                object.put("quantity", item.getQuantity());
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

    public void clear() {
        preferences.edit().remove(KEY_ITEMS).apply();
    }
}
