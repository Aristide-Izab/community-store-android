package com.example.communitystore.shared.data;

import android.content.Context;

import androidx.annotation.Nullable;

import com.example.communitystore.core.access.UserRole;
import com.example.communitystore.core.session.AuthSession;
import com.example.communitystore.core.session.SessionManager;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Community board posts. */
public final class PostRepository {

    public interface PostsListener {
        void onChanged(List<Post> posts, @Nullable Exception error);
    }

    private PostRepository() {
    }

    private static FirebaseFirestore db() {
        return FirebaseFirestore.getInstance();
    }

    /** Live list of the latest 100 posts, newest first. */
    public static ListenerRegistration listen(PostsListener listener) {
        return db().collection("posts")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(100)
                .addSnapshotListener((snapshot, error) -> {
                    List<Post> posts = new ArrayList<>();
                    if (snapshot != null) {
                        for (DocumentSnapshot document : snapshot.getDocuments()) {
                            posts.add(Post.fromDocument(document));
                        }
                    }
                    listener.onChanged(posts, error);
                });
    }

    public static Task<Void> create(
            Context context,
            String category,
            String title,
            String eventDate,
            String location,
            String description
    ) {
        SessionManager session = new SessionManager(context);
        UserRole role = session.getUserRole();

        Map<String, Object> data = new HashMap<>();
        data.put("authorId", AuthSession.uid());
        data.put("authorName", session.getDisplayName());
        data.put("authorRole", role == null ? "" : role.getValue());
        data.put("category", category);
        data.put("title", title);
        data.put("eventDate", eventDate);
        data.put("location", location);
        data.put("description", description);
        data.put("interested", new ArrayList<String>());
        data.put("createdAt", FieldValue.serverTimestamp());

        return db().collection("posts").document().set(data);
    }

    /** Adds or removes the signed-in user from a post's "interested" list. */
    public static Task<Void> setInterested(String postId, boolean interested) {
        String uid = AuthSession.uid();
        return db().collection("posts")
                .document(postId)
                .update("interested", interested
                        ? FieldValue.arrayUnion(uid)
                        : FieldValue.arrayRemove(uid));
    }

    public static Task<Void> delete(String postId) {
        return db().collection("posts").document(postId).delete();
    }
}
