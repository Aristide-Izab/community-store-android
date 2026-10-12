package com.example.communitystore.faculty;

public class FacultyMarketRow {

    public static final int TYPE_PROMO = 0;
    public static final int TYPE_HEADER = 1;
    public static final int TYPE_PRODUCT = 2;

    public final int type;
    public final String title;
    public final FacultyMarketProduct product;

    private FacultyMarketRow(int type, String title, FacultyMarketProduct product) {
        this.type = type;
        this.title = title;
        this.product = product;
    }

    public static FacultyMarketRow promo() {
        return new FacultyMarketRow(TYPE_PROMO, null, null);
    }

    public static FacultyMarketRow header(String title) {
        return new FacultyMarketRow(TYPE_HEADER, title, null);
    }

    public static FacultyMarketRow product(FacultyMarketProduct product) {
        return new FacultyMarketRow(TYPE_PRODUCT, null, product);
    }
}
