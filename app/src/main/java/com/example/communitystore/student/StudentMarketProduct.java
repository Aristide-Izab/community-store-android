package com.example.communitystore.student;

public class StudentMarketProduct {

    public static final double MESSAGE_FOR_PRICE = -1;

    public final String id;
    public final String title;
    public final String category;
    public final double price;
    public final String condition;
    public final String description;
    public final float rating;
    public final int popularity;
    public final int postedOrder;
    public final int[] images;

    public StudentMarketProduct(
            String id,
            String title,
            String category,
            double price,
            String condition,
            String description,
            float rating,
            int popularity,
            int postedOrder,
            int... images
    ) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.price = price;
        this.condition = condition;
        this.description = description;
        this.rating = rating;
        this.popularity = popularity;
        this.postedOrder = postedOrder;
        this.images = images;
    }

    public boolean hasFixedPrice() {
        return price >= 0;
    }
}
