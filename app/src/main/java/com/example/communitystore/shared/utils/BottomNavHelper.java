package com.example.communitystore.shared.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
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

        bind(activity, R.id.navHome, "home", activeTab, homeFor(activity));
        bind(activity, R.id.navMarket, "market", activeTab, marketFor(activity));
        bind(activity, R.id.navCommunity, "community", activeTab, null);
        bind(activity, R.id.navMessages, "messages", activeTab, messagesFor(activity));
        bind(activity, R.id.navProfile, "profile", activeTab, profileFor(activity));
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
