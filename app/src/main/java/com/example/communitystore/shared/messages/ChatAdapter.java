package com.example.communitystore.shared.messages;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.communitystore.R;
import com.example.communitystore.shared.utils.ThemeUtils;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatHolder> {

    public interface Listener {
        void onChatClick(ChatPreview chat);
    }

    private final List<ChatPreview> chats = new ArrayList<>();
    private final Listener listener;

    public ChatAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submit(List<ChatPreview> newChats) {
        chats.clear();
        chats.addAll(newChats);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat, parent, false);
        return new ChatHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatHolder holder, int position) {
        ChatPreview chat = chats.get(position);

        holder.name.setText(chat.name);
        holder.message.setText(chat.lastMessage);
        holder.time.setText(chat.time);

        if (chat.unread > 0) {
            holder.badge.setVisibility(View.VISIBLE);
            holder.badge.setText(String.valueOf(chat.unread));
            holder.badge.setBackground(
                    ThemeUtils.rounded(holder.itemView.getContext(),
                            ThemeUtils.primary(holder.itemView.getContext()), 0, 10));
        } else {
            holder.badge.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> listener.onChatClick(chat));
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    static class ChatHolder extends RecyclerView.ViewHolder {
        final TextView name;
        final TextView message;
        final TextView time;
        final TextView badge;

        ChatHolder(View view) {
            super(view);
            name = view.findViewById(R.id.chatName);
            message = view.findViewById(R.id.chatMessage);
            time = view.findViewById(R.id.chatTime);
            badge = view.findViewById(R.id.chatBadge);
        }
    }
}
