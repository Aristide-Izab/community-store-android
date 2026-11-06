package com.example.communitystore.student;

import com.example.communitystore.shared.data.Product;

/** Market card model. Built from a shared {@link Product} (Firestore or sample). */
public class StudentMarketProduct {

    public static final double MESSAGE_FOR_PRICE = -1;

    public final String id;
    public final String title;
    /** Market section: Books, Tech, Services or Other. */
    public final String category;
    public final double price;
    /** New, Like New or Used (for the filter). */
    public final String condition;
    public final String description;
    public final float rating;
    public final int popularity;
    public final long postedAt;
    public final int[] images;
    public final Product source;

    public StudentMarketProduct(Product source) {
        this.source = source;
        this.id = source.id;
        this.title = source.title;
        this.category = source.marketCategory();
        this.price = source.price;
        this.condition = source.filterCondition();
        this.description = source.description;
        this.rating = 0f;
        this.popularity = source.popularity;
        this.postedAt = source.createdAt;
        this.images = source.imagesOrPlaceholder();
    }

    public boolean hasFixedPrice() {
        return price >= 0;
    }
}
