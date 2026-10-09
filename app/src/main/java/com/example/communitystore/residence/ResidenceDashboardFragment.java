package com.example.communitystore.residence;

import com.example.communitystore.R;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.FirebaseFirestore;

// home tab
public class ResidenceDashboardFragment extends Fragment {

    // dummy orders for now (title, price, status)
    private static final String[][] SAMPLE_RECENT_ORDERS = {
            {"Textbook", "R175", "Processing"},
            {"Textbook", "R175", "Delivered"},
            {"Textbook", "R175", "Delivered"}
    };

    private TextView greetingText;
    private TextView productsCountText;

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

        greetingText =
                view.findViewById(R.id.greetingText);

        productsCountText =
                view.findViewById(R.id.productsCountText);

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

        showRecentOrders(
                view.findViewById(R.id.recentOrdersList)
        );

        loadName();
    }

    @Override
    public void onResume() {
        super.onResume();

        loadProductCount();
    }

    private void loadName() {

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
        });
    }

    private void loadProductCount() {

        FirebaseUser user =
                FirebaseAuth.getInstance()
                        .getCurrentUser();

        if (user == null) {
            return;
        }

        FirebaseFirestore.getInstance()
                .collection("products")
                .whereEqualTo(
                        "sellerId",
                        user.getUid()
                )
                .count()
                .get(AggregateSource.SERVER)
                .addOnSuccessListener(snapshot -> {

                    if (!isAdded()) {
                        return;
                    }

                    productsCountText.setText(
                            String.valueOf(snapshot.getCount())
                    );
                });
    }

    private void showRecentOrders(
            LinearLayout list
    ) {

        LayoutInflater inflater =
                LayoutInflater.from(requireContext());

        for (String[] order : SAMPLE_RECENT_ORDERS) {

            View row =
                    inflater.inflate(
                            R.layout.item_residence_product_row,
                            list,
                            false
                    );

            ((TextView) row.findViewById(R.id.productTitle))
                    .setText(order[0]);

            ((TextView) row.findViewById(R.id.productPrice))
                    .setText(order[1]);

            TextView status =
                    row.findViewById(R.id.productStatus);

            status.setText(order[2]);

            if ("Processing".equals(order[2])) {

                status.setBackgroundResource(
                        R.drawable.bg_residence_status_processing
                );

                status.setTextColor(
                        ContextCompat.getColor(
                                requireContext(),
                                R.color.residence_orange
                        )
                );
            }

            list.addView(row);
        }
    }
}
