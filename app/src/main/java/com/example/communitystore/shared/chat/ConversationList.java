package com.example.communitystore.shared.chat;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.communitystore.R;
import com.example.communitystore.core.access.UserRole;
import com.example.communitystore.core.session.AuthSession;
import com.example.communitystore.shared.data.ChatRepository;
import com.example.communitystore.shared.data.Conversation;
import com.example.communitystore.shared.data.Formatters;
import com.example.communitystore.shared.utils.ThemeUtils;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

/**
 * Live "Chats" / "Requests" list used by the Student, Faculty, Vendor and
 * Residence message screens. Each screen supplies its own views; this class
 * fills them from Firestore.
 *
 * Call {@link #start()} in onStart and {@link #stop()} in onStop.
 */
public class ConversationList {

    private final Activity activity;
    private final RecyclerView recycler;
    private final TextView emptyText;
    @Nullable private final TextView tabChats;
    @Nullable private final TextView tabRequests;

    private final List<Conversation> all = new ArrayList<>();
    private final List<Conversation> visible = new ArrayList<>();
    private final Adapter adapter = new Adapter();

    private ListenerRegistration registration;
    private boolean showingChats = true;
    private final boolean useSelectedState;

    public ConversationList(
            Activity activity,
            RecyclerView recycler,
            TextView emptyText,
            @Nullable TextView tabChats,
            @Nullable TextView tabRequests
    ) {
        this(activity, recycler, emptyText, tabChats, tabRequests, false);
    }

    /**
     * @param useSelectedState true when the tabs are styled by their own
     *                         selector drawables (Residence), so this class
     *                         only toggles View.setSelected.
     */
    public ConversationList(
            Activity activity,
            RecyclerView recycler,
            TextView emptyText,
            @Nullable TextView tabChats,
            @Nullable TextView tabRequests,
            boolean useSelectedState
    ) {
        this.activity = activity;
        this.useSelectedState = useSelectedState;
        this.recycler = recycler;
        this.emptyText = emptyText;
        this.tabChats = tabChats;
        this.tabRequests = tabRequests;

        recycler.setLayoutManager(new LinearLayoutManager(activity));
        recycler.setAdapter(adapter);

        if (tabChats != null) {
            tabChats.setOnClickListener(v -> showTab(true));
        }
        if (tabRequests != null) {
            tabRequests.setOnClickListener(v -> showTab(false));
        }

        showTab(true);
    }

    public void start() {
        if (registration != null) {
            return;
        }

        registration = ChatRepository.listenToConversations((conversations, error) -> {
            all.clear();
            all.addAll(conversations);
            refresh();

            if (error != null && all.isEmpty()) {
                emptyText.setText("Couldn't load messages. Check your connection.");
                emptyText.setVisibility(View.VISIBLE);
            }
        });
    }

    public void stop() {
        if (registration != null) {
            registration.remove();
            registration = null;
        }
    }

    private void showTab(boolean chats) {
        showingChats = chats;
        styleTab(tabChats, chats);
        styleTab(tabRequests, !chats);
        refresh();
    }

    private void refresh() {
        String uid = AuthSession.uid();
        visible.clear();

        int requests = 0;

        for (Conversation conversation : all) {
            boolean request = uid != null && conversation.isRequestFor(uid);

            // Hide empty chats someone opened but never wrote in.
            if (conversation.lastMessage.isEmpty()) {
                continue;
            }

            if (request) {
                requests++;
            }

            if (request != showingChats) {
                visible.add(conversation);
            }
        }

        if (tabRequests != null) {
            tabRequests.setText(requests > 0 ? "Requests (" + requests + ")" : "Requests");
        }

        adapter.notifyDataSetChanged();
        emptyText.setText(showingChats
                ? "No chats yet. Message a seller from any listing to start one."
                : "No message requests.");
        emptyText.setVisibility(visible.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void styleTab(@Nullable TextView tab, boolean active) {
        if (tab == null) {
            return;
        }

        if (useSelectedState) {
            tab.setSelected(active);
            return;
        }

        int color = ThemeUtils.primary(activity);

        if (active) {
            tab.setBackground(ThemeUtils.rounded(activity, color, Color.TRANSPARENT, 19));
            tab.setTextColor(Color.WHITE);
        } else {
            tab.setBackground(ThemeUtils.rounded(activity, Color.parseColor("#D9D9D9"), Color.TRANSPARENT, 19));
            tab.setTextColor(Color.parseColor("#222222"));
        }
    }

    private class Adapter extends RecyclerView.Adapter<Adapter.Holder> {

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.student_item_chat, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            Conversation chat = visible.get(position);
            String uid = AuthSession.uid();

            UserRole role = UserRole.fromString(chat.otherRole);
            String name = chat.otherName + (role == null ? "" : " · " + role.getLabel());
            String prefix = uid != null && uid.equals(chat.lastSenderId) ? "You: " : "";

            holder.name.setText(name);
            holder.message.setText(prefix + chat.lastMessage);
            holder.time.setText(Formatters.shortTime(chat.lastMessageAt));

            if (chat.unread > 0) {
                holder.badge.setVisibility(View.VISIBLE);
                holder.badge.setText(String.valueOf(chat.unread));
                holder.badge.setBackground(ThemeUtils.rounded(
                        activity,
                        ThemeUtils.primary(activity),
                        Color.TRANSPARENT,
                        10
                ));
            } else {
                holder.badge.setVisibility(View.GONE);
            }

            holder.itemView.setOnClickListener(v -> ChatActivity.open(
                    activity,
                    chat.otherId,
                    chat.otherName,
                    chat.otherRole,
                    chat.subject,
                    null
            ));
        }

        @Override
        public int getItemCount() {
            return visible.size();
        }

        class Holder extends RecyclerView.ViewHolder {
            final TextView name;
            final TextView message;
            final TextView time;
            final TextView badge;

            Holder(View view) {
                super(view);
                name = view.findViewById(R.id.chatName);
                message = view.findViewById(R.id.chatMessage);
                time = view.findViewById(R.id.chatTime);
                badge = view.findViewById(R.id.chatBadge);
            }
        }
    }
}
