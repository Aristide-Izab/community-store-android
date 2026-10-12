package com.example.communitystore.vendor;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.communitystore.R;

public class VendorMessagesActivity extends AppCompatActivity {

    private TextView chatsTab;
    private TextView requestsTab;
    private View chatsContent;
    private View requestsContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_messages);

        chatsTab = findViewById(R.id.chatsTab);
        requestsTab = findViewById(R.id.requestsTab);
        chatsContent = findViewById(R.id.chatsContent);
        requestsContent = findViewById(R.id.requestsContent);

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
        chatsTab.setOnClickListener(v -> showChats());
        requestsTab.setOnClickListener(v -> showRequests());

        showChats();
    }

    private void showChats() {
        chatsContent.setVisibility(View.VISIBLE);
        requestsContent.setVisibility(View.GONE);
        chatsTab.setTextColor(0xFFFFA726);
        requestsTab.setTextColor(0xFF64748B);
    }

    private void showRequests() {
        chatsContent.setVisibility(View.GONE);
        requestsContent.setVisibility(View.VISIBLE);
        chatsTab.setTextColor(0xFF64748B);
        requestsTab.setTextColor(0xFFFFA726);
    }
}
