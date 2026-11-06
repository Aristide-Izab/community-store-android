package com.example.communitystore.vendor;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.communitystore.R;
import com.example.communitystore.core.access.UserRole;
import com.example.communitystore.core.session.AuthSession;
import com.example.communitystore.shared.data.UserDirectory;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/** Vendor ranking by points (all vendors). */
public class VendorSalesRankActivity extends AppCompatActivity {

    private static final int MAX_ROWS = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_sales_rank);

        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        UserDirectory.ranking(UserRole.VENDOR.getValue(), (users, error) -> show(users));
    }

    private void show(List<DocumentSnapshot> users) {
        NumberFormat format = NumberFormat.getIntegerInstance(Locale.US);
        String uid = AuthSession.uid();

        TextView score = findViewById(R.id.scoreText);
        TextView rank = findViewById(R.id.rankText);
        LinearLayout list = findViewById(R.id.rankList);
        list.removeAllViews();

        rank.setText(users.isEmpty() ? "No vendors ranked yet." : "Not ranked yet");

        for (int i = 0; i < users.size(); i++) {
            DocumentSnapshot user = users.get(i);
            long points = UserDirectory.points(user);
            boolean me = user.getId().equals(uid);

            if (me) {
                score.setText(format.format(points));
                rank.setText("You are #" + (i + 1) + " of " + users.size() + " vendors");
            }

            if (i < MAX_ROWS) {
                TextView row = VendorUi.row(
                        this,
                        (i + 1) + ". " + UserDirectory.displayName(user, "Vendor") + (me ? " (you)" : ""),
                        format.format(points) + " points",
                        null,
                        Color.BLACK
                );
                list.addView(row);
            }
        }
    }
}
