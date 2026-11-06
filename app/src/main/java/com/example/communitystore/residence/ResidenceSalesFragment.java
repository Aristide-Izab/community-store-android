package com.example.communitystore.residence;

import com.example.communitystore.R;
import com.example.communitystore.core.session.AuthSession;
import com.example.communitystore.shared.data.Formatters;
import com.example.communitystore.shared.data.Order;
import com.example.communitystore.shared.data.OrderRepository;
import com.example.communitystore.shared.orders.OrderDetailActivity;
import com.example.communitystore.shared.orders.SalesStats;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

// sales tab: income for the selected period, worked out from real orders
public class ResidenceSalesFragment extends Fragment {

    private static final int[] PERIODS = {SalesStats.WEEK, SalesStats.MONTH, SalesStats.YEAR};

    private static final float UNSELECTED_ALPHA =
            0.55f;

    private final List<Order> orders = new ArrayList<>();

    private TextView[] periodButtons;
    private int selectedPeriod = 1;

    private TextView totalIncomeText;
    private TextView incomeTrendText;
    private ImageView incomeTrendIcon;
    private TextView totalOrdersText;
    private TextView ordersTrendText;
    private TextView pendingPayoutText;
    private LinearLayout recentIncomeList;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {

        return inflater.inflate(
                R.layout.fragment_residence_sales,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {

        ResidenceHomeActivity home =
                (ResidenceHomeActivity) requireActivity();

        view.findViewById(R.id.backButton)
                .setOnClickListener(v ->
                        home.selectTab(ResidenceHomeActivity.TAB_HOME)
                );

        totalIncomeText = view.findViewById(R.id.totalIncomeText);
        incomeTrendText = view.findViewById(R.id.incomeTrendText);
        incomeTrendIcon = view.findViewById(R.id.incomeTrendIcon);
        totalOrdersText = view.findViewById(R.id.totalOrdersText);
        ordersTrendText = view.findViewById(R.id.ordersTrendText);
        pendingPayoutText = view.findViewById(R.id.pendingPayoutText);
        recentIncomeList = view.findViewById(R.id.recentIncomeList);

        periodButtons =
                new TextView[]{
                        view.findViewById(R.id.periodWeek),
                        view.findViewById(R.id.periodMonth),
                        view.findViewById(R.id.periodYear)
                };

        for (int i = 0; i < periodButtons.length; i++) {

            int period = i;

            periodButtons[i].setOnClickListener(
                    v -> selectPeriod(period)
            );
        }

        selectPeriod(selectedPeriod);
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
        OrderRepository.loadAsSeller(AuthSession.uid(), (list, error) -> {
            if (!isAdded()) {
                return;
            }
            orders.clear();
            orders.addAll(list);
            selectPeriod(selectedPeriod);
        });
    }

    private void selectPeriod(
            int period
    ) {

        selectedPeriod = period;

        for (int i = 0; i < periodButtons.length; i++) {

            boolean selected =
                    i == period;

            periodButtons[i].setSelected(selected);

            periodButtons[i].setAlpha(
                    selected
                            ? 1f
                            : UNSELECTED_ALPHA
            );
        }

        SalesStats stats = SalesStats.compute(orders, PERIODS[period]);

        totalIncomeText.setText(Formatters.price(stats.income));
        totalOrdersText.setText(String.valueOf(stats.orders));
        pendingPayoutText.setText(Formatters.price(stats.toFulfil));

        incomeTrendText.setText(SalesStats.changeText(stats.income, stats.previousIncome).replace("↑ ", "").replace("↓ ", ""));
        ordersTrendText.setText(SalesStats.changeText(stats.orders, stats.previousOrders).replace("↑ ", "").replace("↓ ", ""));

        Long change = SalesStats.percentChange(stats.income, stats.previousIncome);
        incomeTrendIcon.setVisibility(change == null ? View.GONE : View.VISIBLE);
        incomeTrendIcon.setRotationX(change != null && change < 0 ? 180f : 0f);

        showRecentIncome(stats.inPeriod);
    }

    private void showRecentIncome(List<Order> list) {

        recentIncomeList.removeAllViews();

        LayoutInflater inflater =
                LayoutInflater.from(requireContext());

        if (list.isEmpty()) {
            TextView empty = new TextView(requireContext());
            empty.setText("No orders in this period yet.");
            empty.setPadding(0, 24, 0, 24);
            recentIncomeList.addView(empty);
            return;
        }

        for (Order order : list) {

            View row =
                    inflater.inflate(
                            R.layout.item_residence_income_row,
                            recentIncomeList,
                            false
                    );

            ((TextView) row.findViewById(R.id.incomeTitle))
                    .setText(order.firstTitle() + " • " + order.statusLabel());

            ((TextView) row.findViewById(R.id.incomeDate))
                    .setText(Formatters.date(order.createdAt) + " • " + order.buyerName);

            ((TextView) row.findViewById(R.id.incomeAmount))
                    .setText(Order.STATUS_CANCELLED.equals(order.status)
                            ? "-" + Formatters.price(order.total)
                            : Formatters.price(order.total));

            row.setOnClickListener(v -> OrderDetailActivity.open(requireContext(), order.id));
            recentIncomeList.addView(row);
        }
    }
}
