package com.example.communitystore.faculty;

import com.example.communitystore.R;
import com.example.communitystore.student.StudentCommunityBoardActivity;

/**
 * Faculty community board: the Student board plus a "Post" button
 * (faculty_community_board layout) for events and announcements.
 */
public class FacultyCommunityBoardActivity extends StudentCommunityBoardActivity {

    @Override
    protected int layoutRes() {
        return R.layout.faculty_community_board;
    }
}
