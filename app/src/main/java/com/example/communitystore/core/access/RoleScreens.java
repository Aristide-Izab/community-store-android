package com.example.communitystore.core.access;

import android.content.Context;
import android.content.Intent;

import com.example.communitystore.core.session.SessionManager;
import com.example.communitystore.faculty.FacultyHomeActivity;
import com.example.communitystore.faculty.FacultyProfileActivity;
import com.example.communitystore.residence.ResidenceHomeActivity;
import com.example.communitystore.student.StudentHomeActivity;
import com.example.communitystore.student.StudentProfileActivity;
import com.example.communitystore.vendor.VendorHomeActivity;
import com.example.communitystore.vendor.VendorProfileActivity;

/** Opens the right screen for the signed-in account type. */
public final class RoleScreens {

    private RoleScreens() {
    }

    private static UserRole role(Context context) {
        UserRole role = new SessionManager(context).getUserRole();
        return role == null ? UserRole.STUDENT : role;
    }

    public static void openProfile(Context context) {
        switch (role(context)) {
            case FACULTY:
                context.startActivity(new Intent(context, FacultyProfileActivity.class));
                break;
            case VENDOR:
                context.startActivity(new Intent(context, VendorProfileActivity.class));
                break;
            case RESIDENCE:
                Intent residence = new Intent(context, ResidenceHomeActivity.class);
                residence.putExtra(ResidenceHomeActivity.EXTRA_TAB, ResidenceHomeActivity.TAB_PROFILE);
                residence.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(residence);
                break;
            case STUDENT:
            default:
                context.startActivity(new Intent(context, StudentProfileActivity.class));
                break;
        }
    }

    public static Class<?> homeFor(Context context) {
        switch (role(context)) {
            case FACULTY:
                return FacultyHomeActivity.class;
            case VENDOR:
                return VendorHomeActivity.class;
            case RESIDENCE:
                return ResidenceHomeActivity.class;
            case STUDENT:
            default:
                return StudentHomeActivity.class;
        }
    }
}
