package com.example.communitystore.vendor;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.communitystore.R;
import com.example.communitystore.shared.chat.ConversationList;

/** Vendor chats and message requests (live, shared with every account type). */
public class VendorMessagesActivity extends AppCompatActivity {

    private ConversationList conversations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_messages);

        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        RecyclerView recycler = findViewById(R.id.chatRecycler);
        TextView empty = findViewById(R.id.emptyChats);
        TextView chatsTab = findViewById(R.id.chatsTab);
        TextView requestsTab = findViewById(R.id.requestsTab);

        conversations = new ConversationList(this, recycler, empty, chatsTab, requestsTab);
    }

    @Override
    protected void onStart() {
        super.onStart();
        conversations.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        conversations.stop();
    }
}
