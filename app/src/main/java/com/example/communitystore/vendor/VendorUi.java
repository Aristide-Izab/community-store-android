package com.example.communitystore.vendor;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.communitystore.shared.data.Formatters;
import com.example.communitystore.shared.data.Order;
import com.example.communitystore.shared.orders.OrderDetailActivity;

/** Small row builders shared by the Vendor screens. */
final class VendorUi {

    private VendorUi() {
    }

    static int dp(Context context, int value) {
        return Math.round(value * context.getResources().getDisplayMetrics().density);
    }

    /** Light card row with a bold first line. */
    static TextView row(Context context, String title, String detail, String right, int rightColor) {
        TextView row = new TextView(context);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.topMargin = dp(context, 8);
        row.setLayoutParams(params);

        GradientDrawable background = new GradientDrawable();
        background.setColor(Color.parseColor("#F8FAFC"));
        background.setCornerRadius(dp(context, 10));
        row.setBackground(background);

        int pad = dp(context, 16);
        row.setPadding(pad, pad, pad, pad);
        row.setTextColor(Color.parseColor("#111111"));

        SpannableStringBuilder text = new SpannableStringBuilder();
        text.append(title);
        text.setSpan(new StyleSpan(Typeface.BOLD), 0, title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        if (detail != null && !detail.isEmpty()) {
            text.append("\n").append(detail);
        }

        if (right != null && !right.isEmpty()) {
            int start = text.length() + 5;
            text.append("  •  ").append(right);
            text.setSpan(new ForegroundColorSpan(rightColor), start, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            text.setSpan(new StyleSpan(Typeface.BOLD), start, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        row.setText(text);
        return row;
    }

    static TextView orderRow(Context context, Order order) {
        int color;
        if (Order.STATUS_DELIVERED.equals(order.status)) {
            color = Color.parseColor("#10B981");
        } else if (Order.STATUS_CANCELLED.equals(order.status)) {
            color = Color.parseColor("#C62828");
        } else {
            color = Color.parseColor("#F57C00");
        }

        TextView row = row(
                context,
                order.firstTitle(),
                Formatters.price(order.total) + " • " + order.buyerName + " • " + Formatters.date(order.createdAt),
                order.statusLabel(),
                color
        );
        row.setOnClickListener(v -> OrderDetailActivity.open(context, order.id));
        return row;
    }
}
