package com.example.communitystore.shared.data;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;
import java.util.Map;

/**
 * A one-to-one chat, stored in "conversations/{id}" with messages in
 * "conversations/{id}/messages". The id is the two user ids sorted and joined,
 * so the same two people always share one conversation.
 */
public class Conversation {

    public String id = "";
    public String otherId = "";
    public String otherName = "";
    public String otherRole = "";
    public String lastMessage = "";
    public String lastSenderId = "";
    public String startedBy = "";
    public String subject = "";
    public boolean accepted = false;
    public long lastMessageAt = 0;
    public long unread = 0;

    /** A request is a chat someone else started that you haven't replied to yet. */
    public boolean isRequestFor(String uid) {
        return !accepted && !uid.equals(startedBy);
    }

    @SuppressWarnings("unchecked")
    public static Conversation fromDocument(DocumentSnapshot document, String myUid) {
        Conversation c = new Conversation();
        c.id = document.getId();

        Object participants = document.get("participants");
        if (participants instanceof List) {
            for (Object p : (List<Object>) participants) {
                String uid = String.valueOf(p);
                if (!uid.equals(myUid)) {
                    c.otherId = uid;
                }
            }
        }

        Object names = document.get("names");
        if (names instanceof Map) {
            Object name = ((Map<String, Object>) names).get(c.otherId);
            c.otherName = name == null ? "Community member" : String.valueOf(name);
        }

        Object roles = document.get("roles");
        if (roles instanceof Map) {
            Object role = ((Map<String, Object>) roles).get(c.otherId);
            c.otherRole = role == null ? "" : String.valueOf(role);
        }

        Object unread = document.get("unread");
        if (unread instanceof Map) {
            c.unread = Product.number(((Map<String, Object>) unread).get(myUid), 0);
        }

        c.lastMessage = text(document.getString("lastMessage"));
        c.lastSenderId = text(document.getString("lastSenderId"));
        c.startedBy = text(document.getString("startedBy"));
        c.subject = text(document.getString("subject"));
        c.accepted = Boolean.TRUE.equals(document.getBoolean("accepted"));

        Timestamp at = document.getTimestamp("lastMessageAt");
        c.lastMessageAt = at == null ? System.currentTimeMillis() : at.toDate().getTime();
        return c;
    }

    public static String idFor(String uidA, String uidB) {
        return uidA.compareTo(uidB) < 0 ? uidA + "_" + uidB : uidB + "_" + uidA;
    }

    private static String text(String value) {
        return value == null ? "" : value;
    }
}
