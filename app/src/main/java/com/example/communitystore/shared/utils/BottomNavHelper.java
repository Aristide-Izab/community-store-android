package com.example.communitystore.shared.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.communitystore.R;
import com.example.communitystore.core.access.BottomNavThemeManager;
import com.example.communitystore.core.access.UserRole;
import com.example.communitystore.core.session.SessionManager;
import com.example.communitystore.faculty.FacultyCommunityBoardActivity;
import com.example.communitystore.faculty.FacultyHomeActivity;
import com.example.communitystore.faculty.FacultyMarketActivity;
import com.example.communitystore.faculty.FacultyMessagesActivity;
import com.example.communitystore.faculty.FacultyProfileActivity;
import com.example.communitystore.student.StudentCommunityBoardActivity;
import com.example.communitystore.student.StudentHomeActivity;
import com.example.communitystore.student.StudentMarketActivity;
import com.example.communitystore.student.StudentMessagesActivity;
import com.example.communitystore.student.StudentProfileActivity;

public final class BottomNavHelper {

    private BottomNavHelper() {
    }

    public static void setup(Activity activity, String activeTab) {
        BottomNavThemeManager.apply(activity, activeTab);

        bind(activity, R.id.navHome, "home", activeTab, homeFor(activity));
        highlight(activity, R.id.navHomeIcon, R.id.navHomeLabel, "home".equals(activeTab));

        bind(activity, R.id.navMarket, "market", activeTab, marketFor(activity));
        highlight(activity, R.id.navMarketIcon, R.id.navMarketLabel, "market".equals(activeTab));

        bind(activity, R.id.navCommunity, "community", activeTab, communityFor(activity));
        highlight(activity, R.id.navCommunityIcon, R.id.navCommunityLabel, "community".equals(activeTab));

        bind(activity, R.id.navMessages, "messages", activeTab, messagesFor(activity));
        highlight(activity, R.id.navMessagesIcon, R.id.navMessagesLabel, "messages".equals(activeTab));

        bind(activity, R.id.navProfile, "profile", activeTab, profileFor(activity));
        highlight(activity, R.id.navProfileIcon, R.id.navProfileLabel, "profile".equals(activeTab));
    }

    private static void highlight(Activity activity, int iconId, int labelId, boolean active) {
        int color = active ? ThemeUtils.primary(activity) : Color.parseColor("#777777");

        ImageView icon = activity.findViewById(iconId);
        if (icon != null) {
            icon.setImageTintList(ColorStateList.valueOf(color));
        }

        TextView label = activity.findViewById(labelId);
        if (label != null) {
            label.setTextColor(color);
            label.setTypeface(label.getTypeface(), active ? Typeface.BOLD : Typeface.NORMAL);
        }
    }

    public static Class<?> marketFor(Context context) {
        return role(context) == UserRole.FACULTY
                ? FacultyMarketActivity.class
                : StudentMarketActivity.class;
    }

    public static Class<?> communityFor(Context context) {
        return role(context) == UserRole.FACULTY
                ? FacultyCommunityBoardActivity.class
                : StudentCommunityBoardActivity.class;
    }

    public static Class<?> messagesFor(Context context) {
        return role(context) == UserRole.FACULTY
                ? FacultyMessagesActivity.class
                : StudentMessagesActivity.class;
    }

    public static Class<?> profileFor(Context context) {
        return role(context) == UserRole.FACULTY
                ? FacultyProfileActivity.class
                : StudentProfileActivity.class;
    }

    public static Class<?> homeFor(Context context) {
        return role(context) == UserRole.FACULTY
                ? FacultyHomeActivity.class
                : StudentHomeActivity.class;
    }

    public static void open(Activity activity, Class<?> target) {
        if (activity == null || target == null || activity.getClass().equals(target)) {
            return;
        }

        Intent intent = new Intent(activity, target);
        intent.addFlags(
                Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP
                        | Intent.FLAG_ACTIVITY_NO_ANIMATION
        );

        activity.startActivity(intent);
        activity.overridePendingTransition(0, 0);
    }

    private static UserRole role(Context context) {
        return new SessionManager(context).getUserRole();
    }

    private static void bind(Activity activity, int viewId, String tab, String activeTab, Class<?> target) {
        View view = activity.findViewById(viewId);

        if (view == null) {
            return;
        }

        view.setOnClickListener(v -> {
            if (tab.equals(activeTab)) {
                return;
            }

            open(activity, target);
        });
    }
}
