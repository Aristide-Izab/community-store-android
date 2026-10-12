package com.example.communitystore.shared.cart;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.communitystore.R;
import com.example.communitystore.core.access.UserRole;
import com.example.communitystore.core.session.SessionManager;
import com.example.communitystore.shared.checkout.CheckoutActivity;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartActivity extends AppCompatActivity {

    private static final int DELIVERY_FEE = 50;

    private LinearLayout cartHeader;
    private LinearLayout proceedButton;
    private RecyclerView cartRecyclerView;
    private TextView emptyCartText;
    private TextView editButton;
    private TextView subtotalText;
    private TextView deliveryText;
    private TextView totalText;
    private EditText noteEditText;

    private CartManager cartManager;
    private CartAdapter cartAdapter;
    private int themeColor;
    private int currentTotal = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartHeader = findViewById(R.id.cartHeader);
        proceedButton = findViewById(R.id.proceedButton);
        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        emptyCartText = findViewById(R.id.emptyCartText);
        editButton = findViewById(R.id.editButton);
        subtotalText = findViewById(R.id.subtotalText);
        deliveryText = findViewById(R.id.deliveryText);
        totalText = findViewById(R.id.totalText);
        noteEditText = findViewById(R.id.noteEditText);

        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        applyAccountTheme();

        cartManager = new CartManager(this);
        setupCartList();
        updateTotals();

        editButton.setOnClickListener(v -> toggleEditMode());

        proceedButton.setOnClickListener(v -> {
            if (cartAdapter.getItemCount() == 0) {
                Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(this, CheckoutActivity.class);
            intent.putExtra("totalAmount", currentTotal);
            intent.putExtra("subtotalAmount", getSubtotal());
            intent.putExtra("deliveryAmount", DELIVERY_FEE);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (cartAdapter != null && cartManager != null) {
            List<CartItem> latest = cartManager.getItems();
            cartAdapter.getItems().clear();
            cartAdapter.getItems().addAll(latest);
            cartAdapter.notifyDataSetChanged();
            updateTotals();
        }
    }

    private void setupCartList() {
        List<CartItem> items = new ArrayList<>(cartManager.getItems());

        cartAdapter = new CartAdapter(items, themeColor, () -> {
            cartManager.saveItems(cartAdapter.getItems());
            updateTotals();
        });

        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartRecyclerView.setAdapter(cartAdapter);

        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                0
        ) {
            @Override
            public boolean isLongPressDragEnabled() {
                return cartAdapter.isEditMode();
            }

            @Override
            public boolean onMove(
                    RecyclerView recyclerView,
                    RecyclerView.ViewHolder viewHolder,
                    RecyclerView.ViewHolder target
            ) {
                return cartAdapter.moveItem(
                        viewHolder.getBindingAdapterPosition(),
                        target.getBindingAdapterPosition()
                );
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                // Swipe delete intentionally disabled. Use the bin button instead.
            }
        };

        new ItemTouchHelper(callback).attachToRecyclerView(cartRecyclerView);
    }

    private void toggleEditMode() {
        boolean editMode = !cartAdapter.isEditMode();
        cartAdapter.setEditMode(editMode);
        editButton.setText(editMode ? "Done" : "Edit");

        if (editMode && cartAdapter.getItemCount() > 1) {
            Toast.makeText(
                    this,
                    "Hold and drag a product to reorder it",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void updateTotals() {
        int subtotal = getSubtotal();
        int delivery = subtotal > 0 ? DELIVERY_FEE : 0;
        currentTotal = subtotal + delivery;

        subtotalText.setText(formatPrice(subtotal));
        deliveryText.setText(formatPrice(delivery));
        totalText.setText(formatPrice(currentTotal));

        boolean isEmpty = cartAdapter == null || cartAdapter.getItemCount() == 0;
        emptyCartText.setVisibility(isEmpty ? TextView.VISIBLE : TextView.GONE);
        editButton.setEnabled(!isEmpty);
        editButton.setAlpha(isEmpty ? 0.45f : 1f);
    }

    private int getSubtotal() {
        int subtotal = 0;
        if (cartAdapter != null) {
            for (CartItem item : cartAdapter.getItems()) {
                subtotal += item.getLineTotal();
            }
        }
        return subtotal;
    }

    private String formatPrice(int amount) {
        NumberFormat formatter = NumberFormat.getIntegerInstance(Locale.US);
        return "R" + formatter.format(amount);
    }

    private void applyAccountTheme() {
        SessionManager sessionManager = new SessionManager(this);
        themeColor = getThemeColor(sessionManager.getUserRole());

        ViewCompat.setBackgroundTintList(
                cartHeader,
                ColorStateList.valueOf(themeColor)
        );

        ViewCompat.setBackgroundTintList(
                proceedButton,
                ColorStateList.valueOf(themeColor)
        );

        getWindow().setStatusBarColor(themeColor);
    }

    private int getThemeColor(UserRole role) {
        if (role == null) return Color.parseColor("#168FC1");

        switch (role) {
            case STUDENT:
                return Color.parseColor("#168FC1");
            case FACULTY:
                return Color.parseColor("#FF4B2B");
            case VENDOR:
                return Color.parseColor("#FFA726");
            case RESIDENCE:
                return Color.parseColor("#4B00D1");
            default:
                return Color.parseColor("#168FC1");
        }
    }
}
