package com.example.communitystore.residence;

import com.example.communitystore.R;
import com.example.communitystore.shared.chat.ConversationList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

// messages tab: live chats and requests (shared with every account type)
public class ResidenceMessagesFragment extends Fragment {

    private ConversationList conversations;

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

        ResidenceHomeActivity home =
                (ResidenceHomeActivity) requireActivity();

        view.findViewById(R.id.backButton)
                .setOnClickListener(v ->
                        home.selectTab(ResidenceHomeActivity.TAB_HOME)
                );

        conversations = new ConversationList(
                requireActivity(),
                view.findViewById(R.id.conversationsRecycler),
                view.findViewById(R.id.messagesEmptyText),
                view.findViewById(R.id.chatsTab),
                view.findViewById(R.id.requestsTab),
                true
        );
    }

    @Override
    public void onStart() {
        super.onStart();
        conversations.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        conversations.stop();
    }
}
