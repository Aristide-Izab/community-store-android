package com.example.communitystore.student;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.communitystore.R;
import com.example.communitystore.core.access.BottomNavThemeManager;
import com.example.communitystore.shared.chat.ConversationList;
import com.example.communitystore.shared.utils.BottomNavHelper;
import com.example.communitystore.shared.utils.ThemeUtils;

/** Live chats and message requests (Student; Faculty uses the same screen). */
public class StudentMessagesActivity extends AppCompatActivity {

    private ConversationList conversations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_messages);

        findViewById(R.id.messagesRoot).setBackgroundColor(ThemeUtils.primary(this));
        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        RecyclerView recycler = findViewById(R.id.chatRecycler);
        TextView emptyChats = findViewById(R.id.emptyChats);
        TextView tabChats = findViewById(R.id.tabChats);
        TextView tabRequests = findViewById(R.id.tabRequests);

        conversations = new ConversationList(this, recycler, emptyChats, tabChats, tabRequests);

        BottomNavHelper.setup(this, "messages");
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

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavThemeManager.apply(this, "messages");
    }
}
