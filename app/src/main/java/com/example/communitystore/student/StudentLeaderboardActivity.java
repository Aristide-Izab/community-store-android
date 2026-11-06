package com.example.communitystore.student;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.communitystore.R;
import com.example.communitystore.core.access.UserRole;
import com.example.communitystore.core.session.SessionManager;
import com.example.communitystore.shared.data.Formatters;
import com.example.communitystore.shared.data.Product;
import com.example.communitystore.shared.data.UserDirectory;
import com.example.communitystore.shared.data.VoucherRepository;
import com.example.communitystore.shared.utils.ThemeUtils;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Rewards: points balance, vouchers to redeem and tier milestones
 * (Student; Faculty uses the same screen).
 *
 * Points are earned on orders (see OrderRepository). Redeeming creates a
 * voucher code that can be entered at checkout.
 */
public class StudentLeaderboardActivity extends AppCompatActivity {

    private final List<StudentRewardItem> rewards = new ArrayList<>();
    private final List<StudentRewardItem> milestones = new ArrayList<>();

    private long userPoints = 0;
    private long lifetimePoints = 0;
    private boolean showingRewards = true;

    private TextView greetingText;
    private TextView pointsValue;
    private TextView nextRewardText;
    private TextView tabRewards;
    private TextView tabMilestones;
    private StudentRewardAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_leaderboard);

        findViewById(R.id.leaderboardRoot).setBackgroundColor(ThemeUtils.primary(this));

        greetingText = findViewById(R.id.greetingText);
        pointsValue = findViewById(R.id.pointsValue);
        nextRewardText = findViewById(R.id.nextRewardText);
        tabRewards = findViewById(R.id.tabRewards);
        tabMilestones = findViewById(R.id.tabMilestones);

        if (new SessionManager(this).getUserRole() == UserRole.FACULTY) {
            findViewById(R.id.pointsCard).setBackgroundResource(R.drawable.bg_faculty_points_card);
        }

        loadCatalog();

        adapter = new StudentRewardAdapter(this::confirmRedeem);

        RecyclerView recycler = findViewById(R.id.rewardRecycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
        tabRewards.setOnClickListener(v -> showTab(true));
        tabMilestones.setOnClickListener(v -> showTab(false));

        String cached = new SessionManager(this).getDisplayName();
        greetingText.setText(Formatters.greeting() + ", "
                + (cached.isEmpty() ? "there" : Formatters.firstName(cached)));
        render();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUser();
    }

    private void loadCatalog() {
        rewards.add(new StudentRewardItem("R50 Voucher", 500));
        rewards.add(new StudentRewardItem("Free Delivery", 1000));
        rewards.add(new StudentRewardItem("R100 Voucher", 1500));

        milestones.add(new StudentRewardItem("Bronze Tier", 500));
        milestones.add(new StudentRewardItem("Silver Tier", 1000));
        milestones.add(new StudentRewardItem("Gold Tier", 1500));
    }

    /** Rand value of each reward voucher. */
    private static long amountFor(StudentRewardItem item) {
        if (item.title.startsWith("R100")) {
            return 100;
        }
        return 50; // R50 voucher and Free Delivery (delivery fee is R50)
    }

    private void loadUser() {
        UserDirectory.loadCurrent(profile -> {
            if (profile == null) {
                return;
            }

            String name = UserDirectory.displayName(profile, "there");
            greetingText.setText(Formatters.greeting() + ", " + Formatters.firstName(name));

            userPoints = UserDirectory.points(profile);
            lifetimePoints = Math.max(userPoints, Product.number(profile.get("lifetimePoints"), 0));
            render();
        });
    }

    private void confirmRedeem(StudentRewardItem item) {
        new AlertDialog.Builder(this)
                .setTitle("Redeem " + item.title + "?")
                .setMessage("This uses " + item.pointsRequired + " of your "
                        + userPoints + " points. You'll get a voucher code to enter at checkout.")
                .setPositiveButton("Redeem", (d, w) -> redeem(item))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void redeem(StudentRewardItem item) {
        if (userPoints < item.pointsRequired) {
            Toast.makeText(this, "Not enough points yet.", Toast.LENGTH_SHORT).show();
            return;
        }

        VoucherRepository.redeem(item.title, amountFor(item), item.pointsRequired)
                .addOnSuccessListener(code -> {
                    userPoints -= item.pointsRequired;
                    render();

                    new AlertDialog.Builder(this)
                            .setTitle("Voucher ready!")
                            .setMessage("Your code is\n\n" + code
                                    + "\n\nEnter it in the voucher box at checkout for "
                                    + Formatters.price(amountFor(item)) + " off.")
                            .setPositiveButton("OK", null)
                            .show();
                })
                .addOnFailureListener(e -> Toast.makeText(
                        this, "Couldn't redeem: " + e.getMessage(), Toast.LENGTH_LONG
                ).show());
    }

    private void showTab(boolean rewardsTab) {
        showingRewards = rewardsTab;
        render();
    }

    private void render() {
        pointsValue.setText(NumberFormat.getIntegerInstance(Locale.US).format(userPoints));
        updateNextRewardText();

        tabRewards.setBackgroundResource(showingRewards ? R.drawable.bg_seller_tab_selected : 0);
        tabMilestones.setBackgroundResource(showingRewards ? 0 : R.drawable.bg_seller_tab_selected);

        // Rewards use the spendable balance; tiers use everything ever earned.
        adapter.submit(
                showingRewards ? rewards : milestones,
                showingRewards ? StudentRewardAdapter.MODE_REWARDS : StudentRewardAdapter.MODE_MILESTONES,
                (int) Math.min(Integer.MAX_VALUE, showingRewards ? userPoints : lifetimePoints)
        );
    }

    private void updateNextRewardText() {
        for (StudentRewardItem reward : rewards) {
            if (userPoints < reward.pointsRequired) {
                nextRewardText.setText("Next reward at "
                        + NumberFormat.getIntegerInstance(Locale.US).format(reward.pointsRequired)
                        + " points");
                return;
            }
        }

        nextRewardText.setText("You can redeem every reward!");
    }
}
