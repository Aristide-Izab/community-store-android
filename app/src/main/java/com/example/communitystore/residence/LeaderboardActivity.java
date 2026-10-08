package com.example.communitystore.residence;

import com.example.communitystore.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Residence rewards screen (Figma: Gamify Leaderboard rewards / milestone).
 */
public class LeaderboardActivity extends AppCompatActivity {

    /*
     * Points and rankings are not stored in Firestore yet, so this
     * screen shows the sample values from the Figma design.
     */
    private static final String[][] SAMPLE_SALES_RANK = {
            {"Student Store", "Total Sales R15,000"},
            {"Campus Tech", "Total Sales R10,600"},
            {"Tech Hub", "Total Sales R10,000"}
    };

    // Each perk: title, cost, status.
    private static final String[][] SAMPLE_PERKS = {
            {"Commission Reduction", "500 points", "Claimed"},
            {"Premium Store listing", "2000 Points", "In Progress"},
            {"Campus Banner feature", "5000 Points", "Locked"}
    };

    private TextView salesRankTab;
    private TextView merchantPerksTab;
    private TextView scoreTitle;
    private TextView scoreCaption;

    private LinearLayout leaderboardList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_leaderboard
        );

        ResidenceUi.applyEdgeToEdge(
                this,
                findViewById(R.id.leaderboardRoot),
                null
        );

        salesRankTab =
                findViewById(R.id.salesRankTab);

        merchantPerksTab =
                findViewById(R.id.merchantPerksTab);

        scoreTitle =
                findViewById(R.id.scoreTitle);

        scoreCaption =
                findViewById(R.id.scoreCaption);

        leaderboardList =
                findViewById(R.id.leaderboardList);

        TextView greeting =
                findViewById(R.id.leaderboardGreeting);

        greeting.setText(
                ResidenceUi.greeting()
                        + " , "
                        + ResidenceUi.firstName(null)
        );

        ResidenceUi.loadProfile(profile -> {

            if (profile == null
                    || isFinishing()) {
                return;
            }

            greeting.setText(
                    ResidenceUi.greeting()
                            + " , "
                            + ResidenceUi.firstName(
                            profile.getString("displayName")
                    )
            );
        });

        findViewById(R.id.backButton)
                .setOnClickListener(
                        v -> finish()
                );

        salesRankTab.setOnClickListener(
                v -> showSalesRank(true)
        );

        merchantPerksTab.setOnClickListener(
                v -> showSalesRank(false)
        );

        showSalesRank(true);
    }

    private void showSalesRank(
            boolean salesRank
    ) {

        salesRankTab.setSelected(salesRank);
        merchantPerksTab.setSelected(!salesRank);

        scoreTitle.setText(
                salesRank
                        ? "Performance Score"
                        : "Loyalty Points"
        );

        scoreCaption.setVisibility(
                salesRank
                        ? View.GONE
                        : View.VISIBLE
        );

        leaderboardList.removeAllViews();

        String[][] rows =
                salesRank
                        ? SAMPLE_SALES_RANK
                        : SAMPLE_PERKS;

        LayoutInflater inflater =
                LayoutInflater.from(this);

        for (String[] item : rows) {

            View row =
                    inflater.inflate(
                            R.layout.item_residence_leaderboard,
                            leaderboardList,
                            false
                    );

            ((TextView) row.findViewById(R.id.leaderboardTitle))
                    .setText(item[0]);

            ((TextView) row.findViewById(R.id.leaderboardSubtitle))
                    .setText(item[1]);

            if (item.length > 2) {

                TextView tag =
                        row.findViewById(R.id.leaderboardTag);

                tag.setText(item[2]);
                tag.setVisibility(View.VISIBLE);
            }

            leaderboardList.addView(row);
        }
    }
}
