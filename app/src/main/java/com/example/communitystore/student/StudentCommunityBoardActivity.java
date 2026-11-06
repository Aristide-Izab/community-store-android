package com.example.communitystore.student;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.communitystore.R;
import com.example.communitystore.core.access.BottomNavThemeManager;
import com.example.communitystore.core.access.UserRole;
import com.example.communitystore.core.session.AuthSession;
import com.example.communitystore.faculty.FacultyAddPostActivity;
import com.example.communitystore.shared.data.Formatters;
import com.example.communitystore.shared.data.Post;
import com.example.communitystore.shared.data.PostRepository;
import com.example.communitystore.shared.utils.BottomNavHelper;
import com.example.communitystore.shared.utils.ThemeUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

/**
 * Community board (Student; Faculty extends this with a "Post" button).
 *
 * Shows live posts from Firestore "posts" above the three built-in sample
 * posts. Filters: All / Events / Announcements. The star marks a post as
 * "Interested" and is saved per user.
 */
public class StudentCommunityBoardActivity extends AppCompatActivity {

    private static final String FILTER_ALL = "All";

    private MaterialButton chipAll;
    private MaterialButton chipEvents;
    private MaterialButton chipAnnouncements;
    private LinearLayout postsContainer;
    private LinearLayout postLibrary;
    private LinearLayout postCafeteria;
    private LinearLayout postTutor;

    private final List<PostView> livePosts = new ArrayList<>();
    private String filter = FILTER_ALL;
    private ListenerRegistration registration;

    private static class PostView {
        final View view;
        final String category;

        PostView(View view, String category) {
            this.view = view;
            this.category = category;
        }
    }

    /** Layout to use. Faculty overrides this with the board that has a Post button. */
    protected int layoutRes() {
        return R.layout.student_community_board;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutRes());

        chipAll = findViewById(R.id.chipAll);
        chipEvents = findViewById(R.id.chipEvents);
        chipAnnouncements = findViewById(R.id.chipAnnouncements);
        postsContainer = findViewById(R.id.postsContainer);
        postLibrary = findViewById(R.id.postLibrary);
        postCafeteria = findViewById(R.id.postCafeteria);
        postTutor = findViewById(R.id.postTutor);

        ImageButton btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        chipAll.setOnClickListener(v -> applyFilter(FILTER_ALL));
        chipEvents.setOnClickListener(v -> applyFilter(Post.CATEGORY_EVENT));
        chipAnnouncements.setOnClickListener(v -> applyFilter(Post.CATEGORY_ANNOUNCEMENT));

        MaterialButton btnAddPost = findViewById(R.id.btnAddPost);
        if (btnAddPost != null) {
            btnAddPost.setBackgroundTintList(ColorStateList.valueOf(ThemeUtils.primary(this)));
            btnAddPost.setOnClickListener(v ->
                    startActivity(new Intent(this, FacultyAddPostActivity.class)));
        }

        setupSampleStar(R.id.favoriteLibrary);
        setupSampleStar(R.id.favoriteCafeteria);
        setupSampleStar(R.id.favoriteTutor);

        applyFilter(FILTER_ALL);
        BottomNavHelper.setup(this, "community");
    }

    @Override
    protected void onStart() {
        super.onStart();
        registration = PostRepository.listen((posts, error) -> {
            showLivePosts(posts);
            if (error != null) {
                Toast.makeText(this, "Couldn't load new posts.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (registration != null) {
            registration.remove();
            registration = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavThemeManager.apply(this, "community");
    }

    /** Built-in sample posts: the star is only remembered while the screen is open. */
    private void setupSampleStar(int id) {
        ImageButton star = findViewById(id);
        if (star == null) {
            return;
        }
        star.setOnClickListener(v -> {
            boolean on = !Boolean.TRUE.equals(star.getTag());
            star.setTag(on);
            star.setImageResource(on ? R.drawable.star_filled : R.drawable.star);
            Toast.makeText(this, on ? "Added to Interested posts" : "Removed from Interested posts",
                    Toast.LENGTH_SHORT).show();
        });
    }

    private void showLivePosts(List<Post> posts) {
        for (PostView post : livePosts) {
            postsContainer.removeView(post.view);
        }
        livePosts.clear();

        for (int i = 0; i < posts.size(); i++) {
            Post post = posts.get(i);
            View card = createPostCardView(post);
            postsContainer.addView(card, i);
            livePosts.add(new PostView(card, post.category));
        }

        applyFilter(filter);
    }

    private void applyFilter(String newFilter) {
        filter = newFilter;

        boolean all = FILTER_ALL.equals(filter);
        boolean events = Post.CATEGORY_EVENT.equals(filter);
        boolean announcements = Post.CATEGORY_ANNOUNCEMENT.equals(filter);

        // Samples: the library post is an event, the other two are announcements.
        setVisible(postLibrary, all || events);
        setVisible(postCafeteria, all || announcements);
        setVisible(postTutor, all || announcements);

        for (PostView post : livePosts) {
            setVisible(post.view, all || filter.equals(post.category));
        }

        styleChip(chipAll, all);
        styleChip(chipEvents, events);
        styleChip(chipAnnouncements, announcements);
    }

    private static void setVisible(View view, boolean visible) {
        if (view != null) {
            view.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    private void styleChip(MaterialButton chip, boolean selected) {
        if (chip == null) {
            return;
        }
        chip.setBackgroundTintList(ColorStateList.valueOf(
                selected ? ThemeUtils.primary(this) : Color.parseColor("#E5E7EB")));
        chip.setTextColor(selected ? Color.WHITE : Color.parseColor("#333333"));
    }

    private View createPostCardView(Post post) {
        float density = getResources().getDisplayMetrics().density;
        String uid = AuthSession.uid();

        CardView cardView = new CardView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 0, 0, 12);
        cardView.setLayoutParams(layoutParams);
        cardView.setRadius(12f);
        cardView.setCardElevation(2f);
        cardView.setCardBackgroundColor(Color.WHITE);
        cardView.setUseCompatPadding(true);

        LinearLayout outerLayout = new LinearLayout(this);
        outerLayout.setOrientation(LinearLayout.HORIZONTAL);
        outerLayout.setGravity(Gravity.CENTER_VERTICAL);
        outerLayout.setPadding(24, 24, 24, 24);

        ShapeableImageView iconView = new ShapeableImageView(this);
        int iconSize = (int) (44 * density);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(iconSize, iconSize);
        iconParams.setMarginEnd(14);
        iconView.setLayoutParams(iconParams);
        iconView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        iconView.setImageResource(Post.CATEGORY_EVENT.equals(post.category) ? R.drawable.ic_book : R.drawable.ic_tutor);
        outerLayout.addView(iconView);

        LinearLayout textContainer = new LinearLayout(this);
        textContainer.setOrientation(LinearLayout.VERTICAL);
        textContainer.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        UserRole authorRole = UserRole.fromString(post.authorRole);

        TextView badge = new TextView(this);
        badge.setText(post.category.toUpperCase(java.util.Locale.ROOT)
                + (authorRole == null ? "" : " • " + authorRole.getLabel().toUpperCase(java.util.Locale.ROOT)));
        badge.setTextSize(10f);
        badge.setTypeface(null, android.graphics.Typeface.BOLD);
        badge.setTextColor(ThemeUtils.colorFor(this, authorRole));
        textContainer.addView(badge);

        TextView tvTitle = new TextView(this);
        tvTitle.setText(post.title);
        tvTitle.setTextSize(17f);
        tvTitle.setTextColor(Color.parseColor("#111827"));
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        textContainer.addView(tvTitle);

        List<String> details = new ArrayList<>();
        if (!post.eventDate.isEmpty()) {
            details.add(post.eventDate);
        }
        if (!post.location.isEmpty()) {
            details.add(post.location);
        }

        if (!details.isEmpty()) {
            TextView tvDetails = new TextView(this);
            tvDetails.setText(android.text.TextUtils.join(" • ", details));
            tvDetails.setTextSize(13f);
            tvDetails.setTextColor(ThemeUtils.primary(this));
            tvDetails.setPadding(0, 2, 0, 4);
            textContainer.addView(tvDetails);
        }

        TextView tvDesc = new TextView(this);
        tvDesc.setText(post.description);
        tvDesc.setTextSize(13f);
        tvDesc.setTextColor(Color.parseColor("#4B5563"));
        textContainer.addView(tvDesc);

        TextView tvAuthor = new TextView(this);
        tvAuthor.setText("By " + (post.authorName.isEmpty() ? "Community member" : post.authorName)
                + (post.interested.isEmpty() ? "" : " • " + post.interested.size() + " interested"));
        tvAuthor.setTextSize(11f);
        tvAuthor.setTextColor(Color.parseColor("#6B7280"));
        tvAuthor.setPadding(0, 4, 0, 0);
        textContainer.addView(tvAuthor);

        outerLayout.addView(textContainer);

        LinearLayout rightContainer = new LinearLayout(this);
        rightContainer.setOrientation(LinearLayout.VERTICAL);
        rightContainer.setGravity(Gravity.END);

        TextView tvTimeAgo = new TextView(this);
        tvTimeAgo.setText(Formatters.shortTime(post.createdAt));
        tvTimeAgo.setTextSize(12f);
        tvTimeAgo.setTextColor(Color.parseColor("#6B7280"));
        rightContainer.addView(tvTimeAgo);

        ImageButton favoriteButton = new ImageButton(this);
        int starSize = (int) (32 * density);
        LinearLayout.LayoutParams starParams = new LinearLayout.LayoutParams(starSize, starSize);
        starParams.setMargins(0, 4, 0, 0);
        favoriteButton.setLayoutParams(starParams);
        favoriteButton.setBackgroundColor(Color.TRANSPARENT);
        favoriteButton.setContentDescription("Interested");

        boolean interested = uid != null && post.interested.contains(uid);
        favoriteButton.setImageResource(interested ? R.drawable.star_filled : R.drawable.star);
        favoriteButton.setOnClickListener(v ->
                PostRepository.setInterested(post.id, !interested)
                        .addOnSuccessListener(unused -> Toast.makeText(
                                this,
                                !interested ? "Added to Interested posts" : "Removed from Interested posts",
                                Toast.LENGTH_SHORT
                        ).show()));
        rightContainer.addView(favoriteButton);

        outerLayout.addView(rightContainer);
        cardView.addView(outerLayout);

        // Authors can remove their own posts with a long press.
        if (uid != null && uid.equals(post.authorId)) {
            cardView.setOnLongClickListener(v -> {
                new AlertDialog.Builder(this)
                        .setTitle("Delete post?")
                        .setMessage("\"" + post.title + "\" will be removed for everyone.")
                        .setPositiveButton("Delete", (d, w) -> PostRepository.delete(post.id))
                        .setNegativeButton("Cancel", null)
                        .show();
                return true;
            });
        }

        return cardView;
    }
}
