package com.example.communitystore.residence;

import com.example.communitystore.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.communitystore.core.access.UserRole;
import com.example.communitystore.core.session.AuthSession;
import com.example.communitystore.shared.data.Product;
import com.example.communitystore.shared.data.UserDirectory;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// leaderboard screen
public class LeaderboardActivity extends AppCompatActivity {

    // merchant perks unlock with lifetime points (same as the Vendor perks)
    private static final String[] PERKS = {
            "Commission Reduction",
            "Premium Store Listing",
            "Campus Banner Feature"
    };

    private static final long[] PERK_POINTS = {500, 2000, 5000};

    private static final int MAX_RANK_ROWS = 10;

    private final List<DocumentSnapshot> ranking = new ArrayList<>();

    private long points = 0;
    private long lifetimePoints = 0;
    private boolean showingRank = true;

    private TextView scoreValue;

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

        scoreValue =
                findViewById(R.id.scoreValue);

        TextView greeting =
                findViewById(R.id.leaderboardGreeting);

        greeting.setText(
                ResidenceUi.greeting()
                        + ", "
                        + ResidenceUi.firstName(null)
        );

        ResidenceUi.loadProfile(profile -> {

            if (profile == null
                    || isFinishing()) {
                return;
            }

            greeting.setText(
                    ResidenceUi.greeting()
                            + ", "
                            + ResidenceUi.firstName(
                            profile.getString("displayName")
                    )
            );

            points = UserDirectory.points(profile);
            lifetimePoints = Math.max(points, Product.number(profile.get("lifetimePoints"), 0));
            showSalesRank(showingRank);
        });

        // Residence sellers ranked by points.
        UserDirectory.ranking(UserRole.RESIDENCE.getValue(), (users, error) -> {
            if (isFinishing()) {
                return;
            }
            ranking.clear();
            ranking.addAll(users);
            showSalesRank(showingRank);
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

        showingRank = salesRank;

        salesRankTab.setSelected(salesRank);
        merchantPerksTab.setSelected(!salesRank);

        NumberFormat format = NumberFormat.getIntegerInstance(Locale.US);

        scoreTitle.setText(
                salesRank
                        ? "Performance Score"
                        : "Loyalty Points"
        );

        scoreValue.setText(format.format(points));

        String next = "Every merchant perk unlocked!";
        for (long required : PERK_POINTS) {
            if (lifetimePoints < required) {
                next = "Next perk at " + format.format(required) + " lifetime points";
                break;
            }
        }
        scoreCaption.setText(next);

        scoreCaption.setVisibility(
                salesRank
                        ? View.GONE
                        : View.VISIBLE
        );

        leaderboardList.removeAllViews();

        LayoutInflater inflater =
                LayoutInflater.from(this);

        if (salesRank) {

            String uid = AuthSession.uid();

            if (ranking.isEmpty()) {
                addRow(inflater, "No residence sellers ranked yet", "Complete orders to earn points", null);
            }

            for (int i = 0; i < ranking.size() && i < MAX_RANK_ROWS; i++) {
                DocumentSnapshot user = ranking.get(i);
                boolean me = user.getId().equals(uid);

                addRow(
                        inflater,
                        (i + 1) + ". " + UserDirectory.displayName(user, "Resident") + (me ? " (you)" : ""),
                        format.format(UserDirectory.points(user)) + " points",
                        me ? "You" : null
                );
            }

        } else {

            boolean nextFound = false;

            for (int i = 0; i < PERKS.length; i++) {
                String state;

                if (lifetimePoints >= PERK_POINTS[i]) {
                    state = "Unlocked";
                } else if (!nextFound) {
                    state = "In Progress";
                    nextFound = true;
                } else {
                    state = "Locked";
                }

                addRow(inflater, PERKS[i], format.format(PERK_POINTS[i]) + " points", state);
            }
        }
    }

    private void addRow(
            LayoutInflater inflater,
            String title,
            String subtitle,
            String tagText
    ) {

        View row =
                inflater.inflate(
                        R.layout.item_residence_leaderboard,
                        leaderboardList,
                        false
                );

        ((TextView) row.findViewById(R.id.leaderboardTitle))
                .setText(title);

        ((TextView) row.findViewById(R.id.leaderboardSubtitle))
                .setText(subtitle);

        if (tagText != null) {

            TextView tag =
                    row.findViewById(R.id.leaderboardTag);

            tag.setText(tagText);
            tag.setVisibility(View.VISIBLE);
        }

        leaderboardList.addView(row);
    }
}
