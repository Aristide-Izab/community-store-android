package com.example.communitystore.faculty;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.communitystore.R;

import java.util.ArrayList;
import java.util.List;

public class FacultyRewardAdapter extends RecyclerView.Adapter<FacultyRewardAdapter.RewardHolder> {

    public static final int MODE_REWARDS = 0;
    public static final int MODE_MILESTONES = 1;

    public interface Listener {
        void onRedeem(FacultyRewardItem item);
    }

    private final List<FacultyRewardItem> items = new ArrayList<>();
    private final Listener listener;
    private int mode = MODE_REWARDS;
    private int userPoints = 0;

    public FacultyRewardAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submit(List<FacultyRewardItem> newItems, int mode, int userPoints) {
        items.clear();
        items.addAll(newItems);
        this.mode = mode;
        this.userPoints = userPoints;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RewardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.faculty_item_reward, parent, false);
        return new RewardHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RewardHolder holder, int position) {
        FacultyRewardItem item = items.get(position);

        holder.title.setText(item.title);
        holder.points.setText(item.pointsRequired + " Points");

        boolean reached = userPoints >= item.pointsRequired;
        String label;
        boolean enabled = false;

        if (mode == MODE_REWARDS) {
            label = reached ? "Redeem" : "Locked";
            enabled = reached;
        } else if (reached) {
            label = "Achieved";
        } else if (isNextMilestone(position)) {
            label = "In Progress";
        } else {
            label = "Locked";
        }

        holder.action.setText(label);
        holder.action.setTextColor(Color.parseColor(enabled ? "#111111" : "#666666"));
        holder.action.setAlpha(enabled || mode == MODE_MILESTONES ? 1f : 0.6f);
        if (enabled) {
            holder.action.setOnClickListener(v -> listener.onRedeem(item));
        } else {
            holder.action.setOnClickListener(null);
        }
        holder.action.setClickable(enabled);
    }

        private boolean isNextMilestone(int position) {
        for (int i = 0; i < items.size(); i++) {
            if (userPoints < items.get(i).pointsRequired) {
                return i == position;
            }
        }
        return false;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class RewardHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final TextView points;
        final TextView action;

        RewardHolder(View view) {
            super(view);
            title = view.findViewById(R.id.rewardTitle);
            points = view.findViewById(R.id.rewardPoints);
            action = view.findViewById(R.id.rewardAction);
        }
    }
}
