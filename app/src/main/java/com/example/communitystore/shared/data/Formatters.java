package com.example.communitystore.shared.data;

import android.text.format.DateUtils;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/** Shared text formatting for prices and dates. */
public final class Formatters {

    private Formatters() {
    }

    /** 1234 -> "R1,234". Negative values mean "message for price". */
    public static String price(long amount) {
        if (amount < 0) {
            return "Message for price";
        }
        return "R" + NumberFormat.getIntegerInstance(Locale.US).format(amount);
    }

    /** Short time for chat lists: "10:42", "Mon", "12 Oct". */
    public static String shortTime(long millis) {
        if (millis <= 0) {
            return "";
        }

        Calendar now = Calendar.getInstance();
        Calendar then = Calendar.getInstance();
        then.setTimeInMillis(millis);

        if (DateUtils.isToday(millis)) {
            return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(millis));
        }

        long days = (now.getTimeInMillis() - millis) / DateUtils.DAY_IN_MILLIS;

        if (days < 7) {
            return new SimpleDateFormat("EEE", Locale.getDefault()).format(new Date(millis));
        }

        return new SimpleDateFormat("d MMM", Locale.getDefault()).format(new Date(millis));
    }

    /** "12 Oct 2026". */
    public static String date(long millis) {
        if (millis <= 0) {
            return "Just now";
        }
        return new SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(new Date(millis));
    }

    /** "12 Oct 2026, 14:05". */
    public static String dateTime(long millis) {
        if (millis <= 0) {
            return "Just now";
        }
        return new SimpleDateFormat("d MMM yyyy, HH:mm", Locale.getDefault()).format(new Date(millis));
    }

    /** First letter for avatar circles. */
    public static String initial(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "?";
        }
        return name.trim().substring(0, 1).toUpperCase(Locale.getDefault());
    }

    /** First word of a name. */
    public static String firstName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "";
        }
        return name.trim().split("\\s+")[0];
    }

    public static String greeting() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hour < 12) {
            return "Good morning";
        }
        if (hour < 18) {
            return "Good afternoon";
        }
        return "Good evening";
    }
}
