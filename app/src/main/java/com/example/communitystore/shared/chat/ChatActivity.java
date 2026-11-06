package com.example.communitystore.shared.chat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.communitystore.R;
import com.example.communitystore.core.access.UserRole;
import com.example.communitystore.core.session.AuthSession;
import com.example.communitystore.shared.data.ChatRepository;
import com.example.communitystore.shared.data.Conversation;
import com.example.communitystore.shared.data.Formatters;
import com.example.communitystore.shared.data.SampleProducts;
import com.example.communitystore.shared.utils.ThemeUtils;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

/**
 * Real-time chat between two users. Used by every account type.
 *
 * Start it with {@link #open(Context, String, String, String, String, String)}.
 */
public class ChatActivity extends AppCompatActivity {

    private static final String EXTRA_OTHER_ID = "otherId";
    private static final String EXTRA_OTHER_NAME = "otherName";
    private static final String EXTRA_OTHER_ROLE = "otherRole";
    private static final String EXTRA_SUBJECT = "subject";
    private static final String EXTRA_DRAFT = "draft";

    private final List<ChatRepository.Message> messages = new ArrayList<>();

    private RecyclerView recycler;
    private EditText input;
    private TextView sendButton;
    private TextView emptyText;

    private String myUid;
    private String otherId;
    private String conversationId;
    private String startedBy = "";

    private ListenerRegistration registration;
    private MessageAdapter adapter;

    /**
     * Opens (and creates if needed) the conversation with another user.
     *
     * @param subject optional context, e.g. the listing title
     * @param draft   optional text placed in the input box
     */
    public static void open(
            Context context,
            String otherId,
            String otherName,
            String otherRole,
            String subject,
            String draft
    ) {
        String me = AuthSession.uid();

        if (me == null) {
            Toast.makeText(context, "Please log in to send messages.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (otherId == null || otherId.isEmpty() || SampleProducts.SAMPLE_SELLER_ID.equals(otherId)) {
            Toast.makeText(
                    context,
                    "This is a sample listing, so there's no real seller to message.",
                    Toast.LENGTH_LONG
            ).show();
            return;
        }

        if (otherId.equals(me)) {
            Toast.makeText(context, "This is your own listing.", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(EXTRA_OTHER_ID, otherId);
        intent.putExtra(EXTRA_OTHER_NAME, otherName);
        intent.putExtra(EXTRA_OTHER_ROLE, otherRole);
        intent.putExtra(EXTRA_SUBJECT, subject);
        intent.putExtra(EXTRA_DRAFT, draft);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        myUid = AuthSession.uid();
        otherId = getIntent().getStringExtra(EXTRA_OTHER_ID);

        if (myUid == null || otherId == null) {
            finish();
            return;
        }

        conversationId = Conversation.idFor(myUid, otherId);

        String otherName = getIntent().getStringExtra(EXTRA_OTHER_NAME);
        String otherRole = getIntent().getStringExtra(EXTRA_OTHER_ROLE);
        String subject = getIntent().getStringExtra(EXTRA_SUBJECT);
        String draft = getIntent().getStringExtra(EXTRA_DRAFT);

        int color = ThemeUtils.primary(this);

        findViewById(R.id.chatHeader).setBackgroundColor(color);
        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        TextView title = findViewById(R.id.chatTitle);
        TextView subtitle = findViewById(R.id.chatSubtitle);
        TextView avatar = findViewById(R.id.chatAvatar);

        title.setText(otherName == null || otherName.isEmpty() ? "Community member" : otherName);
        avatar.setText(Formatters.initial(title.getText().toString()));

        UserRole role = UserRole.fromString(otherRole);
        String roleLabel = role == null ? "" : role.getLabel();
        if (subject != null && !subject.isEmpty()) {
            subtitle.setText(roleLabel.isEmpty() ? "About: " + subject : roleLabel + " • About: " + subject);
        } else {
            subtitle.setText(roleLabel);
        }

        recycler = findViewById(R.id.messageRecycler);
        input = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        emptyText = findViewById(R.id.emptyMessages);

        sendButton.setBackground(ThemeUtils.rounded(this, color, Color.TRANSPARENT, 23));

        if (draft != null && !draft.isEmpty()) {
            input.setText(draft);
            input.setSelection(draft.length());
        }

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setStackFromEnd(true);
        recycler.setLayoutManager(manager);
        adapter = new MessageAdapter();
        recycler.setAdapter(adapter);

        sendButton.setEnabled(false);
        sendButton.setOnClickListener(v -> send());

        ChatRepository.ensureConversation(this, otherId, otherName, otherRole, subject)
                .addOnSuccessListener(unused -> startListening())
                .addOnFailureListener(e -> Toast.makeText(
                        this,
                        "Could not open the chat: " + e.getMessage(),
                        Toast.LENGTH_LONG
                ).show());
    }

    private void startListening() {
        sendButton.setEnabled(true);

        ChatRepository.get(conversationId).addOnSuccessListener(document -> {
            String value = document.getString("startedBy");
            startedBy = value == null ? "" : value;
        });

        registration = ChatRepository.listenToMessages(conversationId, (list, error) -> {
            messages.clear();
            messages.addAll(list);
            adapter.notifyDataSetChanged();
            emptyText.setVisibility(messages.isEmpty() ? View.VISIBLE : View.GONE);

            if (!messages.isEmpty()) {
                recycler.scrollToPosition(messages.size() - 1);
            }

            ChatRepository.markRead(conversationId);
        });
    }

    private void send() {
        String text = input.getText().toString().trim();

        if (text.isEmpty()) {
            return;
        }

        input.setText("");

        ChatRepository.send(conversationId, otherId, startedBy, text)
                .addOnFailureListener(e -> {
                    input.setText(text);
                    Toast.makeText(this, "Message not sent: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (registration != null) {
            registration.remove();
        }
    }

    private class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.Holder> {

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_message, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            ChatRepository.Message message = messages.get(position);
            boolean mine = myUid.equals(message.senderId);
            Context context = holder.itemView.getContext();
            int color = ThemeUtils.primary(context);

            holder.row.setGravity(mine ? Gravity.END : Gravity.START);
            holder.text.setText(message.text);
            holder.text.setTextColor(mine ? Color.WHITE : Color.parseColor("#111111"));
            holder.text.setBackground(ThemeUtils.rounded(
                    context,
                    mine ? color : Color.WHITE,
                    mine ? Color.TRANSPARENT : Color.parseColor("#E5E7EB"),
                    16
            ));
            holder.time.setText(Formatters.shortTime(message.createdAt));
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }

        class Holder extends RecyclerView.ViewHolder {
            final LinearLayout row;
            final TextView text;
            final TextView time;

            Holder(View view) {
                super(view);
                row = view.findViewById(R.id.messageRow);
                text = view.findViewById(R.id.messageText);
                time = view.findViewById(R.id.messageTime);
            }
        }
    }
}
