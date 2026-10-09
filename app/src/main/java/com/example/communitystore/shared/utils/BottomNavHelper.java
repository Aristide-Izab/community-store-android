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
import android.widget.Toast;

import com.example.communitystore.R;
import com.example.communitystore.core.access.BottomNavThemeManager;
import com.example.communitystore.core.access.UserRole;
import com.example.communitystore.core.session.SessionManager;
import com.example.communitystore.faculty.FacultyHomeActivity;
import com.example.communitystore.faculty.FacultyMarketActivity;
import com.example.communitystore.faculty.FacultyMessagesActivity;
import com.example.communitystore.faculty.FacultyProfileActivity;
import com.example.communitystore.student.StudentHomeActivity;
import com.example.communitystore.student.StudentMarketActivity;
import com.example.communitystore.student.StudentMessagesActivity;
import com.example.communitystore.student.StudentProfileActivity;

public final class BottomNavHelper {

    private BottomNavHelper() {
    }

    public static void setup(Activity activity, String activeTab) {
        BottomNavThemeManager.apply(activity, activeTab);

        // Bind each tab's container for clicks, then highlight its icon and label by ID.
        bind(activity, R.id.navHome, "home", activeTab, homeFor(activity));
        highlight(activity, R.id.navHomeIcon, R.id.navHomeLabel, "home".equals(activeTab));

        bind(activity, R.id.navMarket, "market", activeTab, marketFor(activity));
        highlight(activity, R.id.navMarketIcon, R.id.navMarketLabel, "market".equals(activeTab));

        bind(activity, R.id.navCommunity, "community", activeTab, null);
        highlight(activity, R.id.navCommunityIcon, R.id.navCommunityLabel, "community".equals(activeTab));

        bind(activity, R.id.navMessages, "messages", activeTab, messagesFor(activity));
        highlight(activity, R.id.navMessagesIcon, R.id.navMessagesLabel, "messages".equals(activeTab));

        bind(activity, R.id.navProfile, "profile", activeTab, profileFor(activity));
        highlight(activity, R.id.navProfileIcon, R.id.navProfileLabel, "profile".equals(activeTab));
    }


    // Active tab uses the current role's primary colour; inactive tabs are grey.
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
        if (role(context) == UserRole.FACULTY) {
            return FacultyMarketActivity.class;
        }

        return StudentMarketActivity.class;
    }

    public static Class<?> messagesFor(Context context) {
        if (role(context) == UserRole.FACULTY) {
            return FacultyMessagesActivity.class;
        }

        return StudentMessagesActivity.class;
    }

    public static Class<?> profileFor(Context context) {
        if (role(context) == UserRole.FACULTY) {
            return FacultyProfileActivity.class;
        }

        return StudentProfileActivity.class;
    }

    public static Class<?> homeFor(Context context) {
        if (role(context) == UserRole.FACULTY) {
            return FacultyHomeActivity.class;
        }

        return StudentHomeActivity.class;
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

            if (target == null) {
                Toast.makeText(activity, "Not available yet", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(activity, target);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            activity.startActivity(intent);
            activity.overridePendingTransition(0, 0);
        });
    }
}
