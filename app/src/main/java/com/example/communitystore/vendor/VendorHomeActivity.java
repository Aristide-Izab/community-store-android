package com.example.communitystore.vendor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.communitystore.R;
import com.example.communitystore.core.session.AuthSession;
import com.example.communitystore.core.session.SessionManager;
import com.example.communitystore.shared.data.Formatters;
import com.example.communitystore.shared.data.Order;
import com.example.communitystore.shared.data.OrderRepository;
import com.example.communitystore.shared.data.ProductRepository;
import com.example.communitystore.shared.data.UserDirectory;
import com.example.communitystore.shared.orders.OrdersActivity;
import com.example.communitystore.shared.product.AddListingActivity;
import com.example.communitystore.shared.product.MyListingsActivity;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/** Vendor dashboard: points, sales, orders, products and recent orders. */
public class VendorHomeActivity extends AppCompatActivity {

    private TextView greetingText;
    private TextView productCountText;
    private TextView pointsText;
    private TextView salesTotalText;
    private TextView ordersCountText;
    private LinearLayout recentOrdersList;
    private TextView recentOrdersEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_home);

        greetingText = findViewById(R.id.greetingText);
        productCountText = findViewById(R.id.productCountText);
        pointsText = findViewById(R.id.pointsText);
        salesTotalText = findViewById(R.id.salesTotalText);
        ordersCountText = findViewById(R.id.ordersCountText);
        recentOrdersList = findViewById(R.id.recentOrdersList);
        recentOrdersEmpty = findViewById(R.id.recentOrdersEmpty);

        ((TextView) findViewById(R.id.pointsHint)).setText("Earn points for every delivered order");

        String cached = new SessionManager(this).getDisplayName();
        greetingText.setText(Formatters.greeting() + ", " + (cached.isEmpty() ? "Vendor" : cached));

        findViewById(R.id.profileButton).setOnClickListener(v ->
                startActivity(new Intent(this, VendorProfileActivity.class)));
        findViewById(R.id.viewRewardsButton).setOnClickListener(v ->
                startActivity(new Intent(this, VendorLeaderboardActivity.class)));
        findViewById(R.id.productsStatCard).setOnClickListener(v -> MyListingsActivity.open(this));
        findViewById(R.id.incomeStatCard).setOnClickListener(v ->
                startActivity(new Intent(this, VendorIncomeActivity.class)));
        findViewById(R.id.messagesQuickCard).setOnClickListener(v ->
                startActivity(new Intent(this, VendorMessagesActivity.class)));
        findViewById(R.id.addProductQuickCard).setOnClickListener(v -> AddListingActivity.openNew(this));
        findViewById(R.id.leaderboardQuickCard).setOnClickListener(v ->
                startActivity(new Intent(this, VendorLeaderboardActivity.class)));
        findViewById(R.id.moreOrdersButton).setOnClickListener(v -> OrdersActivity.openSales(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        load();
    }

    private void load() {
        String uid = AuthSession.uid();

        UserDirectory.loadCurrent(profile -> {
            if (profile == null) {
                return;
            }
            greetingText.setText(Formatters.greeting() + ", " + UserDirectory.displayName(profile, "Vendor"));
            pointsText.setText(NumberFormat.getIntegerInstance(Locale.US).format(UserDirectory.points(profile)));
        });

        ProductRepository.loadActiveBySeller(uid, (products, error) ->
                productCountText.setText(String.valueOf(products.size())));

        OrderRepository.loadAsSeller(uid, (orders, error) -> {
            salesTotalText.setText(Formatters.price(OrderRepository.sales(orders)));
            ordersCountText.setText(String.valueOf(countNotCancelled(orders)));
            showRecent(orders);
        });
    }

    private static int countNotCancelled(List<Order> orders) {
        int count = 0;
        for (Order order : orders) {
            if (!Order.STATUS_CANCELLED.equals(order.status)) {
                count++;
            }
        }
        return count;
    }

    private void showRecent(List<Order> orders) {
        recentOrdersList.removeAllViews();

        for (int i = 0; i < orders.size() && i < 3; i++) {
            recentOrdersList.addView(VendorUi.orderRow(this, orders.get(i)));
        }

        recentOrdersEmpty.setVisibility(orders.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
