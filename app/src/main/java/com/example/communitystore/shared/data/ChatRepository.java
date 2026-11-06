package com.example.communitystore.shared.data;

import android.content.Context;

import androidx.annotation.Nullable;

import com.example.communitystore.core.access.UserRole;
import com.example.communitystore.core.session.AuthSession;
import com.example.communitystore.core.session.SessionManager;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Real-time one-to-one messaging on Firestore. */
public final class ChatRepository {

    public static class Message {
        public String id = "";
        public String senderId = "";
        public String text = "";
        public long createdAt = 0;
    }

    public interface ConversationsListener {
        void onChanged(List<Conversation> conversations, @Nullable Exception error);
    }

    public interface MessagesListener {
        void onChanged(List<Message> messages, @Nullable Exception error);
    }

    private ChatRepository() {
    }

    private static FirebaseFirestore db() {
        return FirebaseFirestore.getInstance();
    }

    /** Live list of the signed-in user's conversations, most recent first. */
    public static ListenerRegistration listenToConversations(ConversationsListener listener) {
        String uid = AuthSession.uid();

        if (uid == null) {
            listener.onChanged(new ArrayList<>(), null);
            return null;
        }

        return db().collection("conversations")
                .whereArrayContains("participants", uid)
                .addSnapshotListener((snapshot, error) -> {
                    List<Conversation> list = new ArrayList<>();

                    if (snapshot != null) {
                        for (DocumentSnapshot document : snapshot.getDocuments()) {
                            list.add(Conversation.fromDocument(document, uid));
                        }
                        Collections.sort(list, (a, b) -> Long.compare(b.lastMessageAt, a.lastMessageAt));
                    }

                    listener.onChanged(list, error);
                });
    }

    /** Live messages of one conversation, oldest first. */
    public static ListenerRegistration listenToMessages(String conversationId, MessagesListener listener) {
        return db().collection("conversations")
                .document(conversationId)
                .collection("messages")
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshot, error) -> {
                    List<Message> list = new ArrayList<>();

                    if (snapshot != null) {
                        for (DocumentSnapshot document : snapshot.getDocuments()) {
                            Message m = new Message();
                            m.id = document.getId();
                            m.senderId = document.getString("senderId") == null ? "" : document.getString("senderId");
                            m.text = document.getString("text") == null ? "" : document.getString("text");
                            com.google.firebase.Timestamp at = document.getTimestamp("createdAt");
                            // Pending local writes have no server time yet: show them as "now".
                            m.createdAt = at == null ? System.currentTimeMillis() : at.toDate().getTime();
                            list.add(m);
                        }
                    }

                    listener.onChanged(list, error);
                });
    }

    /**
     * Makes sure the conversation document exists. The first person to open it
     * is recorded as "startedBy", so it shows as a request for the other person.
     */
    public static Task<Void> ensureConversation(
            Context context,
            String otherId,
            String otherName,
            String otherRole,
            String subject
    ) {
        String uid = AuthSession.uid();
        String id = Conversation.idFor(uid, otherId);
        DocumentReference ref = db().collection("conversations").document(id);

        return ref.get().continueWithTask(task -> {
            DocumentSnapshot existing = task.isSuccessful() ? task.getResult() : null;

            SessionManager session = new SessionManager(context);
            UserRole myRole = session.getUserRole();

            Map<String, Object> names = new HashMap<>();
            names.put(uid, session.getDisplayName().isEmpty() ? "Community member" : session.getDisplayName());
            names.put(otherId, otherName == null || otherName.isEmpty() ? "Community member" : otherName);

            Map<String, Object> roles = new HashMap<>();
            roles.put(uid, myRole == null ? "" : myRole.getValue());
            roles.put(otherId, otherRole == null ? "" : otherRole);

            Map<String, Object> data = new HashMap<>();
            // Always the same order, so a merge never changes the list (see firestore.rules).
            data.put("participants", uid.compareTo(otherId) < 0
                    ? Arrays.asList(uid, otherId)
                    : Arrays.asList(otherId, uid));
            data.put("names", names);
            data.put("roles", roles);

            if (existing == null || !existing.exists()) {
                Map<String, Object> unread = new HashMap<>();
                unread.put(uid, 0);
                unread.put(otherId, 0);

                data.put("startedBy", uid);
                data.put("accepted", false);
                data.put("lastMessage", "");
                data.put("lastSenderId", "");
                data.put("subject", subject == null ? "" : subject);
                data.put("unread", unread);
                data.put("lastMessageAt", FieldValue.serverTimestamp());
                data.put("createdAt", FieldValue.serverTimestamp());
            }

            return ref.set(data, SetOptions.merge());
        });
    }

    /** Sends a message and updates the conversation preview / unread count. */
    public static Task<Void> send(String conversationId, String otherId, String startedBy, String text) {
        String uid = AuthSession.uid();
        DocumentReference conversation = db().collection("conversations").document(conversationId);
        DocumentReference message = conversation.collection("messages").document();

        Map<String, Object> messageData = new HashMap<>();
        messageData.put("senderId", uid);
        messageData.put("text", text);
        messageData.put("createdAt", FieldValue.serverTimestamp());

        Map<String, Object> update = new HashMap<>();
        update.put("lastMessage", text);
        update.put("lastSenderId", uid);
        update.put("lastMessageAt", FieldValue.serverTimestamp());
        update.put("unread." + otherId, FieldValue.increment(1));

        // Replying to someone else's chat accepts it (it moves from Requests to Chats).
        if (startedBy != null && !startedBy.isEmpty() && !startedBy.equals(uid)) {
            update.put("accepted", true);
        }

        WriteBatch batch = db().batch();
        batch.set(message, messageData);
        batch.update(conversation, update);
        return batch.commit();
    }

    /** Clears the signed-in user's unread count for a conversation. */
    public static void markRead(String conversationId) {
        String uid = AuthSession.uid();
        if (uid == null) {
            return;
        }
        db().collection("conversations")
                .document(conversationId)
                .update("unread." + uid, 0);
    }

    public static Task<DocumentSnapshot> get(String conversationId) {
        return db().collection("conversations").document(conversationId).get();
    }
}
