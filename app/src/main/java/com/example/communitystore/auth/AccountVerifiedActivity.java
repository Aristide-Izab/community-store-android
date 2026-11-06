package com.example.communitystore.auth;

import com.example.communitystore.R;
import com.example.communitystore.core.access.UserRole;
import com.example.communitystore.core.session.AccountStatus;
import com.example.communitystore.shared.utils.ThemeUtils;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

/**
 * Final registration screen. Shown in the colour of the account type,
 * with a message that matches how the account was verified.
 *
 * Extra "accountType": role value. Extra "pendingReview": true when a
 * Vendor/Residence application is waiting for manual approval.
 */
public class AccountVerifiedActivity extends AppCompatActivity {

    public static final String EXTRA_PENDING_REVIEW = "pendingReview";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_auth_account_verified);

        UserRole role = UserRole.fromString(getIntent().getStringExtra("accountType"));
        boolean pendingReview = getIntent().getBooleanExtra(EXTRA_PENDING_REVIEW, false);

        TextView icon = findViewById(R.id.verifiedIcon);
        TextView title = findViewById(R.id.verifiedTitle);
        TextView description = findViewById(R.id.verifiedDescription);
        Button loginButton = findViewById(R.id.loginButton);

        int color = role == null
                ? getColor(R.color.brand_green)
                : ThemeUtils.colorFor(this, role);

        ViewCompat.setBackgroundTintList(icon, ColorStateList.valueOf(color));
        loginButton.setBackgroundTintList(ColorStateList.valueOf(color));

        if (pendingReview) {
            icon.setText("⏳");
            title.setText("Application Submitted");
            description.setText(
                    "Your " + (role == null ? "" : role.getLabel() + " ")
                            + "application is with our team. You'll be able to log in once it's approved."
            );
        } else if (role != null && role.requiresDocuments()) {
            title.setText(role.getLabel() + " Account Approved!");
            description.setText(
                    AccountStatus.DEMO_AUTO_APPROVE
                            ? "Your document was accepted (demo approval). Log in to open your "
                            + role.getLabel() + " dashboard."
                            : "Your account is active. Log in to open your " + role.getLabel() + " dashboard."
            );
        } else if (role != null) {
            title.setText("Account Verified!");
            description.setText(
                    "You're all set. Log in to start using Community Store as a "
                            + role.getLabel() + "."
            );
        }

        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(AccountVerifiedActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
