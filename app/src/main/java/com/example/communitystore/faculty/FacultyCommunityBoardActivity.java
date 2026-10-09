package com.example.communitystore.faculty;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.communitystore.MainActivity;
import com.example.communitystore.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

public class FacultyCommunityBoardActivity extends AppCompatActivity {

    private static final int ADD_POST_REQUEST_CODE = 101;

    private ImageButton btnBack;
    private MaterialButton chipAll, chipEvents, chipAnnouncements, btnAddPost;
    private LinearLayout postsContainer, postLibrary, postCafeteria, postTutor;
    private ImageButton favoriteLibrary, favoriteCafeteria, favoriteTutor;

    private final List<PostViewHold> dynamicPosts = new ArrayList<>();

    private boolean isLibraryStarred = false;
    private boolean isCafeteriaStarred = false;
    private boolean isTutorStarred = false;

    private static class PostViewHold {
        View view;
        String category;

        PostViewHold(View view, String category) {
            this.view = view;
            this.category = category;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.faculty_community_board);

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        chipAll = findViewById(R.id.chipAll);
        chipEvents = findViewById(R.id.chipEvents);
        chipAnnouncements = findViewById(R.id.chipAnnouncements);
        btnAddPost = findViewById(R.id.btnAddPost);

        postsContainer = findViewById(R.id.postsContainer);

        postLibrary = findViewById(R.id.postLibrary);
        postCafeteria = findViewById(R.id.postCafeteria);
        postTutor = findViewById(R.id.postTutor);

        favoriteLibrary = findViewById(R.id.favoriteLibrary);
        favoriteCafeteria = findViewById(R.id.favoriteCafeteria);
        favoriteTutor = findViewById(R.id.favoriteTutor);
    }

    private void setupListeners() {
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        if (chipAll != null) {
            chipAll.setOnClickListener(v -> {
                updateChipSelection(chipAll);
                showAllPosts();
            });
        }

        if (chipEvents != null) {
            chipEvents.setOnClickListener(v -> {
                updateChipSelection(chipEvents);
                showEventPosts();
            });
        }

        if (chipAnnouncements != null) {
            chipAnnouncements.setOnClickListener(v -> {
                updateChipSelection(chipAnnouncements);
                showAnnouncementPosts();
            });
        }

        if (btnAddPost != null) {
            btnAddPost.setOnClickListener(v -> {
                Intent intent = new Intent(FacultyCommunityBoardActivity.this, FacultyAddPostActivity.class);
                startActivityForResult(intent, ADD_POST_REQUEST_CODE);
            });
        }

        if (favoriteLibrary != null) {
            favoriteLibrary.setOnClickListener(v -> {
                isLibraryStarred = !isLibraryStarred;
                favoriteLibrary.setImageResource(isLibraryStarred ? R.drawable.star_filled : R.drawable.star);
                Toast.makeText(this, isLibraryStarred ? "Added Library post to Interested" : "Removed from Interested", Toast.LENGTH_SHORT).show();
            });
        }

        if (favoriteCafeteria != null) {
            favoriteCafeteria.setOnClickListener(v -> {
                isCafeteriaStarred = !isCafeteriaStarred;
                favoriteCafeteria.setImageResource(isCafeteriaStarred ? R.drawable.star_filled : R.drawable.star);
                Toast.makeText(this, isCafeteriaStarred ? "Added Cafeteria post to Interested" : "Removed from Interested", Toast.LENGTH_SHORT).show();
            });
        }

        if (favoriteTutor != null) {
            favoriteTutor.setOnClickListener(v -> {
                isTutorStarred = !isTutorStarred;
                favoriteTutor.setImageResource(isTutorStarred ? R.drawable.star_filled : R.drawable.star);
                Toast.makeText(this, isTutorStarred ? "Added Tutor post to Interested" : "Removed from Interested", Toast.LENGTH_SHORT).show();
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_POST_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            String title = data.getStringExtra("post_title");
            String category = data.getStringExtra("post_category");
            String dateTime = data.getStringExtra("post_datetime");
            String location = data.getStringExtra("post_location");
            String description = data.getStringExtra("post_description");

            View newPostView = createPostCardView(title, category, dateTime, location, description);

            if (postsContainer != null) {
                postsContainer.addView(newPostView, 0);
            }

            dynamicPosts.add(new PostViewHold(newPostView, category));
            Toast.makeText(this, "Published successfully!", Toast.LENGTH_SHORT).show();

            showAllPosts();
            if (chipAll != null) updateChipSelection(chipAll);
        }
    }

    private View createPostCardView(String title, String category, String dateTime, String location, String description) {
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
        int iconSize = (int) (44 * getResources().getDisplayMetrics().density);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(iconSize, iconSize);
        iconParams.setMarginEnd(14);
        iconView.setLayoutParams(iconParams);
        iconView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        if ("Event".equals(category)) {
            iconView.setImageResource(R.drawable.ic_book);
        } else {
            iconView.setImageResource(R.drawable.ic_tutor);
        }
        outerLayout.addView(iconView);

        LinearLayout textContainer = new LinearLayout(this);
        textContainer.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        textContainer.setLayoutParams(textParams);

        TextView tvTitle = new TextView(this);
        tvTitle.setText(title);
        tvTitle.setTextSize(17f);
        tvTitle.setTextColor(Color.parseColor("#111827"));
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        textContainer.addView(tvTitle);

        TextView tvDetails = new TextView(this);
        tvDetails.setText(dateTime + " • " + location);
        tvDetails.setTextSize(13f);
        tvDetails.setTextColor(Color.parseColor("#FF4B26"));
        tvDetails.setPadding(0, 2, 0, 4);
        textContainer.addView(tvDetails);

        TextView tvDesc = new TextView(this);
        tvDesc.setText(description);
        tvDesc.setTextSize(13f);
        tvDesc.setTextColor(Color.parseColor("#4B5563"));
        textContainer.addView(tvDesc);

        outerLayout.addView(textContainer);

        LinearLayout rightContainer = new LinearLayout(this);
        rightContainer.setOrientation(LinearLayout.VERTICAL);
        rightContainer.setGravity(Gravity.END);

        TextView tvTimeAgo = new TextView(this);
        tvTimeAgo.setText("Just now");
        tvTimeAgo.setTextSize(12f);
        tvTimeAgo.setTextColor(Color.parseColor("#6B7280"));
        rightContainer.addView(tvTimeAgo);

        ImageButton favoriteButton = new ImageButton(this);
        int starSize = (int) (32 * getResources().getDisplayMetrics().density);
        LinearLayout.LayoutParams starParams = new LinearLayout.LayoutParams(starSize, starSize);
        starParams.setMargins(0, 4, 0, 0);
        favoriteButton.setLayoutParams(starParams);
        favoriteButton.setBackgroundColor(Color.TRANSPARENT);
        favoriteButton.setImageResource(R.drawable.star);
        favoriteButton.setContentDescription("Save post");

        final boolean[] isStarred = {false};
        favoriteButton.setOnClickListener(v -> {
            isStarred[0] = !isStarred[0];
            favoriteButton.setImageResource(isStarred[0] ? R.drawable.star_filled : R.drawable.star);
            Toast.makeText(this, isStarred[0] ? "Added to Interested" : "Removed from Interested", Toast.LENGTH_SHORT).show();
        });
        rightContainer.addView(favoriteButton);

        outerLayout.addView(rightContainer);
        cardView.addView(outerLayout);

        return cardView;
    }

    private void updateChipSelection(MaterialButton selectedChip) {
        int greyBg = Color.parseColor("#E5E7EB");
        int darkText = Color.parseColor("#333333");
        int orangeColor = Color.parseColor("#FF4B26");

        if (chipAll != null) {
            chipAll.setBackgroundTintList(android.content.res.ColorStateList.valueOf(greyBg));
            chipAll.setTextColor(darkText);
        }
        if (chipEvents != null) {
            chipEvents.setBackgroundTintList(android.content.res.ColorStateList.valueOf(greyBg));
            chipEvents.setTextColor(darkText);
        }
        if (chipAnnouncements != null) {
            chipAnnouncements.setBackgroundTintList(android.content.res.ColorStateList.valueOf(greyBg));
            chipAnnouncements.setTextColor(darkText);
        }

        if (selectedChip != null) {
            selectedChip.setBackgroundTintList(android.content.res.ColorStateList.valueOf(orangeColor));
            selectedChip.setTextColor(Color.WHITE);
        }
    }

    private void showAllPosts() {
        if (postLibrary != null) postLibrary.setVisibility(View.VISIBLE);
        if (postCafeteria != null) postCafeteria.setVisibility(View.VISIBLE);
        if (postTutor != null) postTutor.setVisibility(View.VISIBLE);

        for (PostViewHold post : dynamicPosts) {
            if (post.view != null) post.view.setVisibility(View.VISIBLE);
        }
    }

    private void showEventPosts() {
        if (postLibrary != null) postLibrary.setVisibility(View.VISIBLE);
        if (postCafeteria != null) postCafeteria.setVisibility(View.GONE);
        if (postTutor != null) postTutor.setVisibility(View.GONE);

        for (PostViewHold post : dynamicPosts) {
            if (post.view != null) {
                post.view.setVisibility("Event".equals(post.category) ? View.VISIBLE : View.GONE);
            }
        }
    }

    private void showAnnouncementPosts() {
        if (postLibrary != null) postLibrary.setVisibility(View.GONE);
        if (postCafeteria != null) postCafeteria.setVisibility(View.VISIBLE);
        if (postTutor != null) postTutor.setVisibility(View.VISIBLE);

        for (PostViewHold post : dynamicPosts) {
            if (post.view != null) {
                post.view.setVisibility("Announcement".equals(post.category) ? View.VISIBLE : View.GONE);
            }
        }
    }
}
