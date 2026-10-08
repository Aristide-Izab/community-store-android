package com.example.communitystore.shared.checkout;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CardManager {
    private static final String PREF_NAME = "CommunityStoreCards";
    private static final String KEY_CARDS = "saved_cards";
    private final SharedPreferences preferences;

    public CardManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        ensureDefaultCard();
    }

    public List<SavedCard> getCards() {
        List<SavedCard> cards = new ArrayList<>();
        String raw = preferences.getString(KEY_CARDS, "[]");
        try {
            JSONArray array = new JSONArray(raw);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                cards.add(new SavedCard(
                        object.optString("id"),
                        object.optString("brand", "Card"),
                        object.optString("lastFour", "0000"),
                        object.optString("holderName", "")
                ));
            }
        } catch (Exception ignored) {}
        return cards;
    }

    public SavedCard addCard(String brand, String lastFour, String holderName) {
        List<SavedCard> cards = getCards();
        for (SavedCard card : cards) {
            if (card.getLastFour().equals(lastFour)
                    && card.getHolderName().equalsIgnoreCase(holderName.trim())) {
                return card;
            }
        }

        SavedCard card = new SavedCard(
                UUID.randomUUID().toString(),
                brand,
                lastFour,
                holderName.trim()
        );
        cards.add(card);
        saveCards(cards);
        return card;
    }

    public void removeCard(String cardId) {
        List<SavedCard> cards = getCards();
        cards.removeIf(card -> card.getId().equals(cardId));
        saveCards(cards);
    }

    private void ensureDefaultCard() {
        if (!preferences.contains(KEY_CARDS)) {
            List<SavedCard> defaults = new ArrayList<>();
            defaults.add(new SavedCard(
                    "default-mastercard-0864",
                    "Mastercard",
                    "0864",
                    ""
            ));
            saveCards(defaults);
        }
    }

    private void saveCards(List<SavedCard> cards) {
        JSONArray array = new JSONArray();
        try {
            for (SavedCard card : cards) {
                JSONObject object = new JSONObject();
                object.put("id", card.getId());
                object.put("brand", card.getBrand());
                object.put("lastFour", card.getLastFour());
                object.put("holderName", card.getHolderName());
                array.put(object);
            }
        } catch (Exception ignored) {}

        preferences.edit().putString(KEY_CARDS, array.toString()).apply();
    }
}
