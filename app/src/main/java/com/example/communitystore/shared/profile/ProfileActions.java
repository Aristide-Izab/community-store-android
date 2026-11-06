package com.example.communitystore.shared.profile;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.InputType;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.communitystore.BuildConfig;
import com.example.communitystore.core.session.AuthSession;
import com.example.communitystore.core.session.SessionManager;
import com.example.communitystore.shared.cart.CartManager;
import com.example.communitystore.shared.checkout.CardManager;
import com.example.communitystore.shared.checkout.SavedCard;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

/**
 * Profile menu actions shared by every account type:
 * edit name, saved payment cards, delivery address, settings and help.
 */
public final class ProfileActions {

    private static final String PREFS = "CommunityStoreProfile";
    private static final String KEY_ADDRESS = "deliveryAddress";

    public interface NameCallback {
        void onNameChanged(String name);
    }

    private ProfileActions() {
    }

    private static int dp(Context context, int value) {
        return Math.round(value * context.getResources().getDisplayMetrics().density);
    }

    private static EditText input(Activity activity, String text, String hint, int type) {
        EditText input = new EditText(activity);
        input.setText(text);
        input.setHint(hint);
        input.setInputType(type);
        input.setSelection(input.getText().length());
        return input;
    }

    private static FrameLayout padded(Activity activity, EditText input) {
        FrameLayout frame = new FrameLayout(activity);
        int pad = dp(activity, 20);
        frame.setPadding(pad, dp(activity, 8), pad, 0);
        frame.addView(input);
        return frame;
    }

    /** Change the display name (users/{uid}.displayName and the Auth profile). */
    public static void editName(Activity activity, String currentName, NameCallback callback) {
        EditText input = input(activity, currentName, "Full name",
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        new AlertDialog.Builder(activity)
                .setTitle("Edit name")
                .setView(padded(activity, input))
                .setPositiveButton("Save", (dialog, which) -> {
                    String name = input.getText().toString().trim();
                    String uid = AuthSession.uid();

                    if (name.isEmpty() || uid == null) {
                        Toast.makeText(activity, "Name can't be empty.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    FirebaseFirestore.getInstance()
                            .collection("users")
                            .document(uid)
                            .update("displayName", name)
                            .addOnSuccessListener(unused -> {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if (user != null) {
                                    user.updateProfile(new UserProfileChangeRequest.Builder()
                                            .setDisplayName(name)
                                            .build());
                                }
                                new SessionManager(activity).saveDisplayName(name);
                                callback.onNameChanged(name);
                                Toast.makeText(activity, "Name updated.", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> Toast.makeText(
                                    activity, "Couldn't update: " + e.getMessage(), Toast.LENGTH_LONG
                            ).show());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /** Saved cards (only brand + last four digits are ever stored). */
    public static void paymentOptions(Activity activity) {
        CardManager manager = new CardManager(activity);
        List<SavedCard> cards = manager.getCards();

        if (cards.isEmpty()) {
            new AlertDialog.Builder(activity)
                    .setTitle("Payment options")
                    .setMessage("No saved cards. You can add a card at checkout.\n\n"
                            + "Payments in this app are simulated: only the card brand and last four digits are saved on this phone.")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        String[] labels = new String[cards.size()];
        for (int i = 0; i < cards.size(); i++) {
            SavedCard card = cards.get(i);
            labels[i] = card.getBrand() + " •••• " + card.getLastFour() + "   (tap to remove)";
        }

        new AlertDialog.Builder(activity)
                .setTitle("Saved cards")
                .setItems(labels, (dialog, which) -> {
                    SavedCard card = cards.get(which);
                    new AlertDialog.Builder(activity)
                            .setTitle("Remove card?")
                            .setMessage(card.getBrand() + " •••• " + card.getLastFour())
                            .setPositiveButton("Remove", (d, w) -> {
                                manager.removeCard(card.getId());
                                Toast.makeText(activity, "Card removed.", Toast.LENGTH_SHORT).show();
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                })
                .setNegativeButton("Close", null)
                .show();
    }

    /** Default delivery / collection note used at checkout. */
    public static String deliveryAddress(Context context) {
        return prefs(context).getString(KEY_ADDRESS, "");
    }

    public static void editDeliveryAddress(Activity activity) {
        EditText input = input(activity, deliveryAddress(activity), "e.g. Bellville campus, Res block B, room 12",
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_MULTI_LINE);

        new AlertDialog.Builder(activity)
                .setTitle("Delivery address")
                .setMessage("Used as the delivery / collection note on your orders.")
                .setView(padded(activity, input))
                .setPositiveButton("Save", (dialog, which) -> {
                    prefs(activity).edit().putString(KEY_ADDRESS, input.getText().toString().trim()).apply();
                    Toast.makeText(activity, "Delivery address saved.", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /** Account settings: password reset email, clear cart. */
    public static void settings(Activity activity) {
        String[] options = {"Send password reset email", "Clear my cart"};

        new AlertDialog.Builder(activity)
                .setTitle("Settings")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        sendPasswordReset(activity);
                    } else {
                        new CartManager(activity).clear();
                        Toast.makeText(activity, "Cart cleared.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Close", null)
                .show();
    }

    private static void sendPasswordReset(Activity activity) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null || user.getEmail() == null) {
            return;
        }

        ActionCodeSettings settings = ActionCodeSettings.newBuilder()
                .setUrl(BuildConfig.FIREBASE_AUTH_URL)
                .setHandleCodeInApp(true)
                .setAndroidPackageName("com.example.communitystore", false, null)
                .build();

        FirebaseAuth.getInstance()
                .sendPasswordResetEmail(user.getEmail(), settings)
                .addOnSuccessListener(unused -> Toast.makeText(
                        activity, "Reset link sent to " + user.getEmail(), Toast.LENGTH_LONG
                ).show())
                .addOnFailureListener(e -> Toast.makeText(
                        activity, "Couldn't send the email: " + e.getMessage(), Toast.LENGTH_LONG
                ).show());
    }

    public static void help(Activity activity) {
        new AlertDialog.Builder(activity)
                .setTitle("Help and Support")
                .setMessage("• Buying: open a listing, add it to your cart and check out. "
                        + "Payments are simulated in this version.\n\n"
                        + "• Selling: use Post a Listing (or Add Product) and manage it under My Listings.\n\n"
                        + "• Orders: sellers update the status (Packed → Out for delivery → Delivered). "
                        + "Buyers can review delivered orders.\n\n"
                        + "• Messages: tap Message Seller on any listing or order.\n\n"
                        + "• Points: buyers earn points per order, sellers when an order is delivered. "
                        + "Redeem them for vouchers under Rewards.")
                .setPositiveButton("OK", null)
                .show();
    }

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }
}
