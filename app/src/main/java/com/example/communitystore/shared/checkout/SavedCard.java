package com.example.communitystore.shared.checkout;

public class SavedCard {

    private final String id;
    private final String brand;
    private final String lastFour;
    private final String holderName;

    public SavedCard(String id, String brand, String lastFour, String holderName) {
        this.id = id;
        this.brand = brand;
        this.lastFour = lastFour;
        this.holderName = holderName;
    }

    public String getId() {
        return id;
    }

    public String getBrand() {
        return brand;
    }

    public String getLastFour() {
        return lastFour;
    }

    public String getHolderName() {
        return holderName;
    }
}
