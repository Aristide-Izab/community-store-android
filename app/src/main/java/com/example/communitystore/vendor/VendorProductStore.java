package com.example.communitystore.vendor;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public final class VendorProductStore {

    private static final String PREF_NAME = "CommunityStoreVendorProducts";
    private static final String KEY_PRODUCTS = "products";

    private VendorProductStore() {
    }

    public static List<VendorProduct> getProducts(Context context) {
        List<VendorProduct> products = new ArrayList<>();
        SharedPreferences preferences = context.getSharedPreferences(
                PREF_NAME,
                Context.MODE_PRIVATE
        );

        String raw = preferences.getString(KEY_PRODUCTS, "[]");

        try {
            JSONArray array = new JSONArray(raw);

            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);

                products.add(
                        new VendorProduct(
                                object.optString("title", "Product"),
                                object.optInt("price", 0),
                                object.optString("category", "Other"),
                                object.optString("condition", "New"),
                                object.optString("description", ""),
                                object.optString("location", "Campus"),
                                object.optString("status", "Active")
                        )
                );
            }
        } catch (Exception ignored) {
        }

        return products;
    }

    public static void addProduct(Context context, VendorProduct product) {
        List<VendorProduct> products = getProducts(context);
        products.add(product);
        saveProducts(context, products);
    }

    public static int countActive(Context context) {
        int count = 0;

        for (VendorProduct product : getProducts(context)) {
            if (product.isActive()) {
                count++;
            }
        }

        return count;
    }

    private static void saveProducts(
            Context context,
            List<VendorProduct> products
    ) {
        JSONArray array = new JSONArray();

        try {
            for (VendorProduct product : products) {
                JSONObject object = new JSONObject();
                object.put("title", product.getTitle());
                object.put("price", product.getPrice());
                object.put("category", product.getCategory());
                object.put("condition", product.getCondition());
                object.put("description", product.getDescription());
                object.put("location", product.getLocation());
                object.put("status", product.getStatus());
                array.put(object);
            }
        } catch (Exception ignored) {
        }

        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_PRODUCTS, array.toString())
                .apply();
    }
}
