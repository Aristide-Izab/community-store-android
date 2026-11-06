package com.example.communitystore.residence;

import com.example.communitystore.R;
import com.example.communitystore.auth.LogoutActivity;
import com.example.communitystore.core.session.AuthSession;
import com.example.communitystore.faculty.FacultyAddPostActivity;
import com.example.communitystore.shared.data.OrderRepository;
import com.example.communitystore.shared.data.UserDirectory;
import com.example.communitystore.shared.orders.OrdersActivity;
import com.example.communitystore.shared.profile.ProfileActions;
import com.example.communitystore.shared.seller.SellerProfileActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

// profile tab
public class ResidenceProfileFragment extends Fragment {

    private static final String[] MENU_ITEMS = {
            "Sales & Order Hub",
            "Post an Announcement",
            "Edit Profile",
            "Payment Options",
            "Rewards & Ratings",
            "Settings",
            "Residence Support & Guidelines"
    };

    private TextView profileName;
    private TextView salesCountText;
    private TextView reviewsCountText;
    private TextView rewardsCountText;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {

        return inflater.inflate(
                R.layout.fragment_residence_profile,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {

        profileName = view.findViewById(R.id.profileName);
        salesCountText = view.findViewById(R.id.salesCountText);
        reviewsCountText = view.findViewById(R.id.reviewsCountText);
        rewardsCountText = view.findViewById(R.id.rewardsCountText);

        ResidenceHomeActivity home =
                (ResidenceHomeActivity) requireActivity();

        view.findViewById(R.id.backButton)
                .setOnClickListener(v ->
                        home.selectTab(ResidenceHomeActivity.TAB_HOME)
                );

        view.findViewById(R.id.logoutButton)
                .setOnClickListener(v ->
                        startActivity(
                                new Intent(
                                        requireContext(),
                                        LogoutActivity.class
                                )
                        )
                );

        buildMenu(
                view.findViewById(R.id.profileMenu),
                home
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

        ResidenceUi.loadProfile(profile -> {

            if (!isAdded()
                    || profile == null) {
                return;
            }

            String displayName =
                    profile.getString("displayName");

            if (displayName != null
                    && !displayName.trim().isEmpty()) {

                profileName.setText(displayName.trim());
            }

            rewardsCountText.setText(String.valueOf(UserDirectory.points(profile)));
        });

        OrderRepository.loadAsSeller(uid, (orders, error) -> {
            if (isAdded()) {
                salesCountText.setText(String.valueOf(orders.size()));
            }
        });

        UserDirectory.sellerRating(uid, (average, count) -> {
            if (isAdded()) {
                reviewsCountText.setText(String.valueOf(count));
            }
        });
    }

    private void buildMenu(
            LinearLayout menu,
            ResidenceHomeActivity home
    ) {

        LayoutInflater inflater =
                LayoutInflater.from(requireContext());

        for (String item : MENU_ITEMS) {

            View row =
                    inflater.inflate(
                            R.layout.item_residence_profile_menu,
                            menu,
                            false
                    );

            ((TextView) row.findViewById(R.id.menuLabel))
                    .setText(item);

            row.findViewById(R.id.menuRow)
                    .setOnClickListener(v ->
                            onMenuItemClicked(item, home)
                    );

            menu.addView(row);
        }
    }

    private void onMenuItemClicked(
            String item,
            ResidenceHomeActivity home
    ) {

        switch (item) {

            case "Sales & Order Hub":
                OrdersActivity.openSales(requireContext());
                break;

            case "Post an Announcement":
                // Residence admins post to the same community board as Faculty.
                startActivity(new Intent(requireContext(), FacultyAddPostActivity.class));
                break;

            case "Edit Profile":
                ProfileActions.editName(
                        requireActivity(),
                        profileName.getText().toString(),
                        profileName::setText
                );
                break;

            case "Payment Options":
                ProfileActions.paymentOptions(requireActivity());
                break;

            case "Rewards & Ratings":
                showRewardsAndRatings();
                break;

            case "Settings":
                ProfileActions.settings(requireActivity());
                break;

            default:
                showGuidelines();
                break;
        }
    }

    private void showRewardsAndRatings() {
        String[] options = {"Leaderboard & perks", "My public seller page (ratings)"};

        new AlertDialog.Builder(requireContext())
                .setTitle("Rewards & Ratings")
                .setItems(options, (d, which) -> {
                    if (which == 0) {
                        startActivity(new Intent(requireContext(), LeaderboardActivity.class));
                    } else {
                        SellerProfileActivity.open(
                                requireContext(),
                                AuthSession.uid(),
                                profileName.getText().toString(),
                                "residence"
                        );
                    }
                })
                .show();
    }

    private void showGuidelines() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Residence Support & Guidelines")
                .setMessage("• Residence admin accounts are verified so announcements can be trusted.\n\n"
                        + "• Post announcements and events for residents from this menu; they appear on the community board.\n\n"
                        + "• List items for sale under Listings and update each order: Packed → Out for delivery → Delivered.\n\n"
                        + "• Payments in this version are simulated; no money is collected.")
                .setPositiveButton("OK", null)
                .show();
    }
}
