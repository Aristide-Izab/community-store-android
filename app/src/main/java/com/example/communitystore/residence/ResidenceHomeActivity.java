package com.example.communitystore.residence;

import com.example.communitystore.R;

import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

/**
 * Main area for verified Residence accounts: hosts the five bottom-nav tabs.
 */
public class ResidenceHomeActivity extends AppCompatActivity {

    public static final int TAB_HOME = 0;
    public static final int TAB_LISTING = 1;
    public static final int TAB_SALES = 2;
    public static final int TAB_MESSAGES = 3;
    public static final int TAB_PROFILE = 4;

    private static final String STATE_TAB =
            "selectedTab";

    private View[] navItems;

    private int selectedTab =
            -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_residence_home
        );

        ResidenceUi.applyEdgeToEdge(
                this,
                findViewById(R.id.residenceHomeRoot),
                findViewById(R.id.residenceBottomNav)
        );

        navItems =
                new View[]{
                        findViewById(R.id.navHome),
                        findViewById(R.id.navListing),
                        findViewById(R.id.navSales),
                        findViewById(R.id.navMessages),
                        findViewById(R.id.navProfile)
                };

        for (int i = 0; i < navItems.length; i++) {

            int tab = i;

            navItems[i].setOnClickListener(
                    v -> selectTab(tab)
            );
        }

        getOnBackPressedDispatcher().addCallback(
                this,
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {

                        if (selectedTab != TAB_HOME) {

                            selectTab(TAB_HOME);

                        } else {

                            finish();
                        }
                    }
                }
        );

        int initialTab =
                savedInstanceState != null
                        ? savedInstanceState.getInt(STATE_TAB, TAB_HOME)
                        : TAB_HOME;

        selectTab(initialTab);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(
                STATE_TAB,
                selectedTab
        );
    }

    public void selectTab(
            int tab
    ) {

        for (int i = 0; i < navItems.length; i++) {

            navItems[i].setSelected(i == tab);
        }

        if (tab == selectedTab) {
            return;
        }

        selectedTab = tab;

        getSupportFragmentManager()
                .beginTransaction()
                .replace(
                        R.id.residenceFragmentContainer,
                        createFragment(tab)
                )
                .commit();
    }

    private Fragment createFragment(
            int tab
    ) {

        switch (tab) {

            case TAB_LISTING:
                return new ResidenceListingsFragment();

            case TAB_SALES:
                return new ResidenceSalesFragment();

            case TAB_MESSAGES:
                return new ResidenceMessagesFragment();

            case TAB_PROFILE:
                return new ResidenceProfileFragment();

            default:
                return new ResidenceDashboardFragment();
        }
    }
}
