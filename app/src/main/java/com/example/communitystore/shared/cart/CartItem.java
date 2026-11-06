package com.example.communitystore.shared.cart;

/** One line in the cart. Seller details are kept so checkout can create one order per seller. */
public class CartItem {

    private final String productId;
    private final String title;
    private final int unitPrice;
    private final int imageResource;
    private int quantity;
    private final String sellerId;
    private final String sellerName;
    private final String sellerRole;
    private final String category;

    public CartItem(
            String productId,
            String title,
            int unitPrice,
            int imageResource,
            int quantity,
            String sellerId,
            String sellerName,
            String sellerRole,
            String category
    ) {
        this.productId = productId == null ? "" : productId;
        this.title = title;
        this.unitPrice = unitPrice;
        this.imageResource = imageResource;
        this.quantity = Math.max(1, quantity);
        this.sellerId = sellerId == null ? "" : sellerId;
        this.sellerName = sellerName == null ? "" : sellerName;
        this.sellerRole = sellerRole == null ? "" : sellerRole;
        this.category = category == null ? "" : category;
    }

    /** Kept for older code: a cart line without seller details. */
    public CartItem(String title, int unitPrice, int imageResource, int quantity) {
        this("", title, unitPrice, imageResource, quantity, "", "", "", "");
    }

    public String getProductId() {
        return productId;
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

    public String getSellerId() {
        return sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public String getSellerRole() {
        return sellerRole;
    }

    public String getCategory() {
        return category;
    }

    /** Same product (or, for old entries, same title and price). */
    public boolean sameProductAs(String otherId, String otherTitle, int otherPrice) {
        if (!productId.isEmpty() && otherId != null && !otherId.isEmpty()) {
            return productId.equals(otherId);
        }
        return title.equalsIgnoreCase(otherTitle) && unitPrice == otherPrice;
    }
}
