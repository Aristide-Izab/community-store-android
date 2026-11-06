package com.example.communitystore.shared.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.communitystore.R;

import com.example.communitystore.core.access.UserRole;
import com.example.communitystore.core.session.SessionManager;

public final class ThemeUtils {

    private ThemeUtils() {
    }

    /** Colour of the signed-in account type (falls back to Student blue). */
    public static int primary(Context context) {
        return colorFor(context, new SessionManager(context).getUserRole());
    }

    /**
     * Single source of truth for account type colours.
     * The values live in res/values/colors.xml (role_*).
     */
    public static int colorFor(Context context, UserRole role) {
        if (role == null) {
            return ContextCompat.getColor(context, R.color.role_student);
        }

        switch (role) {
            case FACULTY:
                return ContextCompat.getColor(context, R.color.role_faculty);
            case VENDOR:
                return ContextCompat.getColor(context, R.color.role_vendor);
            case RESIDENCE:
                return ContextCompat.getColor(context, R.color.role_residence);
            case STUDENT:
            default:
                return ContextCompat.getColor(context, R.color.role_student);
        }
    }

    /** Same colour with an alpha (0-255), for light tinted backgrounds. */
    public static int withAlpha(int color, int alpha) {
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
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
