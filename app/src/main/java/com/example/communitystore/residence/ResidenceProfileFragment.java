package com.example.communitystore.residence;

import com.example.communitystore.R;
import com.example.communitystore.auth.ForgotPasswordActivity;
import com.example.communitystore.auth.LogoutActivity;

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
import androidx.fragment.app.Fragment;

/**
 * Residence "Profile" tab (Figma: Profile).
 */
public class ResidenceProfileFragment extends Fragment {

    private static final String[] MENU_ITEMS = {
            "Sales & Order Hub",
            "Authentication",
            "Payment Options",
            "Reviews & Ratings",
            "Settings",
            "Vendor Support & Guidelines"
    };

    private TextView profileName;

    @Nullable
    private String email;

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

        profileName =
                view.findViewById(R.id.profileName);

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

            email =
                    profile.getString("email");
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

        if ("Sales & Order Hub".equals(item)) {

            home.selectTab(ResidenceHomeActivity.TAB_SALES);
            return;
        }

        if ("Authentication".equals(item)) {

            Intent intent =
                    new Intent(
                            requireContext(),
                            ForgotPasswordActivity.class
                    );

            if (email != null) {

                intent.putExtra(
                        "email",
                        email
                );
            }

            startActivity(intent);
            return;
        }

        Toast.makeText(
                requireContext(),
                item + " will be added soon.",
                Toast.LENGTH_SHORT
        ).show();
    }
}
