package com.example.communitystore.vendor;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.communitystore.R;
import com.example.communitystore.core.session.SessionManager;
import com.example.communitystore.shared.data.Formatters;
import com.example.communitystore.shared.data.Product;
import com.example.communitystore.shared.data.UserDirectory;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Vendor rewards: points balance and merchant perks that unlock with
 * lifetime points (earned when orders are delivered).
 */
public class VendorLeaderboardActivity extends AppCompatActivity {

    private static final String[] PERKS = {
            "Commission Reduction",
            "Premium Store Listing",
            "Campus Banner Feature"
    };
    private static final long[] PERK_POINTS = {500, 2000, 5000};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_leaderboard);

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
        findViewById(R.id.salesRankCard).setOnClickListener(v ->
                startActivity(new Intent(this, VendorSalesRankActivity.class)));

        String cached = new SessionManager(this).getDisplayName();
        ((TextView) findViewById(R.id.greetingText)).setText(
                Formatters.greeting() + ", " + (cached.isEmpty() ? "Vendor" : cached));

        render(0, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();

        UserDirectory.loadCurrent(profile -> {
            if (profile == null) {
                return;
            }
            long points = UserDirectory.points(profile);
            long lifetime = Math.max(points, Product.number(profile.get("lifetimePoints"), 0));
            render(points, lifetime);
        });
    }

    private void render(long points, long lifetime) {
        NumberFormat format = NumberFormat.getIntegerInstance(Locale.US);
        ((TextView) findViewById(R.id.pointsValue)).setText(format.format(points));

        String next = "You've unlocked every merchant perk!";
        for (long required : PERK_POINTS) {
            if (lifetime < required) {
                next = "Next perk at " + format.format(required) + " lifetime points";
                break;
            }
        }
        ((TextView) findViewById(R.id.nextRewardText)).setText(next);

        int[] ids = {R.id.perk1, R.id.perk2, R.id.perk3};
        boolean nextFound = false;

        for (int i = 0; i < PERKS.length; i++) {
            String state;

            if (lifetime >= PERK_POINTS[i]) {
                state = "Unlocked ✓";
            } else if (!nextFound) {
                state = "In Progress";
                nextFound = true;
            } else {
                state = "Locked";
            }

            TextView row = findViewById(ids[i]);
            row.setText(PERKS[i] + "\n" + format.format(PERK_POINTS[i]) + " points  •  " + state);
        }
    }
}
