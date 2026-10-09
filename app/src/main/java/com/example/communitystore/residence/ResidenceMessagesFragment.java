package com.example.communitystore.residence;

import com.example.communitystore.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

// messages tab
public class ResidenceMessagesFragment extends Fragment {

    // dummy chats for now (name, message, unread, time)
    private static final String[][] SAMPLE_CHATS = {
            {"Campus Eats", "Hi your order ready for pickup", "2", ""},
            {"Smith", "New Items available", "2", ""},
            {"Aristide", "Yes,its available", "", "Mon"}
    };

    private TextView chatsTab;
    private TextView requestsTab;
    private TextView emptyText;

    private LinearLayout conversationsList;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {

        return inflater.inflate(
                R.layout.fragment_residence_messages,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {

        chatsTab =
                view.findViewById(R.id.chatsTab);

        requestsTab =
                view.findViewById(R.id.requestsTab);

        emptyText =
                view.findViewById(R.id.messagesEmptyText);

        conversationsList =
                view.findViewById(R.id.conversationsList);

        ResidenceHomeActivity home =
                (ResidenceHomeActivity) requireActivity();

        view.findViewById(R.id.backButton)
                .setOnClickListener(v ->
                        home.selectTab(ResidenceHomeActivity.TAB_HOME)
                );

        chatsTab.setOnClickListener(
                v -> showChats(true)
        );

        requestsTab.setOnClickListener(
                v -> showChats(false)
        );

        showChats(true);
    }

    private void showChats(
            boolean chats
    ) {

        chatsTab.setSelected(chats);
        requestsTab.setSelected(!chats);

        conversationsList.removeAllViews();

        emptyText.setVisibility(
                chats
                        ? View.GONE
                        : View.VISIBLE
        );

        if (!chats) {
            return;
        }

        LayoutInflater inflater =
                LayoutInflater.from(requireContext());

        for (String[] chat : SAMPLE_CHATS) {

            View row =
                    inflater.inflate(
                            R.layout.item_residence_conversation,
                            conversationsList,
                            false
                    );

            ((TextView) row.findViewById(R.id.conversationName))
                    .setText(chat[0]);

            ((TextView) row.findViewById(R.id.conversationPreview))
                    .setText(chat[1]);

            TextView unread =
                    row.findViewById(R.id.conversationUnread);

            TextView time =
                    row.findViewById(R.id.conversationTime);

            boolean hasUnread =
                    !chat[2].isEmpty();

            unread.setText(chat[2]);

            unread.setVisibility(
                    hasUnread
                            ? View.VISIBLE
                            : View.GONE
            );

            time.setText(chat[3]);

            time.setVisibility(
                    hasUnread
                            ? View.GONE
                            : View.VISIBLE
            );

            row.findViewById(R.id.conversationRow)
                    .setOnClickListener(v ->
                            Toast.makeText(
                                    requireContext(),
                                    "Chat with " + chat[0] + " will be added soon.",
                                    Toast.LENGTH_SHORT
                            ).show()
                    );

            conversationsList.addView(row);
        }
    }
}
