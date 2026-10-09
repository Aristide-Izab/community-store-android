package com.example.communitystore.student;

public class StudentMarketRow {

    public static final int TYPE_PROMO = 0;
    public static final int TYPE_HEADER = 1;
    public static final int TYPE_PRODUCT = 2;

    public final int type;
    public final String title;
    public final StudentMarketProduct product;

    private StudentMarketRow(int type, String title, StudentMarketProduct product) {
        this.type = type;
        this.title = title;
        this.product = product;
    }

    public static StudentMarketRow promo() {
        return new StudentMarketRow(TYPE_PROMO, null, null);
    }

    public static StudentMarketRow header(String title) {
        return new StudentMarketRow(TYPE_HEADER, title, null);
    }

    public static StudentMarketRow product(StudentMarketProduct product) {
        return new StudentMarketRow(TYPE_PRODUCT, null, product);
    }
}
