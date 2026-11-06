package com.example.communitystore.shared.orders;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.communitystore.R;
import com.example.communitystore.core.session.AuthSession;
import com.example.communitystore.shared.data.Formatters;
import com.example.communitystore.shared.data.Order;
import com.example.communitystore.shared.data.OrderRepository;
import com.example.communitystore.shared.data.Product;
import com.example.communitystore.shared.data.SampleProducts;
import com.example.communitystore.shared.utils.ThemeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Orders list for every account type.
 *   Purchases: "My Orders" (things the user bought)
 *   Sales:     "My Sales" (orders other users placed with this seller)
 */
public class OrdersActivity extends AppCompatActivity {

    private static final String EXTRA_SALES = "sales";

    private final List<Order> all = new ArrayList<>();
    private final List<Order> visible = new ArrayList<>();

    private boolean salesMode;
    private boolean showActive = true;

    private TextView tabActive;
    private TextView tabCompleted;
    private TextView emptyText;
    private Adapter adapter;

    public static void openPurchases(Context context) {
        Intent intent = new Intent(context, OrdersActivity.class);
        intent.putExtra(EXTRA_SALES, false);
        context.startActivity(intent);
    }

    public static void openSales(Context context) {
        Intent intent = new Intent(context, OrdersActivity.class);
        intent.putExtra(EXTRA_SALES, true);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        salesMode = getIntent().getBooleanExtra(EXTRA_SALES, false);

        findViewById(R.id.ordersRoot).setBackgroundColor(ThemeUtils.primary(this));
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        TextView title = findViewById(R.id.ordersTitle);
        title.setText(salesMode ? "My Sales" : "My Orders");

        findViewById(R.id.salesSummary).setVisibility(salesMode ? View.VISIBLE : View.GONE);

        tabActive = findViewById(R.id.tabActive);
        tabCompleted = findViewById(R.id.tabCompleted);
        emptyText = findViewById(R.id.ordersEmpty);

        tabActive.setOnClickListener(v -> showTab(true));
        tabCompleted.setOnClickListener(v -> showTab(false));

        RecyclerView recycler = findViewById(R.id.ordersRecycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter();
        recycler.setAdapter(adapter);

        styleTabs();
    }

    @Override
    protected void onResume() {
        super.onResume();
        load();
    }

    private void load() {
        String uid = AuthSession.uid();

        OrderRepository.ListCallback callback = (orders, error) -> {
            all.clear();
            all.addAll(orders);

            if (salesMode) {
                bindSummary();
            }

            refresh();

            if (error != null && all.isEmpty()) {
                emptyText.setText("Couldn't load orders. Check your connection and try again.");
                emptyText.setVisibility(View.VISIBLE);
            }
        };

        if (salesMode) {
            OrderRepository.loadAsSeller(uid, callback);
        } else {
            OrderRepository.loadAsBuyer(uid, callback);
        }
    }

    private void bindSummary() {
        int open = 0;
        for (Order order : all) {
            if (order.isOpen()) {
                open++;
            }
        }

        ((TextView) findViewById(R.id.summarySales)).setText(Formatters.price(OrderRepository.sales(all)));
        ((TextView) findViewById(R.id.summaryEarned)).setText(Formatters.price(OrderRepository.revenue(all)));
        ((TextView) findViewById(R.id.summaryOpen)).setText(String.valueOf(open));
    }

    private void showTab(boolean active) {
        showActive = active;
        styleTabs();
        refresh();
    }

    private void styleTabs() {
        int color = ThemeUtils.primary(this);
        style(tabActive, showActive, color);
        style(tabCompleted, !showActive, color);
    }

    private void style(TextView tab, boolean selected, int color) {
        tab.setBackground(ThemeUtils.rounded(
                this,
                selected ? color : Color.parseColor("#E5E7EB"),
                Color.TRANSPARENT,
                19
        ));
        tab.setTextColor(selected ? Color.WHITE : Color.parseColor("#333333"));
    }

    private void refresh() {
        visible.clear();

        for (Order order : all) {
            if (order.isOpen() == showActive) {
                visible.add(order);
            }
        }

        adapter.notifyDataSetChanged();

        if (visible.isEmpty()) {
            if (salesMode) {
                emptyText.setText(showActive
                        ? "No orders to fulfil right now."
                        : "Completed and cancelled sales will appear here.");
            } else {
                emptyText.setText(showActive
                        ? "You have no active orders. Find something in the Market!"
                        : "Delivered and cancelled orders will appear here.");
            }
            emptyText.setVisibility(View.VISIBLE);
        } else {
            emptyText.setVisibility(View.GONE);
        }
    }

    /** Image for an order: the sample photo if it was a sample listing, else a placeholder. */
    static int imageFor(Order order) {
        if (!order.items.isEmpty()) {
            Product sample = SampleProducts.byId(order.items.get(0).productId);
            if (sample != null) {
                return sample.primaryImage();
            }
        }
        return R.drawable.ic_product_placeholder;
    }

    static int statusColor(Context context, Order order) {
        if (Order.STATUS_DELIVERED.equals(order.status)) {
            return Color.parseColor("#10B981");
        }
        if (Order.STATUS_CANCELLED.equals(order.status)) {
            return Color.parseColor("#C62828");
        }
        return ThemeUtils.primary(context);
    }

    private class Adapter extends RecyclerView.Adapter<Adapter.Holder> {

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            Order order = visible.get(position);
            Context context = holder.itemView.getContext();

            holder.image.setImageResource(imageFor(order));
            holder.title.setText(order.firstTitle());
            holder.meta.setText(order.reference() + " • " + Formatters.price(order.total));
            holder.party.setText(salesMode
                    ? "Buyer: " + order.buyerName + " • " + Formatters.date(order.createdAt)
                    : "From " + order.sellerName + " • " + Formatters.date(order.createdAt));
            holder.status.setText("Status: " + order.statusLabel());
            holder.status.setTextColor(statusColor(context, order));

            holder.itemView.setOnClickListener(v -> OrderDetailActivity.open(context, order.id));
        }

        @Override
        public int getItemCount() {
            return visible.size();
        }

        class Holder extends RecyclerView.ViewHolder {
            final ImageView image;
            final TextView title;
            final TextView meta;
            final TextView party;
            final TextView status;

            Holder(View view) {
                super(view);
                image = view.findViewById(R.id.orderImage);
                title = view.findViewById(R.id.orderTitle);
                meta = view.findViewById(R.id.orderMeta);
                party = view.findViewById(R.id.orderParty);
                status = view.findViewById(R.id.orderStatus);
            }
        }
    }
}
