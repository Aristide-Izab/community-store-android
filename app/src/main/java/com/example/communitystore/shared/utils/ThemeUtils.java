package com.example.communitystore.shared.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.widget.TextView;

import com.example.communitystore.core.access.UserRole;
import com.example.communitystore.core.session.SessionManager;

public final class ThemeUtils {

    private ThemeUtils() {
    }

    public static int primary(Context context) {
        UserRole role = new SessionManager(context).getUserRole();

        if (role == null) {
            return Color.parseColor("#168FC1");
        }

        switch (role) {
            case FACULTY:
                return Color.parseColor("#FF4B2B");
            case VENDOR:
                return Color.parseColor("#FFA726");
            case RESIDENCE:
                return Color.parseColor("#4B00D1");
            case STUDENT:
            default:
                return Color.parseColor("#168FC1");
        }
    }

    public static int dp(Context context, float value) {
        return Math.round(value * context.getResources().getDisplayMetrics().density);
    }

    public static GradientDrawable rounded(Context context, int fill, int stroke, float radiusDp) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(fill);
        drawable.setCornerRadius(dp(context, radiusDp));

        if (stroke != Color.TRANSPARENT) {
            drawable.setStroke(dp(context, 1), stroke);
        }

        return drawable;
    }

    public static GradientDrawable topRounded(Context context, int fill, float radiusDp) {
        float r = dp(context, radiusDp);
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(fill);
        drawable.setCornerRadii(new float[]{r, r, r, r, 0, 0, 0, 0});
        return drawable;
    }

        public static void styleChip(TextView chip, boolean selected) {
        Context context = chip.getContext();

        if (selected) {
            chip.setBackground(rounded(context, primary(context), Color.TRANSPARENT, 16));
            chip.setTextColor(Color.WHITE);
        } else {
            chip.setBackground(rounded(context, Color.parseColor("#EDEDED"), Color.parseColor("#CFCFCF"), 16));
            chip.setTextColor(Color.parseColor("#222222"));
        }

        chip.setSelected(selected);
    }

        public static void stylePrimaryButton(TextView button) {
        Context context = button.getContext();
        button.setBackground(rounded(context, primary(context), Color.TRANSPARENT, 22));
        button.setTextColor(Color.WHITE);
    }
}
