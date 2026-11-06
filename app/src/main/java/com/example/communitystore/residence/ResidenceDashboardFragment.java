package com.example.communitystore.residence;

import com.example.communitystore.R;
import com.example.communitystore.core.session.AuthSession;
import com.example.communitystore.shared.data.Formatters;
import com.example.communitystore.shared.data.Order;
import com.example.communitystore.shared.data.OrderRepository;
import com.example.communitystore.shared.data.ProductRepository;
import com.example.communitystore.shared.data.UserDirectory;
import com.example.communitystore.shared.orders.OrderDetailActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

// home tab: greeting, points, sales totals and recent orders (all live)
public class ResidenceDashboardFragment extends Fragment {

    private static final int RECENT_ORDERS = 3;

    private TextView greetingText;
    private TextView productsCountText;
    private TextView totalSalesText;
    private TextView ordersCountText;
    private TextView loyaltyPointsText;
    private LinearLayout recentOrdersList;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {

        return inflater.inflate(
                R.layout.fragment_residence_dashboard,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {

        greetingText = view.findViewById(R.id.greetingText);
        productsCountText = view.findViewById(R.id.productsCountText);
        totalSalesText = view.findViewById(R.id.totalSalesText);
        ordersCountText = view.findViewById(R.id.ordersCountText);
        loyaltyPointsText = view.findViewById(R.id.loyaltyPointsText);
        recentOrdersList = view.findViewById(R.id.recentOrdersList);

        ((TextView) view.findViewById(R.id.loyaltyCaption))
                .setText("Loyalty Points\nEarned on delivered orders");

        greetingText.setText(
                ResidenceUi.greeting()
                        + ", "
                        + ResidenceUi.firstName(null)
        );

        ResidenceHomeActivity home =
                (ResidenceHomeActivity) requireActivity();

        view.findViewById(R.id.avatarButton)
                .setOnClickListener(v ->
                        home.selectTab(ResidenceHomeActivity.TAB_PROFILE)
                );

        view.findViewById(R.id.productsCard)
                .setOnClickListener(v ->
                        home.selectTab(ResidenceHomeActivity.TAB_LISTING)
                );

        view.findViewById(R.id.moreOrdersText)
                .setOnClickListener(v ->
                        home.selectTab(ResidenceHomeActivity.TAB_SALES)
                );

        view.findViewById(R.id.loyaltyCard)
                .setOnClickListener(v ->
                        startActivity(
                                new Intent(
                                        requireContext(),
                                        LeaderboardActivity.class
                                )
                        )
                );
    }

    @Override
    public void onResume() {
        super.onResume();
        load();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            load();
        }
    }

    private void load() {

        String uid = AuthSession.uid();

        if (uid == null) {
            return;
        }

        ResidenceUi.loadProfile(profile -> {

            if (!isAdded()
                    || profile == null) {
                return;
            }

            greetingText.setText(
                    ResidenceUi.greeting()
                            + ", "
                            + ResidenceUi.firstName(
                            profile.getString("displayName")
                    )
            );

            loyaltyPointsText.setText(
                    NumberFormat.getIntegerInstance(Locale.US)
                            .format(UserDirectory.points(profile))
            );
        });

        ProductRepository.loadActiveBySeller(uid, (products, error) -> {
            if (isAdded()) {
                productsCountText.setText(String.valueOf(products.size()));
            }
        });

        OrderRepository.loadAsSeller(uid, (orders, error) -> {
            if (!isAdded()) {
                return;
            }

            int count = 0;
            for (Order order : orders) {
                if (!Order.STATUS_CANCELLED.equals(order.status)) {
                    count++;
                }
            }

            totalSalesText.setText(Formatters.price(OrderRepository.sales(orders)));
            ordersCountText.setText(String.valueOf(count));
            showRecentOrders(orders);
        });
    }

    private void showRecentOrders(
            List<Order> orders
    ) {

        recentOrdersList.removeAllViews();

        LayoutInflater inflater =
                LayoutInflater.from(requireContext());

        if (orders.isEmpty()) {
            TextView empty = new TextView(requireContext());
            empty.setText("No orders yet. Your sales will show here.");
            empty.setPadding(24, 24, 24, 24);
            recentOrdersList.addView(empty);
            return;
        }

        for (int i = 0; i < orders.size() && i < RECENT_ORDERS; i++) {

            Order order = orders.get(i);

            View row =
                    inflater.inflate(
                            R.layout.item_residence_product_row,
                            recentOrdersList,
                            false
                    );

            ((TextView) row.findViewById(R.id.productTitle))
                    .setText(order.firstTitle());

            ((TextView) row.findViewById(R.id.productPrice))
                    .setText(Formatters.price(order.total));

            TextView status =
                    row.findViewById(R.id.productStatus);

            status.setText(order.statusLabel());

            if (order.isOpen()) {

                status.setBackgroundResource(
                        R.drawable.bg_residence_status_processing
                );

                status.setTextColor(
                        ContextCompat.getColor(
                                requireContext(),
                                R.color.residence_orange
                        )
                );

            } else if (Order.STATUS_CANCELLED.equals(order.status)) {

                status.setBackgroundResource(
                        R.drawable.bg_residence_status_inactive
                );
            }

            row.setOnClickListener(v ->
                    OrderDetailActivity.open(requireContext(), order.id));

            recentOrdersList.addView(row);
        }
    }
}
