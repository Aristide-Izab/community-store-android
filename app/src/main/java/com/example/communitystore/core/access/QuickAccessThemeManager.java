package com.example.communitystore.core.access;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.view.ViewCompat;

import com.example.communitystore.R;
import com.example.communitystore.core.session.SessionManager;

public class QuickAccessThemeManager {

    private static final int INACTIVE_COLOR =
            Color.parseColor("#E2E2E2");

    private static final int INACTIVE_TEXT_COLOR =
            Color.parseColor("#222222");

    private QuickAccessThemeManager() {
    }

    public static void apply(
            Activity activity,
            String activeButton
    ) {

        SessionManager sessionManager =
                new SessionManager(activity);

        UserRole role =
                sessionManager.getUserRole();

        int themeColor =
                getThemeColor(role);

        LinearLayout postListing =
                activity.findViewById(
                        R.id.postListingButton
                );

        LinearLayout news =
                activity.findViewById(
                        R.id.newsButton
                );

        LinearLayout sales =
                activity.findViewById(
                        R.id.salesButton
                );

        LinearLayout orders =
                activity.findViewById(
                        R.id.ordersButton
                );

        setInactive(postListing);
        setInactive(news);
        setInactive(sales);
        setInactive(orders);

        if (activeButton == null) {
            return;
        }

        switch (activeButton.toLowerCase()) {

            case "post":
                setActive(
                        postListing,
                        themeColor
                );
                break;

            case "news":
                setActive(
                        news,
                        themeColor
                );
                break;

            case "sales":
                setActive(
                        sales,
                        themeColor
                );
                break;

            case "orders":
                setActive(
                        orders,
                        themeColor
                );
                break;
        }
    }

    private static void setInactive(
            LinearLayout button
    ) {

        if (button == null) {
            return;
        }

        ViewCompat.setBackgroundTintList(
                button,
                ColorStateList.valueOf(
                        INACTIVE_COLOR
                )
        );

        changeTextColor(
                button,
                INACTIVE_TEXT_COLOR
        );
    }

    private static void setActive(
            LinearLayout button,
            int color
    ) {

        if (button == null) {
            return;
        }

        ViewCompat.setBackgroundTintList(
                button,
                ColorStateList.valueOf(
                        color
                )
        );

        changeTextColor(
                button,
                Color.WHITE
        );
    }

    private static void changeTextColor(
            ViewGroup group,
            int color
    ) {

        for (int i = 0;
             i < group.getChildCount();
             i++) {

            if (group.getChildAt(i)
                    instanceof TextView) {

                TextView textView =
                        (TextView)
                                group.getChildAt(i);

                textView.setTextColor(
                        color
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