package com.example.communitystore.vendor;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.communitystore.R;
import com.example.communitystore.core.session.AuthSession;
import com.example.communitystore.shared.data.Formatters;
import com.example.communitystore.shared.data.Order;
import com.example.communitystore.shared.data.OrderRepository;
import com.example.communitystore.shared.orders.OrdersActivity;
import com.example.communitystore.shared.orders.SalesStats;

import java.util.ArrayList;
import java.util.List;

/**
 * Income / revenue for a period (this week, month or year), worked out from
 * the vendor's orders. Income = delivered orders; "To fulfil" = open orders.
 */
public class VendorIncomeActivity extends AppCompatActivity {

    private static final int PERIOD_WEEK = SalesStats.WEEK;
    private static final int PERIOD_MONTH = SalesStats.MONTH;
    private static final int PERIOD_YEAR = SalesStats.YEAR;

    private final List<Order> orders = new ArrayList<>();

    private TextView totalIncomeValue;
    private TextView totalOrdersValue;
    private TextView pendingPayoutValue;
    private TextView incomeChangeText;
    private TextView ordersChangeText;
    private LinearLayout incomeList;
    private TextView incomeEmpty;
    private TextView weekButton;
    private TextView monthButton;
    private TextView yearButton;

    private int period = PERIOD_MONTH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_income);

        totalIncomeValue = findViewById(R.id.totalIncomeValue);
        totalOrdersValue = findViewById(R.id.totalOrdersValue);
        pendingPayoutValue = findViewById(R.id.pendingPayoutValue);
        incomeChangeText = findViewById(R.id.incomeChangeText);
        ordersChangeText = findViewById(R.id.ordersChangeText);
        incomeList = findViewById(R.id.incomeList);
        incomeEmpty = findViewById(R.id.incomeEmpty);
        weekButton = findViewById(R.id.weekButton);
        monthButton = findViewById(R.id.monthButton);
        yearButton = findViewById(R.id.yearButton);

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
        findViewById(R.id.viewAllSalesButton).setOnClickListener(v -> OrdersActivity.openSales(this));

        weekButton.setOnClickListener(v -> show(PERIOD_WEEK));
        monthButton.setOnClickListener(v -> show(PERIOD_MONTH));
        yearButton.setOnClickListener(v -> show(PERIOD_YEAR));
    }

    @Override
    protected void onResume() {
        super.onResume();

        OrderRepository.loadAsSeller(AuthSession.uid(), (list, error) -> {
            orders.clear();
            orders.addAll(list);
            show(period);
        });
    }

    /** Start of the current period (offset 0) or the previous one (offset -1). */


    private void show(int newPeriod) {
        period = newPeriod;
        styleButtons();

        SalesStats stats = SalesStats.compute(orders, period);

        totalIncomeValue.setText(Formatters.price(stats.income));
        totalOrdersValue.setText(String.valueOf(stats.orders));
        pendingPayoutValue.setText(Formatters.price(stats.toFulfil));
        incomeChangeText.setText(SalesStats.changeText(stats.income, stats.previousIncome) + " from previous period");
        ordersChangeText.setText(SalesStats.changeText(stats.orders, stats.previousOrders));

        incomeList.removeAllViews();
        for (Order order : stats.inPeriod) {
            incomeList.addView(VendorUi.orderRow(this, order));
        }
        incomeEmpty.setVisibility(stats.inPeriod.isEmpty() ? View.VISIBLE : View.GONE);
    }



    private void styleButtons() {
        style(weekButton, period == PERIOD_WEEK);
        style(monthButton, period == PERIOD_MONTH);
        style(yearButton, period == PERIOD_YEAR);
    }

    private void style(TextView button, boolean selected) {
        if (button == null) {
            return;
        }
        button.setBackgroundTintList(ColorStateList.valueOf(
                selected ? getColor(R.color.role_vendor) : Color.parseColor("#FFF0D9")));
        button.setTextColor(selected ? Color.WHITE : Color.parseColor("#111111"));
    }
}
