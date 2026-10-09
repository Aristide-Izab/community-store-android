package com.example.communitystore.shared.rewards;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.communitystore.R;
import com.example.communitystore.core.access.UserRole;
import com.example.communitystore.core.session.SessionManager;
import com.example.communitystore.shared.utils.ThemeUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LeaderboardActivity extends AppCompatActivity {

    private final List<RewardItem> rewards = new ArrayList<>();
    private final List<RewardItem> milestones = new ArrayList<>();

    private int userPoints = 1250;
    private boolean showingRewards = true;

    private TextView greetingText;
    private TextView pointsValue;
    private TextView nextRewardText;
    private TextView tabRewards;
    private TextView tabMilestones;
    private RewardAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

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

        adapter = new RewardAdapter(item ->
                Toast.makeText(this, "Redeeming " + item.title, Toast.LENGTH_SHORT).show());

        RecyclerView recycler = findViewById(R.id.rewardRecycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
        tabRewards.setOnClickListener(v -> showTab(true));
        tabMilestones.setOnClickListener(v -> showTab(false));

        greetingText.setText("Good morning, " + defaultName());
        loadUser();
        render();
    }

    private void loadCatalog() {
        rewards.add(new RewardItem("R50 Voucher", 500));
        rewards.add(new RewardItem("Free Delivery", 1000));
        rewards.add(new RewardItem("R100 Voucher", 1500));

        milestones.add(new RewardItem("Bronze Tier", 500));
        milestones.add(new RewardItem("Silver Tier", 1000));
        milestones.add(new RewardItem("Gold Tier", 1500));
    }

    private String defaultName() {
        UserRole role = new SessionManager(this).getUserRole();

        if (role == UserRole.FACULTY) {
            return "Professor";
        }

        return role == null ? "there" : capitalise(role.getValue());
    }

    private void loadUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            return;
        }

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(document -> {
                    String name = document.getString("displayName");

                    if (name == null || name.trim().isEmpty()) {
                        name = document.getString("fullName");
                    }

                    if (name != null && !name.trim().isEmpty()) {
                        greetingText.setText("Good morning, " + name);
                    }

                    Long points = document.getLong("loyaltyPoints");

                    if (points != null) {
                        userPoints = points.intValue();
                    }

                    render();
                });
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

        adapter.submit(
                showingRewards ? rewards : milestones,
                showingRewards ? RewardAdapter.MODE_REWARDS : RewardAdapter.MODE_MILESTONES,
                userPoints
        );
    }

    private void updateNextRewardText() {
        for (RewardItem reward : rewards) {
            if (userPoints < reward.pointsRequired) {
                nextRewardText.setText("Next reward at "
                        + NumberFormat.getIntegerInstance(Locale.US).format(reward.pointsRequired)
                        + " points");
                return;
            }
        }

        nextRewardText.setText("You've unlocked every reward!");
    }

    private String capitalise(String value) {
        return value.substring(0, 1).toUpperCase() + value.substring(1);
    }
}
