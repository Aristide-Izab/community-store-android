package com.example.communitystore.shared.market;

public class MarketRow {

    public static final int TYPE_PROMO = 0;
    public static final int TYPE_HEADER = 1;
    public static final int TYPE_PRODUCT = 2;

    public final int type;
    public final String title;
    public final MarketProduct product;

    private MarketRow(int type, String title, MarketProduct product) {
        this.type = type;
        this.title = title;
        this.product = product;
    }

    public static MarketRow promo() {
        return new MarketRow(TYPE_PROMO, null, null);
    }

    public static MarketRow header(String title) {
        return new MarketRow(TYPE_HEADER, title, null);
    }

    public static MarketRow product(MarketProduct product) {
        return new MarketRow(TYPE_PRODUCT, null, product);
    }
}
