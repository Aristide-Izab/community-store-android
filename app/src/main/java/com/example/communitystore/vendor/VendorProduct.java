package com.example.communitystore.vendor;

public class VendorProduct {

    private final String title;
    private final int price;
    private final String category;
    private final String condition;
    private final String description;
    private final String location;
    private final String status;

    public VendorProduct(
            String title,
            int price,
            String category,
            String condition,
            String description,
            String location,
            String status
    ) {
        this.title = title;
        this.price = price;
        this.category = category;
        this.condition = condition;
        this.description = description;
        this.location = location;
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public int getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    public String getCondition() {
        return condition;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getStatus() {
        return status;
    }

    public boolean isActive() {
        return "Active".equalsIgnoreCase(status);
    }
}
