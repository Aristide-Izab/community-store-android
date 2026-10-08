package com.example.communitystore.core.access;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.communitystore.R;
import com.example.communitystore.core.session.SessionManager;

public class BottomNavThemeManager {

    private static final int INACTIVE_COLOR =
            Color.parseColor("#111111");

    private BottomNavThemeManager() {
    }

    public static void apply(
            Activity activity,
            String activeTab
    ) {

        SessionManager sessionManager =
                new SessionManager(activity);

        UserRole role =
                sessionManager.getUserRole();

        int themeColor =
                getThemeColor(role);

        LinearLayout navHome =
                activity.findViewById(R.id.navHome);

        LinearLayout navMarket =
                activity.findViewById(R.id.navMarket);

        LinearLayout navCommunity =
                activity.findViewById(R.id.navCommunity);

        LinearLayout navMessages =
                activity.findViewById(R.id.navMessages);

        LinearLayout navProfile =
                activity.findViewById(R.id.navProfile);

        setInactive(navHome);
        setInactive(navMarket);
        setInactive(navCommunity);
        setInactive(navMessages);
        setInactive(navProfile);

        if (activeTab == null) {
            return;
        }

        switch (activeTab.toLowerCase()) {

            case "home":
                setActive(
                        navHome,
                        themeColor
                );
                break;

            case "market":
                setActive(
                        navMarket,
                        themeColor
                );
                break;

            case "community":
                setActive(
                        navCommunity,
                        themeColor
                );
                break;

            case "messages":
                setActive(
                        navMessages,
                        themeColor
                );
                break;

            case "profile":
                setActive(
                        navProfile,
                        themeColor
                );
                break;
        }
    }

    private static void setInactive(
            ViewGroup container
    ) {

        if (container == null) {
            return;
        }

        applyColorToChildren(
                container,
                INACTIVE_COLOR,
                0.75f
        );
    }

    private static void setActive(
            ViewGroup container,
            int color
    ) {

        if (container == null) {
            return;
        }

        applyColorToChildren(
                container,
                color,
                1.0f
        );
    }

    private static void applyColorToChildren(
            ViewGroup group,
            int color,
            float iconAlpha
    ) {

        for (int i = 0;
             i < group.getChildCount();
             i++) {

            View child =
                    group.getChildAt(i);

            if (child instanceof ImageView) {

                ImageView imageView =
                        (ImageView) child;

                imageView.setColorFilter(
                        color,
                        PorterDuff.Mode.SRC_IN
                );

                imageView.setAlpha(
                        iconAlpha
                );

            } else if (child instanceof TextView) {

                TextView textView =
                        (TextView) child;

                textView.setTextColor(
                        color
                );

            } else if (child instanceof ViewGroup) {

                applyColorToChildren(
                        (ViewGroup) child,
                        color,
                        iconAlpha
                );
            }
        }
    }

    private static int getThemeColor(
            UserRole role
    ) {

        if (role == null) {
            return Color.parseColor(
                    "#168FC1"
            );
        }

        switch (role) {

            case STUDENT:

                return Color.parseColor(
                        "#168FC1"
                );

            case FACULTY:

                return Color.parseColor(
                        "#FF4B2B"
                );

            case VENDOR:

                return Color.parseColor(
                        "#FFA726"
                );

            case RESIDENCE:

                return Color.parseColor(
                        "#4B00D1"
                );

            default:

                return Color.parseColor(
                        "#168FC1"
                );
        }
    }
}