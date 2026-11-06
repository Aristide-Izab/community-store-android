package com.example.communitystore.shared.data;

import com.example.communitystore.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Built-in demo listings shown alongside real Firestore listings, so the
 * marketplace is never empty during a presentation.
 *
 * They can be added to the cart and "bought" like real listings; the order
 * is saved with sellerId = SAMPLE_SELLER_ID.
 */
public final class SampleProducts {

    public static final String SAMPLE_SELLER_ID = "sample_seller";
    public static final String SAMPLE_SELLER_NAME = "TechHub (sample)";

    private SampleProducts() {
    }

    public static List<Product> all() {
        List<Product> list = new ArrayList<>();

        list.add(make("b1", "Java Textbook", "Textbooks", 250, "Used",
                "Textbook in good condition. Ideal for students.", 40, 8,
                R.drawable.textbook_java_1, R.drawable.textbook_java_2));
        list.add(make("b2", "Intro to Java", "Textbooks", 180, "Like New",
                "Second-hand copy, lightly highlighted.", 22, 6,
                R.drawable.textbook_java_2, R.drawable.textbook_java_1));
        list.add(make("b3", "Calculus Notes", "Textbooks", 120, "New",
                "Printed summary notes for first-year calculus.", 10, 9,
                R.drawable.textbook_java_1));

        list.add(make("t1", "iPhone 15", "Electronics", 16000, "Like New",
                "Excellent condition. Comes with original accessories.", 95, 7,
                R.drawable.iphone_15_image_1, R.drawable.iphone_15_image_2));
        list.add(make("t2", "Volkano Headphones", "Electronics", -1, "New",
                "Wireless headphones. Message the seller for pricing.", 60, 5,
                R.drawable.volkano_headphones_1, R.drawable.volkano_headphones_2, R.drawable.volkano_headphones_3));
        list.add(make("t3", "Dell Laptop", "Electronics", 7500, "Used",
                "Reliable student laptop, battery in good health.", 80, 4,
                R.drawable.dell_laptop_1, R.drawable.dell_laptop_2, R.drawable.dell_laptop_3, R.drawable.dell_laptop_4));
        list.add(make("t4", "HP Mouse", "Electronics", 180, "New",
                "Wireless mouse, unopened.", 18, 3,
                R.drawable.hp_mouse_1, R.drawable.hp_mouse_2));
        list.add(make("t5", "Gaming Chair", "Furniture", 1800, "Used",
                "Comfortable ergonomic chair.", 35, 2,
                R.drawable.game_chair_1, R.drawable.game_chair_2, R.drawable.game_chair_3, R.drawable.game_chair_4));

        list.add(make("s1", "Tutor", "Services", -1, "",
                "Private tutoring available. Contact the seller for more information.", 70, 10,
                R.drawable.tutor_1, R.drawable.tutor_2));
        list.add(make("s2", "Proofreading", "Services", 120, "",
                "Assignment and essay proofreading.", 25, 1,
                R.drawable.tutor_2));
        list.add(make("s3", "Laptop Repairs", "Services", -1, "",
                "Screen, battery and software repairs.", 50, 0,
                R.drawable.dell_laptop_2));

        return list;
    }

    public static Product byId(String id) {
        for (Product product : all()) {
            if (product.id.equals(id)) {
                return product;
            }
        }
        return null;
    }

    private static Product make(
            String id,
            String title,
            String category,
            long price,
            String condition,
            String description,
            int popularity,
            int postedOrder,
            int... images
    ) {
        Product p = new Product();
        p.id = "sample_" + id;
        p.title = title;
        p.category = category;
        p.price = price;
        p.condition = condition;
        p.description = description;
        p.location = "Cape Town";
        p.status = Product.STATUS_ACTIVE;
        p.sellerId = SAMPLE_SELLER_ID;
        p.sellerName = SAMPLE_SELLER_NAME;
        p.sellerRole = "vendor";
        p.popularity = popularity;
        // Older samples sort below newer ones and below every real listing.
        p.createdAt = 1_000_000L * postedOrder;
        p.sample = true;
        p.images = images;
        return p;
    }
}
