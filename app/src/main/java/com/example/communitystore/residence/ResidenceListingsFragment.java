package com.example.communitystore.residence;

import com.example.communitystore.R;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Residence "My Listing" tab (Figma: Manage Products).
 */
public class ResidenceListingsFragment extends Fragment {

    private static final String STATUS_ACTIVE =
            "active";

    private static final String STATUS_INACTIVE =
            "inactive";

    private TextView activeTab;
    private TextView inactiveTab;
    private TextView listingsMessage;

    private LinearLayout productsList;

    private FirebaseFirestore db;

    private String selectedStatus =
            STATUS_ACTIVE;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {

        return inflater.inflate(
                R.layout.fragment_residence_listings,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {

        db =
                FirebaseFirestore.getInstance();

        activeTab =
                view.findViewById(R.id.activeTab);

        inactiveTab =
                view.findViewById(R.id.inactiveTab);

        listingsMessage =
                view.findViewById(R.id.listingsMessage);

        productsList =
                view.findViewById(R.id.productsList);

        ResidenceHomeActivity home =
                (ResidenceHomeActivity) requireActivity();

        view.findViewById(R.id.backButton)
                .setOnClickListener(v ->
                        home.selectTab(ResidenceHomeActivity.TAB_HOME)
                );

        view.findViewById(R.id.avatarButton)
                .setOnClickListener(v ->
                        home.selectTab(ResidenceHomeActivity.TAB_PROFILE)
                );

        view.findViewById(R.id.addProductButton)
                .setOnClickListener(v ->
                        startActivity(
                                new Intent(
                                        requireContext(),
                                        AddProductActivity.class
                                )
                        )
                );

        activeTab.setOnClickListener(
                v -> selectStatus(STATUS_ACTIVE)
        );

        inactiveTab.setOnClickListener(
                v -> selectStatus(STATUS_INACTIVE)
        );

        updateTabs();
    }

    @Override
    public void onResume() {
        super.onResume();

        loadProducts();
    }

    private void selectStatus(
            String status
    ) {

        if (status.equals(selectedStatus)) {
            return;
        }

        selectedStatus = status;

        updateTabs();
        loadProducts();
    }

    private void updateTabs() {

        activeTab.setSelected(
                STATUS_ACTIVE.equals(selectedStatus)
        );

        inactiveTab.setSelected(
                STATUS_INACTIVE.equals(selectedStatus)
        );
    }

    private void loadProducts() {

        FirebaseUser user =
                FirebaseAuth.getInstance()
                        .getCurrentUser();

        if (user == null) {

            showMessage("Please log in again.");
            return;
        }

        String requestedStatus =
                selectedStatus;

        showMessage("Loading products...");

        db.collection("products")
                .whereEqualTo(
                        "sellerId",
                        user.getUid()
                )
                .whereEqualTo(
                        "status",
                        requestedStatus
                )
                .get()
                .addOnSuccessListener(snapshot -> {

                    if (!isAdded()
                            || !requestedStatus.equals(selectedStatus)) {
                        return;
                    }

                    productsList.removeAllViews();

                    if (snapshot.isEmpty()) {

                        showMessage(
                                STATUS_ACTIVE.equals(requestedStatus)
                                        ? "No active products yet. Tap Add Product to create one."
                                        : "No inactive products."
                        );

                        return;
                    }

                    listingsMessage.setVisibility(View.GONE);

                    for (DocumentSnapshot document : snapshot.getDocuments()) {

                        addProductRow(document);
                    }
                })
                .addOnFailureListener(e -> {

                    if (!isAdded()) {
                        return;
                    }

                    productsList.removeAllViews();

                    showMessage(
                            "Could not load products: "
                                    + e.getMessage()
                    );
                });
    }

    private void addProductRow(
            DocumentSnapshot document
    ) {

        View row =
                LayoutInflater.from(requireContext())
                        .inflate(
                                R.layout.item_residence_product_row,
                                productsList,
                                false
                        );

        ((TextView) row.findViewById(R.id.productTitle))
                .setText(document.getString("title"));

        ((TextView) row.findViewById(R.id.productPrice))
                .setText(
                        AddProductActivity.formatPrice(
                                document.getDouble("price")
                        )
                );

        boolean active =
                STATUS_ACTIVE.equals(
                        document.getString("status")
                );

        TextView status =
                row.findViewById(R.id.productStatus);

        ViewGroup.LayoutParams params =
                status.getLayoutParams();

        params.height =
                (int) (37 * getResources().getDisplayMetrics().density);

        status.setLayoutParams(params);

        status.setText(
                active
                        ? "Active"
                        : "Inactive"
        );

        status.setBackgroundResource(
                active
                        ? R.drawable.bg_residence_status_active
                        : R.drawable.bg_residence_status_inactive
        );

        status.setTextColor(
                ContextCompat.getColor(
                        requireContext(),
                        R.color.residence_text
                )
        );

        status.setOnClickListener(v ->
                confirmStatusChange(
                        document,
                        active
                )
        );

        productsList.addView(row);
    }

    private void confirmStatusChange(
            DocumentSnapshot document,
            boolean currentlyActive
    ) {

        String title =
                document.getString("title");

        new AlertDialog.Builder(requireContext())
                .setTitle(
                        currentlyActive
                                ? "Deactivate listing?"
                                : "Activate listing?"
                )
                .setMessage(
                        currentlyActive
                                ? "\"" + title + "\" will be hidden from buyers."
                                : "\"" + title + "\" will be visible to buyers again."
                )
                .setNegativeButton("Cancel", null)
                .setPositiveButton(
                        currentlyActive
                                ? "Deactivate"
                                : "Activate",
                        (dialog, which) ->
                                document.getReference()
                                        .update(
                                                "status",
                                                currentlyActive
                                                        ? STATUS_INACTIVE
                                                        : STATUS_ACTIVE
                                        )
                                        .addOnSuccessListener(unused -> {

                                            if (isAdded()) {
                                                loadProducts();
                                            }
                                        })
                                        .addOnFailureListener(e -> {

                                            if (!isAdded()) {
                                                return;
                                            }

                                            Toast.makeText(
                                                    requireContext(),
                                                    "Could not update listing: "
                                                            + e.getMessage(),
                                                    Toast.LENGTH_LONG
                                            ).show();
                                        })
                )
                .show();
    }

    private void showMessage(
            String message
    ) {

        listingsMessage.setText(message);
        listingsMessage.setVisibility(View.VISIBLE);
    }
}
