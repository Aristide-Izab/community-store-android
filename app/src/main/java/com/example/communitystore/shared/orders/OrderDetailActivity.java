package com.example.communitystore.shared.orders;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.communitystore.R;
import com.example.communitystore.core.session.AuthSession;
import com.example.communitystore.shared.chat.ChatActivity;
import com.example.communitystore.shared.data.Formatters;
import com.example.communitystore.shared.data.Order;
import com.example.communitystore.shared.data.OrderRepository;
import com.example.communitystore.shared.data.SampleProducts;
import com.example.communitystore.shared.utils.ThemeUtils;
import com.google.android.material.button.MaterialButton;

/**
 * Order page with the 4-step tracker. The buyer can message the seller,
 * cancel before packing and review after delivery; the seller moves the
 * order through Packed -> Out for delivery -> Delivered.
 */
public class OrderDetailActivity extends AppCompatActivity {

    private static final String EXTRA_ORDER_ID = "orderId";

    private static final int DONE_COLOR = Color.parseColor("#10B981");
    private static final int TODO_COLOR = Color.parseColor("#374151");

    private String orderId;
    private Order order;

    private TextView tvOrderNumber;
    private TextView tvStatusHeader;
    private TextView tvEstimatedLabel;
    private TextView tvEstimatedDelivery;
    private TextView tvParty;
    private TextView tvPayment;
    private TextView tvShippingAddress;
    private TextView tvProductName;
    private TextView tvProductPrice;
    private TextView tvItemsList;
    private TextView tvOrderNote;
    private TextView btnCancelOrder;
    private ImageView imgProduct;
    private MaterialButton primaryButton;
    private MaterialButton secondaryButton;

    private final ImageView[] stepNodes = new ImageView[4];
    private final View[] stepLines = new View[3];

    public static void open(Context context, String orderId) {
        Intent intent = new Intent(context, OrderDetailActivity.class);
        intent.putExtra(EXTRA_ORDER_ID, orderId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        orderId = getIntent().getStringExtra(EXTRA_ORDER_ID);

        if (orderId == null) {
            finish();
            return;
        }

        int color = ThemeUtils.primary(this);
        findViewById(R.id.orderRoot).setBackgroundColor(color);
        findViewById(R.id.topAppBar).setBackgroundColor(color);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        tvOrderNumber = findViewById(R.id.tvOrderNumber);
        tvStatusHeader = findViewById(R.id.tvStatusHeader);
        tvEstimatedLabel = findViewById(R.id.tvEstimatedLabel);
        tvEstimatedDelivery = findViewById(R.id.tvEstimatedDelivery);
        tvParty = findViewById(R.id.tvParty);
        tvPayment = findViewById(R.id.tvPayment);
        tvShippingAddress = findViewById(R.id.tvShippingAddress);
        tvProductName = findViewById(R.id.tvProductName);
        tvProductPrice = findViewById(R.id.tvProductPrice);
        tvItemsList = findViewById(R.id.tvItemsList);
        tvOrderNote = findViewById(R.id.tvOrderNote);
        btnCancelOrder = findViewById(R.id.btnCancelOrder);
        imgProduct = findViewById(R.id.imgProduct);
        primaryButton = findViewById(R.id.btnLeaveReview);
        secondaryButton = findViewById(R.id.btnContactSeller);

        stepNodes[0] = findViewById(R.id.step1Node);
        stepNodes[1] = findViewById(R.id.step2Node);
        stepNodes[2] = findViewById(R.id.step3Node);
        stepNodes[3] = findViewById(R.id.step4Node);
        stepLines[0] = findViewById(R.id.stepLine1);
        stepLines[1] = findViewById(R.id.stepLine2);
        stepLines[2] = findViewById(R.id.stepLine3);

        primaryButton.setBackgroundTintList(ColorStateList.valueOf(color));
        secondaryButton.setTextColor(color);
        secondaryButton.setStrokeColor(ColorStateList.valueOf(color));

        btnCancelOrder.setOnClickListener(v -> confirmCancel());
    }

    @Override
    protected void onResume() {
        super.onResume();
        load();
    }

    private void load() {
        OrderRepository.get(orderId)
                .addOnSuccessListener(document -> {
                    if (!document.exists()) {
                        Toast.makeText(this, "This order no longer exists.", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }
                    order = Order.fromDocument(document);
                    bind();
                })
                .addOnFailureListener(e -> Toast.makeText(
                        this,
                        "Couldn't load the order: " + e.getMessage(),
                        Toast.LENGTH_LONG
                ).show());
    }

    private boolean isSeller() {
        String uid = AuthSession.uid();
        return uid != null && uid.equals(order.sellerId);
    }

    private boolean isSampleOrder() {
        return SampleProducts.SAMPLE_SELLER_ID.equals(order.sellerId);
    }

    private void bind() {
        tvOrderNumber.setText(order.reference());
        tvStatusHeader.setText(order.statusLabel());
        tvEstimatedLabel.setText("Placed: ");
        tvEstimatedDelivery.setText(Formatters.dateTime(order.createdAt));

        tvParty.setText(isSeller()
                ? "Buyer: " + order.buyerName
                : "Seller: " + order.sellerName);
        tvPayment.setText("Paid with " + order.paymentMethod + " • Total " + Formatters.price(order.total));
        tvShippingAddress.setText(order.deliveryAddress.isEmpty() ? "Collect on campus" : order.deliveryAddress);

        imgProduct.setImageResource(OrdersActivity.imageFor(order));
        tvProductName.setText(order.firstTitle());
        tvProductPrice.setText(order.itemCount() + (order.itemCount() == 1 ? " item" : " items")
                + " • " + Formatters.price(order.subtotal));

        StringBuilder lines = new StringBuilder();
        for (Order.Item item : order.items) {
            if (lines.length() > 0) {
                lines.append('\n');
            }
            lines.append(item.quantity).append(" × ").append(item.title)
                    .append(" — ").append(Formatters.price(item.lineTotal()));
        }
        if (order.delivery > 0) {
            lines.append("\nDelivery — ").append(Formatters.price(order.delivery));
        }
        if (order.discount > 0) {
            lines.append("\nVoucher — -").append(Formatters.price(order.discount));
        }
        tvItemsList.setText(lines.toString());

        bindSteps();
        bindActions();
    }

    private void bindSteps() {
        int step = order.step();

        for (int i = 0; i < stepNodes.length; i++) {
            stepNodes[i].setImageResource(i <= step ? R.drawable.ic_step_active : R.drawable.ic_step_inactive);
        }
        for (int i = 0; i < stepLines.length; i++) {
            stepLines[i].setBackgroundColor(i < step ? DONE_COLOR : TODO_COLOR);
        }

        if (step < 0) {
            tvStatusHeader.setTextColor(Color.parseColor("#C62828"));
        }
    }

    private void bindActions() {
        btnCancelOrder.setVisibility(View.GONE);
        primaryButton.setVisibility(View.VISIBLE);
        primaryButton.setEnabled(true);
        primaryButton.setAlpha(1f);
        tvOrderNote.setText(order.paymentMethod.contains("simulated")
                ? "Payment was simulated for this demo. No money was taken."
                : "");

        if (isSeller()) {
            secondaryButton.setText("Message Buyer");
            secondaryButton.setOnClickListener(v -> ChatActivity.open(
                    this, order.buyerId, order.buyerName, "",
                    order.firstTitle(), null));

            String next = order.nextStatus();

            if (next == null) {
                disablePrimary(Order.STATUS_CANCELLED.equals(order.status) ? "Cancelled" : "Completed ✓");
            } else {
                primaryButton.setText(Order.actionFor(next));
                primaryButton.setOnClickListener(v -> advance(next));
            }
            return;
        }

        secondaryButton.setText("Message Seller");
        secondaryButton.setOnClickListener(v -> ChatActivity.open(
                this, order.sellerId, order.sellerName, order.sellerRole,
                order.firstTitle(), "Hi! I have a question about " + order.reference() + "."));

        if (Order.STATUS_PLACED.equals(order.status)) {
            btnCancelOrder.setVisibility(View.VISIBLE);
        }

        if (Order.STATUS_DELIVERED.equals(order.status)) {
            if (order.reviewed) {
                disablePrimary("Reviewed ✓");
            } else {
                primaryButton.setText("Leave a Review");
                primaryButton.setOnClickListener(v -> ReviewActivity.open(this, order));
            }
            return;
        }

        if (Order.STATUS_CANCELLED.equals(order.status)) {
            disablePrimary("Cancelled");
            return;
        }

        if (isSampleOrder()) {
            String next = order.nextStatus();
            primaryButton.setText("Next step (demo)");
            primaryButton.setOnClickListener(v -> advance(next));
            tvOrderNote.setText("Sample listing: there is no real seller, so you can move this order "
                    + "through each step yourself. Payment was simulated.");
            return;
        }

        disablePrimary("Waiting for seller");
    }

    private void disablePrimary(String text) {
        primaryButton.setText(text);
        primaryButton.setEnabled(false);
        primaryButton.setAlpha(0.6f);
        primaryButton.setOnClickListener(null);
    }

    private void advance(String next) {
        if (next == null) {
            return;
        }

        primaryButton.setEnabled(false);

        OrderRepository.updateStatus(order, next)
                .addOnSuccessListener(unused -> {
                    if (Order.STATUS_DELIVERED.equals(next) && isSeller()) {
                        Toast.makeText(
                                this,
                                "Order delivered! +" + OrderRepository.sellerPointsFor(order.total) + " points",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                    load();
                })
                .addOnFailureListener(e -> {
                    primaryButton.setEnabled(true);
                    Toast.makeText(this, "Couldn't update: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void confirmCancel() {
        new AlertDialog.Builder(this)
                .setTitle("Cancel this order?")
                .setMessage("The seller hasn't packed it yet, so you can still cancel.")
                .setPositiveButton("Cancel order", (dialog, which) ->
                        OrderRepository.cancel(order)
                                .addOnSuccessListener(unused -> load())
                                .addOnFailureListener(e -> Toast.makeText(
                                        this, "Couldn't cancel: " + e.getMessage(), Toast.LENGTH_LONG
                                ).show()))
                .setNegativeButton("Keep order", null)
                .show();
    }
}
