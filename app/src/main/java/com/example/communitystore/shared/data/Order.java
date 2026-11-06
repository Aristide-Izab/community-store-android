package com.example.communitystore.shared.data;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * An order, stored in Firestore "orders/{id}". Checkout creates one order per
 * seller. Payment is simulated (no real money moves), see CheckoutActivity.
 */
public class Order {

    public static final String STATUS_PLACED = "placed";
    public static final String STATUS_PACKED = "packed";
    public static final String STATUS_OUT_FOR_DELIVERY = "out_for_delivery";
    public static final String STATUS_DELIVERED = "delivered";
    public static final String STATUS_CANCELLED = "cancelled";

    /** Order of the tracking steps shown on the order page. */
    public static final String[] FLOW = {
            STATUS_PLACED,
            STATUS_PACKED,
            STATUS_OUT_FOR_DELIVERY,
            STATUS_DELIVERED
    };

    public static class Item {
        public String productId = "";
        public String title = "";
        public long unitPrice = 0;
        public int quantity = 1;
        public String category = "";

        public long lineTotal() {
            return unitPrice * quantity;
        }
    }

    public String id = "";
    public String buyerId = "";
    public String buyerName = "";
    public String sellerId = "";
    public String sellerName = "";
    public String sellerRole = "";
    public List<Item> items = new ArrayList<>();
    public long subtotal = 0;
    public long delivery = 0;
    public long discount = 0;
    public long total = 0;
    public String paymentMethod = "";
    public String deliveryAddress = "";
    public String status = STATUS_PLACED;
    public boolean reviewed = false;
    public long createdAt = 0;
    public long updatedAt = 0;

    /** "#AB12CD" style short reference. */
    public String reference() {
        String shortId = id.length() > 6 ? id.substring(0, 6) : id;
        return "Order #" + shortId.toUpperCase(java.util.Locale.ROOT);
    }

    public String firstTitle() {
        if (items.isEmpty()) {
            return "Order";
        }
        String title = items.get(0).title;
        return items.size() > 1 ? title + " + " + (items.size() - 1) + " more" : title;
    }

    public int itemCount() {
        int count = 0;
        for (Item item : items) {
            count += item.quantity;
        }
        return count;
    }

    public boolean isOpen() {
        return !STATUS_DELIVERED.equals(status) && !STATUS_CANCELLED.equals(status);
    }

    /** Index of the status in FLOW (0-3), or -1 when cancelled. */
    public int step() {
        for (int i = 0; i < FLOW.length; i++) {
            if (FLOW[i].equals(status)) {
                return i;
            }
        }
        return STATUS_CANCELLED.equals(status) ? -1 : 0;
    }

    /** Next status in the flow, or null when finished. */
    public String nextStatus() {
        int step = step();
        if (step < 0 || step >= FLOW.length - 1) {
            return null;
        }
        return FLOW[step + 1];
    }

    public static String label(String status) {
        if (STATUS_PACKED.equals(status)) {
            return "Packed";
        }
        if (STATUS_OUT_FOR_DELIVERY.equals(status)) {
            return "Out for delivery";
        }
        if (STATUS_DELIVERED.equals(status)) {
            return "Delivered";
        }
        if (STATUS_CANCELLED.equals(status)) {
            return "Cancelled";
        }
        return "Order placed";
    }

    /** Button text for the seller to move the order on. */
    public static String actionFor(String nextStatus) {
        if (STATUS_PACKED.equals(nextStatus)) {
            return "Mark as Packed";
        }
        if (STATUS_OUT_FOR_DELIVERY.equals(nextStatus)) {
            return "Mark Out for Delivery";
        }
        if (STATUS_DELIVERED.equals(nextStatus)) {
            return "Mark as Delivered";
        }
        return "Update Status";
    }

    public String statusLabel() {
        return label(status);
    }

    @SuppressWarnings("unchecked")
    public static Order fromDocument(DocumentSnapshot document) {
        Order o = new Order();
        o.id = document.getId();
        o.buyerId = text(document.getString("buyerId"));
        o.buyerName = text(document.getString("buyerName"));
        o.sellerId = text(document.getString("sellerId"));
        o.sellerName = text(document.getString("sellerName"));
        o.sellerRole = text(document.getString("sellerRole"));
        o.subtotal = Product.number(document.get("subtotal"), 0);
        o.delivery = Product.number(document.get("delivery"), 0);
        o.discount = Product.number(document.get("discount"), 0);
        o.total = Product.number(document.get("total"), 0);
        o.paymentMethod = text(document.getString("paymentMethod"));
        o.deliveryAddress = text(document.getString("deliveryAddress"));
        o.status = document.getString("status") == null ? STATUS_PLACED : document.getString("status");
        o.reviewed = Boolean.TRUE.equals(document.getBoolean("reviewed"));

        Timestamp created = document.getTimestamp("createdAt");
        o.createdAt = created == null ? System.currentTimeMillis() : created.toDate().getTime();
        Timestamp updated = document.getTimestamp("updatedAt");
        o.updatedAt = updated == null ? o.createdAt : updated.toDate().getTime();

        Object rawItems = document.get("items");
        if (rawItems instanceof List) {
            for (Object raw : (List<Object>) rawItems) {
                if (!(raw instanceof Map)) {
                    continue;
                }
                Map<String, Object> map = (Map<String, Object>) raw;
                Item item = new Item();
                item.productId = map.get("productId") == null ? "" : String.valueOf(map.get("productId"));
                item.title = map.get("title") == null ? "Item" : String.valueOf(map.get("title"));
                item.unitPrice = Product.number(map.get("unitPrice"), 0);
                item.quantity = (int) Math.max(1, Product.number(map.get("quantity"), 1));
                item.category = map.get("category") == null ? "" : String.valueOf(map.get("category"));
                o.items.add(item);
            }
        }

        return o;
    }

    private static String text(String value) {
        return value == null ? "" : value;
    }
}
