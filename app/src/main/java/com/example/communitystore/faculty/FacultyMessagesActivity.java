package com.example.communitystore.faculty;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.communitystore.R;
import com.example.communitystore.shared.utils.BottomNavHelper;
import com.example.communitystore.shared.utils.ThemeUtils;

import java.util.ArrayList;
import java.util.List;

public class FacultyMessagesActivity extends AppCompatActivity {

    private final List<FacultyChatPreview> chats = new ArrayList<>();
    private final List<FacultyChatPreview> requests = new ArrayList<>();

    private FacultyChatAdapter adapter;
    private TextView tabChats;
    private TextView tabRequests;
    private TextView emptyChats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.faculty_messages);

        findViewById(R.id.messagesRoot).setBackgroundColor(ThemeUtils.primary(this));

        tabChats = findViewById(R.id.tabChats);
        tabRequests = findViewById(R.id.tabRequests);
        emptyChats = findViewById(R.id.emptyChats);

        loadSampleData();

        adapter = new FacultyChatAdapter(chat ->
                Toast.makeText(this, "Open chat with " + chat.name, Toast.LENGTH_SHORT).show());

        RecyclerView recycler = findViewById(R.id.chatRecycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        tabChats.setOnClickListener(v -> showTab(true));
        tabRequests.setOnClickListener(v -> showTab(false));
        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        BottomNavHelper.setup(this, "messages");
        showTab(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavHelper.setup(this, "messages");
    }

    private void loadSampleData() {
        chats.add(new FacultyChatPreview("Campus Eats", "Hi your order ready for pickup", "10:42", 1));
        chats.add(new FacultyChatPreview("Smith", "New items available", "09:15", 1));
        chats.add(new FacultyChatPreview("Aristide", "Yes, it's available", "Mon", 0));

        requests.add(new FacultyChatPreview("Thabo", "Is the textbook still for sale?", "Tue", 1));
    }

    private void showTab(boolean showChats) {
        styleTab(tabChats, showChats);
        styleTab(tabRequests, !showChats);

        List<FacultyChatPreview> data = showChats ? chats : requests;
        adapter.submit(data);
        emptyChats.setVisibility(data.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void styleTab(TextView tab, boolean active) {
        if (active) {
            tab.setBackground(ThemeUtils.rounded(this, ThemeUtils.primary(this), Color.TRANSPARENT, 19));
            tab.setTextColor(Color.WHITE);
        } else {
            tab.setBackground(ThemeUtils.rounded(this, Color.parseColor("#D9D9D9"), Color.TRANSPARENT, 19));
            tab.setTextColor(Color.parseColor("#222222"));
        }
    }
}
