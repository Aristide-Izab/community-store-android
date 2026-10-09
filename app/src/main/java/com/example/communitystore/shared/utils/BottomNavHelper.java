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
import com.example.communitystore.residence.ResidenceHomeActivity;
import com.example.communitystore.shared.market.MarketActivity;
import com.example.communitystore.shared.messages.MessagesActivity;
import com.example.communitystore.shared.profile.ProfileActivity;
import com.example.communitystore.student.StudentHomeActivity;
import com.example.communitystore.student.StudentMarketActivity;
import com.example.communitystore.student.StudentMessagesActivity;
import com.example.communitystore.student.StudentProfileActivity;
import com.example.communitystore.vendor.VendorHomeActivity;

public final class BottomNavHelper {

    private BottomNavHelper() {
    }

    public static void setup(Activity activity, String activeTab) {
        BottomNavThemeManager.apply(activity, activeTab);

        bind(activity, R.id.navHome, "home", activeTab, homeFor(activity));
        bind(activity, R.id.navMarket, "market", activeTab, marketFor(activity));
        bind(activity, R.id.navCommunity, "community", activeTab, communityFor(activity));
        bind(activity, R.id.navMessages, "messages", activeTab, messagesFor(activity));
        bind(activity, R.id.navProfile, "profile", activeTab, profileFor(activity));
    }

    public static Class<?> marketFor(Context context) {
        UserRole role = role(context);

        if (role == UserRole.STUDENT) {
            return StudentMarketActivity.class;
        }

        if (role == UserRole.FACULTY) {
            return FacultyMarketActivity.class;
        }

        return MarketActivity.class;
    }

    public static Class<?> messagesFor(Context context) {
        UserRole role = role(context);

        if (role == UserRole.STUDENT) {
            return StudentMessagesActivity.class;
        }

        if (role == UserRole.FACULTY) {
            return FacultyMessagesActivity.class;
        }

        return MessagesActivity.class;
    }

    public static Class<?> profileFor(Context context) {
        UserRole role = role(context);

        if (role == UserRole.STUDENT) {
            return StudentProfileActivity.class;
        }

        if (role == UserRole.FACULTY) {
            return FacultyProfileActivity.class;
        }

        return ProfileActivity.class;
    }

    public static Class<?> communityFor(Context context) {
        return null;
    }

    public static Class<?> homeFor(Context context) {
        UserRole role = role(context);

        if (role == null) {
            return StudentHomeActivity.class;
        }

        switch (role) {
            case FACULTY:
                return FacultyHomeActivity.class;
            case VENDOR:
                return VendorHomeActivity.class;
            case RESIDENCE:
                return ResidenceHomeActivity.class;
            case STUDENT:
            default:
                return StudentHomeActivity.class;
        }
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
