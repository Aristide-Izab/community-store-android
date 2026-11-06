package com.example.communitystore.shared.data;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/** A community board post, stored in "posts/{id}". */
public class Post {

    public static final String CATEGORY_EVENT = "Event";
    public static final String CATEGORY_ANNOUNCEMENT = "Announcement";

    public String id = "";
    public String authorId = "";
    public String authorName = "";
    public String authorRole = "";
    public String category = CATEGORY_ANNOUNCEMENT;
    public String title = "";
    public String description = "";
    public String location = "";
    public String eventDate = "";
    public long createdAt = 0;
    public List<String> interested = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public static Post fromDocument(DocumentSnapshot document) {
        Post p = new Post();
        p.id = document.getId();
        p.authorId = text(document.getString("authorId"));
        p.authorName = text(document.getString("authorName"));
        p.authorRole = text(document.getString("authorRole"));
        p.category = document.getString("category") == null ? CATEGORY_ANNOUNCEMENT : document.getString("category");
        p.title = text(document.getString("title"));
        p.description = text(document.getString("description"));
        p.location = text(document.getString("location"));
        p.eventDate = text(document.getString("eventDate"));

        Timestamp created = document.getTimestamp("createdAt");
        p.createdAt = created == null ? System.currentTimeMillis() : created.toDate().getTime();

        Object interested = document.get("interested");
        if (interested instanceof List) {
            for (Object uid : (List<Object>) interested) {
                p.interested.add(String.valueOf(uid));
            }
        }
        return p;
    }

    private static String text(String value) {
        return value == null ? "" : value;
    }
}
