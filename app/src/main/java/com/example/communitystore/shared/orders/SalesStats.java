package com.example.communitystore.shared.orders;

import com.example.communitystore.shared.data.Order;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Income figures for a seller over a period (this week / month / year),
 * compared with the previous period. Income counts delivered orders only.
 */
public class SalesStats {

    public static final int WEEK = Calendar.WEEK_OF_YEAR;
    public static final int MONTH = Calendar.MONTH;
    public static final int YEAR = Calendar.YEAR;

    public long income;
    public long previousIncome;
    public int orders;
    public int previousOrders;
    /** Value of orders still being fulfilled (any period). */
    public long toFulfil;
    /** Orders placed in the period, newest first. */
    public final List<Order> inPeriod = new ArrayList<>();

    public static SalesStats compute(List<Order> all, int period) {
        SalesStats stats = new SalesStats();
        long start = periodStart(period, 0);
        long previousStart = periodStart(period, -1);

        for (Order order : all) {
            boolean delivered = Order.STATUS_DELIVERED.equals(order.status);
            boolean cancelled = Order.STATUS_CANCELLED.equals(order.status);

            if (order.isOpen()) {
                stats.toFulfil += order.total;
            }

            if (order.createdAt >= start) {
                if (!cancelled) {
                    stats.orders++;
                }
                if (delivered) {
                    stats.income += order.total;
                }
                stats.inPeriod.add(order);
            } else if (order.createdAt >= previousStart) {
                if (!cancelled) {
                    stats.previousOrders++;
                }
                if (delivered) {
                    stats.previousIncome += order.total;
                }
            }
        }

        return stats;
    }

    /** Percentage change, or null when there is nothing to compare with. */
    public static Long percentChange(long current, long previous) {
        if (previous == 0) {
            return null;
        }
        return Math.round((current - previous) * 100.0 / previous);
    }

    public static String changeText(long current, long previous) {
        Long percent = percentChange(current, previous);
        if (percent == null) {
            return current == 0 ? "No change" : "New this period";
        }
        return (percent >= 0 ? "↑ " : "↓ ") + Math.abs(percent) + "%";
    }

    /** Start of the current period (offset 0) or of an earlier one (offset -1). */
    public static long periodStart(int field, int offset) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        if (field == WEEK) {
            c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());
        } else if (field == MONTH) {
            c.set(Calendar.DAY_OF_MONTH, 1);
        } else {
            c.set(Calendar.DAY_OF_YEAR, 1);
        }

        c.add(field, offset);
        return c.getTimeInMillis();
    }
}
