package com.example.communitystore.shared.checkout;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import com.example.communitystore.R;
import com.example.communitystore.core.access.UserRole;
import com.example.communitystore.core.session.SessionManager;
import com.example.communitystore.shared.cart.CartItem;
import com.example.communitystore.shared.cart.CartManager;
import com.example.communitystore.shared.data.OrderRepository;
import com.example.communitystore.shared.data.VoucherRepository;
import com.example.communitystore.shared.profile.ProfileActions;
import com.example.communitystore.shared.orders.OrdersActivity;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CheckoutActivity extends AppCompatActivity {

    private enum PaymentMethod {
        SAVED_CARD,
        NEW_CARD,
        GOOGLE_PAY
    }

    private LinearLayout checkoutHeader;
    private LinearLayout googlePayButton;
    private LinearLayout savedMethodsContainer;
    private LinearLayout addNewCardSection;
    private LinearLayout saveCardButton;
    private LinearLayout payButton;

    private TextView savedMethodsEditButton;
    private TextView addNewCardRadio;
    private TextView orderAmountText;
    private TextView payButtonText;
    private TextView voucherStatusText;

    private EditText voucherEditText;
    private EditText cardNumberEditText;
    private EditText expiryEditText;
    private EditText cvvEditText;
    private EditText cardNameEditText;

    private CardManager cardManager;
    private PaymentMethod selectedPaymentMethod = PaymentMethod.SAVED_CARD;
    private String selectedSavedCardId;
    private boolean savedCardsEditMode = false;

    private static final int DEFAULT_DELIVERY_FEE = 50;

    private int originalAmount = 0;
    private int totalAmount = 0;
    private int deliveryAmount = DEFAULT_DELIVERY_FEE;
    private boolean placingOrder = false;
    private String appliedVoucherCode = null;
    private final java.util.Map<String, VoucherRepository.Voucher> userVouchers = new java.util.HashMap<>();
    private int themeColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        checkoutHeader = findViewById(R.id.checkoutHeader);
        googlePayButton = findViewById(R.id.googlePayButton);
        savedMethodsContainer = findViewById(R.id.savedMethodsContainer);
        addNewCardSection = findViewById(R.id.addNewCardSection);
        saveCardButton = findViewById(R.id.saveCardButton);
        payButton = findViewById(R.id.payButton);

        savedMethodsEditButton = findViewById(R.id.savedMethodsEditButton);
        addNewCardRadio = findViewById(R.id.addNewCardRadio);
        orderAmountText = findViewById(R.id.orderAmountText);
        payButtonText = findViewById(R.id.payButtonText);
        voucherStatusText = findViewById(R.id.voucherStatusText);

        voucherEditText = findViewById(R.id.voucherEditText);
        cardNumberEditText = findViewById(R.id.cardNumberEditText);
        expiryEditText = findViewById(R.id.expiryEditText);
        cvvEditText = findViewById(R.id.cvvEditText);
        cardNameEditText = findViewById(R.id.cardNameEditText);

        cardManager = new CardManager(this);

        loadAmount();
        VoucherRepository.loadUnused(vouchers -> userVouchers.putAll(vouchers));
        applyAccountTheme();
        setupSavedCards();
        setupClicks();
    }

    private void setupClicks() {
        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        googlePayButton.setOnClickListener(v -> processGooglePayPayment());

        savedMethodsEditButton.setOnClickListener(v -> {
            savedCardsEditMode = !savedCardsEditMode;
            savedMethodsEditButton.setText(savedCardsEditMode ? "Done" : "Edit");
            renderSavedCards();
        });

        addNewCardSection.setOnClickListener(v -> selectNewCard());

        saveCardButton.setOnClickListener(v -> saveNewCardFromForm());

        View.OnFocusChangeListener newCardFocusListener = (view, hasFocus) -> {
            if (hasFocus) selectNewCard();
        };

        cardNumberEditText.setOnFocusChangeListener(newCardFocusListener);
        expiryEditText.setOnFocusChangeListener(newCardFocusListener);
        cvvEditText.setOnFocusChangeListener(newCardFocusListener);
        cardNameEditText.setOnFocusChangeListener(newCardFocusListener);

        voucherEditText.setOnEditorActionListener((v, actionId, event) -> {
            applyVoucher();
            return false;
        });

        payButton.setOnClickListener(v -> handlePayment());
    }

    private void setupSavedCards() {
        List<SavedCard> cards = cardManager.getCards();

        if (!cards.isEmpty()) {
            selectedSavedCardId = cards.get(0).getId();
            selectedPaymentMethod = PaymentMethod.SAVED_CARD;
        } else {
            selectedPaymentMethod = PaymentMethod.NEW_CARD;
        }

        renderSavedCards();
        updatePaymentSelection();
    }

    private void renderSavedCards() {
        savedMethodsContainer.removeAllViews();
        List<SavedCard> cards = cardManager.getCards();

        TextView emptyText = findViewById(R.id.noSavedMethodsText);
        emptyText.setVisibility(cards.isEmpty() ? View.VISIBLE : View.GONE);
        savedMethodsEditButton.setVisibility(cards.isEmpty() ? View.GONE : View.VISIBLE);

        if (cards.isEmpty()) {
            savedCardsEditMode = false;
            savedMethodsEditButton.setText("Edit");
        }

        LayoutInflater inflater = LayoutInflater.from(this);

        for (SavedCard card : cards) {
            View row = inflater.inflate(R.layout.item_saved_card, savedMethodsContainer, false);

            ImageView brandImage = row.findViewById(R.id.savedCardBrandImage);
            TextView brandText = row.findViewById(R.id.savedCardBrandText);
            TextView lastFourText = row.findViewById(R.id.savedCardLastFourText);
            TextView radio = row.findViewById(R.id.savedCardRadio);
            ImageView deleteButton = row.findViewById(R.id.savedCardDeleteButton);

            boolean isMastercard = card.getBrand().equalsIgnoreCase("Mastercard");
            brandImage.setImageResource(
                    isMastercard ? R.drawable.ic_mastercard : R.drawable.ic_credit_card
            );

            brandText.setText(card.getBrand());
            lastFourText.setText("•••• " + card.getLastFour());

            boolean selected = selectedPaymentMethod == PaymentMethod.SAVED_CARD
                    && card.getId().equals(selectedSavedCardId);

            radio.setText(selected ? "●" : "○");
            radio.setTextColor(selected ? themeColor : Color.parseColor("#A5ABB3"));

            deleteButton.setVisibility(savedCardsEditMode ? View.VISIBLE : View.GONE);
            deleteButton.setOnClickListener(v -> removeSavedCard(card));

            row.setOnClickListener(v -> {
                if (savedCardsEditMode) return;
                selectedPaymentMethod = PaymentMethod.SAVED_CARD;
                selectedSavedCardId = card.getId();
                clearFocusFromCardFields();
                renderSavedCards();
                updatePaymentSelection();
            });

            savedMethodsContainer.addView(row);
        }
    }

    private void removeSavedCard(SavedCard card) {
        cardManager.removeCard(card.getId());

        if (card.getId().equals(selectedSavedCardId)) {
            List<SavedCard> remaining = cardManager.getCards();

            if (!remaining.isEmpty()) {
                selectedSavedCardId = remaining.get(0).getId();
                selectedPaymentMethod = PaymentMethod.SAVED_CARD;
            } else {
                selectedSavedCardId = null;
                selectedPaymentMethod = PaymentMethod.NEW_CARD;
            }
        }

        renderSavedCards();
        updatePaymentSelection();

        Toast.makeText(this, "Card removed", Toast.LENGTH_SHORT).show();
    }

    private void processGooglePayPayment() {
        selectedPaymentMethod = PaymentMethod.GOOGLE_PAY;
        selectedSavedCardId = null;

        clearFocusFromCardFields();
        renderSavedCards();
        updatePaymentSelection();
        applyVoucher();
        hideKeyboard();
        placeOrder("Google Pay (simulated)");
    }

    private void selectNewCard() {
        selectedPaymentMethod = PaymentMethod.NEW_CARD;
        selectedSavedCardId = null;
        renderSavedCards();
        updatePaymentSelection();
    }

    private void updatePaymentSelection() {
        boolean newCardSelected = selectedPaymentMethod == PaymentMethod.NEW_CARD;
        addNewCardRadio.setText(newCardSelected ? "●" : "○");
        addNewCardRadio.setTextColor(
                newCardSelected ? themeColor : Color.parseColor("#A5ABB3")
        );

        float gPayAlpha = selectedPaymentMethod == PaymentMethod.GOOGLE_PAY ? 1f : 0.92f;
        googlePayButton.setAlpha(gPayAlpha);
    }


    private void saveNewCardFromForm() {
        selectNewCard();

        if (!validateNewCardFields()) {
            showMissingCardDetailsDialog();
            return;
        }

        SavedCard newCard = saveNewCard();
        selectedSavedCardId = newCard.getId();
        selectedPaymentMethod = PaymentMethod.SAVED_CARD;
        savedCardsEditMode = false;
        savedMethodsEditButton.setText("Edit");
        clearNewCardForm();
        hideKeyboard();
        renderSavedCards();
        updatePaymentSelection();

        Toast.makeText(this, "Card saved", Toast.LENGTH_SHORT).show();
    }

    private void handlePayment() {
        applyVoucher();

        if (selectedPaymentMethod == PaymentMethod.GOOGLE_PAY) {
            hideKeyboard();
            placeOrder("Google Pay (simulated)");
            return;
        }

        if (selectedPaymentMethod == PaymentMethod.NEW_CARD) {
            if (!validateNewCardFields()) {
                showMissingCardDetailsDialog();
                return;
            }

            SavedCard newCard = saveNewCard();
            selectedSavedCardId = newCard.getId();
            selectedPaymentMethod = PaymentMethod.SAVED_CARD;
            savedCardsEditMode = false;
            savedMethodsEditButton.setText("Edit");
            clearNewCardForm();
            renderSavedCards();
            updatePaymentSelection();
        }

        if (selectedPaymentMethod == PaymentMethod.SAVED_CARD && selectedSavedCardId == null) {
            showMessageDialog(
                    "Payment method required",
                    "Please select a saved card or choose another payment method."
            );
            return;
        }

        hideKeyboard();
        placeOrder(selectedCardLabel() + " (simulated)");
    }

    private String selectedCardLabel() {
        for (SavedCard card : cardManager.getCards()) {
            if (card.getId().equals(selectedSavedCardId)) {
                return card.getBrand() + " •••• " + card.getLastFour();
            }
        }
        return "Card";
    }

    /**
     * Payment is simulated: no money is taken. The cart is turned into real
     * orders in Firestore (one per seller) and then cleared.
     */
    private void placeOrder(String paymentLabel) {
        if (placingOrder) {
            return;
        }

        List<CartItem> cart = new CartManager(this).getItems();

        if (cart.isEmpty()) {
            Toast.makeText(this, "Your cart is empty.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        placingOrder = true;
        payButton.setEnabled(false);
        payButtonText.setText("Processing...");

        String note = getIntent().getStringExtra("orderNote");
        String saved = ProfileActions.deliveryAddress(this);
        String address;
        if (note != null && !note.isEmpty()) {
            address = saved.isEmpty() ? note : saved + "\nNote: " + note;
        } else {
            address = saved.isEmpty() ? "Collect on campus" : saved;
        }

        OrderRepository.placeOrders(
                        this,
                        cart,
                        deliveryAmount,
                        originalAmount - totalAmount,
                        paymentLabel,
                        address
                )
                .addOnSuccessListener(ids -> {
                    VoucherRepository.markUsed(appliedVoucherCode);
                    new CartManager(this).clear();
                    showPaymentSuccessDialog();
                })
                .addOnFailureListener(e -> {
                    placingOrder = false;
                    payButton.setEnabled(true);
                    updateAmountLabels();
                    showMessageDialog(
                            "Order not placed",
                            "Nothing was charged. Please check your connection and try again.\n\n"
                                    + e.getMessage()
                    );
                });
    }

    private boolean validateNewCardFields() {
        String number = cardNumberEditText.getText().toString().replace(" ", "").trim();
        String expiry = expiryEditText.getText().toString().trim();
        String cvv = cvvEditText.getText().toString().trim();
        String name = cardNameEditText.getText().toString().trim();

        return number.length() >= 12
                && number.length() <= 19
                && number.matches("\\d+")
                && passesLuhn(number)
                && isValidExpiry(expiry)
                && !name.isEmpty()
                && (cvv.length() == 3 || cvv.length() == 4)
                && cvv.matches("\\d+");
    }

    /** Standard card number checksum, so obvious typos are caught. */
    private boolean passesLuhn(String number) {
        int sum = 0;
        boolean alternate = false;

        for (int i = number.length() - 1; i >= 0; i--) {
            int digit = number.charAt(i) - '0';
            if (alternate) {
                digit *= 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }
            sum += digit;
            alternate = !alternate;
        }

        return sum % 10 == 0;
    }

    /** Accepts MM/YY or MMYY that is not in the past. */
    private boolean isValidExpiry(String expiry) {
        String digits = expiry.replace("/", "").replace(" ", "");

        if (!digits.matches("\\d{4}")) {
            return false;
        }

        int month = Integer.parseInt(digits.substring(0, 2));
        int year = 2000 + Integer.parseInt(digits.substring(2, 4));

        if (month < 1 || month > 12) {
            return false;
        }

        java.util.Calendar now = java.util.Calendar.getInstance();
        int currentYear = now.get(java.util.Calendar.YEAR);
        int currentMonth = now.get(java.util.Calendar.MONTH) + 1;

        return year > currentYear || (year == currentYear && month >= currentMonth);
    }

    private SavedCard saveNewCard() {
        String number = cardNumberEditText.getText().toString().replace(" ", "").trim();
        String name = cardNameEditText.getText().toString().trim();
        String lastFour = number.substring(number.length() - 4);
        String brand = detectCardBrand(number);

        return cardManager.addCard(brand, lastFour, name);
    }

    private void clearNewCardForm() {
        cardNumberEditText.setText("");
        expiryEditText.setText("");
        cvvEditText.setText("");
        cardNameEditText.setText("");
    }

    private String detectCardBrand(String number) {
        if (number.startsWith("5") || number.startsWith("2")) {
            return "Mastercard";
        }
        return "Card";
    }

    private void showMissingCardDetailsDialog() {
        showMessageDialog(
                "Card details required",
                "Please check the card number, the expiry date (MM/YY, not in the past), the CVV and the name on the card.\n\nPayments are simulated: no money is taken."
        );
    }

    private void showMessageDialog(String title, String message) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .create();

        dialog.setOnShowListener(ignored ->
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(themeColor)
        );

        dialog.show();
    }

    private void loadAmount() {
        int subtotal = new CartManager(this).getSubtotal();

        if (subtotal <= 0) {
            Toast.makeText(this, "Your cart is empty.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        deliveryAmount = getIntent().getIntExtra("deliveryAmount", DEFAULT_DELIVERY_FEE);
        originalAmount = subtotal + deliveryAmount;
        totalAmount = originalAmount;
        updateAmountLabels();
    }

    /**
     * Vouchers: the demo code SAVE20 (R20 off) or a reward voucher the user
     * bought with points (see Rewards). Never more than the order total.
     */
    private void applyVoucher() {
        String voucher = voucherEditText.getText().toString().trim().toUpperCase(Locale.ROOT);
        appliedVoucherCode = null;

        long amount = -1;
        if (voucher.equals(VoucherRepository.DEMO_CODE)) {
            amount = VoucherRepository.DEMO_AMOUNT;
        } else if (userVouchers.containsKey(voucher)) {
            amount = userVouchers.get(voucher).amount;
        }

        if (amount > 0) {
            int discount = (int) Math.min(amount, originalAmount);
            totalAmount = Math.max(0, originalAmount - discount);
            appliedVoucherCode = voucher;
            voucherStatusText.setText("✓ Voucher applied! " + formatPrice(discount) + " off");
            voucherStatusText.setTextColor(Color.parseColor("#188038"));
        } else if (!voucher.isEmpty()) {
            totalAmount = originalAmount;
            voucherStatusText.setText("Invalid or already used voucher");
            voucherStatusText.setTextColor(Color.parseColor("#C62828"));
        } else {
            totalAmount = originalAmount;
            voucherStatusText.setText("");
        }

        updateAmountLabels();
    }

    private void updateAmountLabels() {
        orderAmountText.setText(formatPrice(totalAmount));
        payButtonText.setText("Pay " + formatPrice(totalAmount));
    }

    private String formatPrice(int amount) {
        NumberFormat formatter = NumberFormat.getIntegerInstance(Locale.US);
        return "R" + formatter.format(amount);
    }

    private void showPaymentSuccessDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_payment_success);
        dialog.setCancelable(false);

        LinearLayout successIcon = dialog.findViewById(R.id.successIcon);
        LinearLayout viewOrderButton = dialog.findViewById(R.id.viewOrderButton);

        ViewCompat.setBackgroundTintList(successIcon, ColorStateList.valueOf(themeColor));
        ViewCompat.setBackgroundTintList(viewOrderButton, ColorStateList.valueOf(themeColor));

        viewOrderButton.setOnClickListener(v -> {
            dialog.dismiss();
            OrdersActivity.openPurchases(this);
            finish();
        });

        dialog.show();

        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setDimAmount(0.65f);
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

            WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            params.gravity = Gravity.CENTER;
            params.horizontalMargin = 0.06f;
            window.setAttributes(params);
        }
    }

    private void clearFocusFromCardFields() {
        cardNumberEditText.clearFocus();
        expiryEditText.clearFocus();
        cvvEditText.clearFocus();
        cardNameEditText.clearFocus();
    }

    private void hideKeyboard() {
        View current = getCurrentFocus();
        if (current == null) return;

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(current.getWindowToken(), 0);
        }
        current.clearFocus();
    }

    private void applyAccountTheme() {
        SessionManager sessionManager = new SessionManager(this);
        themeColor = getThemeColor(sessionManager.getUserRole());

        ViewCompat.setBackgroundTintList(checkoutHeader, ColorStateList.valueOf(themeColor));
        ViewCompat.setBackgroundTintList(googlePayButton, ColorStateList.valueOf(themeColor));
        ViewCompat.setBackgroundTintList(saveCardButton, ColorStateList.valueOf(themeColor));
        ViewCompat.setBackgroundTintList(payButton, ColorStateList.valueOf(themeColor));
        savedMethodsEditButton.setTextColor(themeColor);
    }

    private int getThemeColor(UserRole role) {
        return com.example.communitystore.shared.utils.ThemeUtils.colorFor(this, role);
    }
}
