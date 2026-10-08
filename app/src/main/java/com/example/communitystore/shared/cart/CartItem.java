package com.example.communitystore.shared.cart;

public class CartItem {

    private String title;
    private int unitPrice;
    private int imageResource;
    private int quantity;

    public CartItem(String title, int unitPrice, int imageResource, int quantity) {
        this.title = title;
        this.unitPrice = unitPrice;
        this.imageResource = imageResource;
        this.quantity = quantity;
    }

    public String getTitle() {
        return title;
    }

    public int getUnitPrice() {
        return unitPrice;
    }

    public int getImageResource() {
        return imageResource;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = Math.max(1, quantity);
    }

    public int getLineTotal() {
        return unitPrice * quantity;
    }
}
