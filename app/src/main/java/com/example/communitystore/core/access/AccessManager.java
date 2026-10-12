package com.example.communitystore.core.access;

import android.app.Activity;
import android.content.Intent;

import com.example.communitystore.faculty.FacultyHomeActivity;
import com.example.communitystore.residence.ResidenceHomeActivity;
import com.example.communitystore.student.StudentHomeActivity;
import com.example.communitystore.vendor.VendorHomeActivity;

public class AccessManager {

    private AccessManager() {
    }

    public static void openHome(
            Activity activity,
            UserRole role
    ) {

        if (activity == null || role == null) {
            return;
        }

        Intent intent;

        switch (role) {

            case STUDENT:

                intent =
                        new Intent(
                                activity,
                                StudentHomeActivity.class
                        );

                break;

            case FACULTY:

                intent =
                        new Intent(
                                activity,
                                FacultyHomeActivity.class
                        );

                break;

            case VENDOR:

                intent =
                        new Intent(
                                activity,
                                VendorHomeActivity.class
                        );

                break;

            case RESIDENCE:

                intent =
                        new Intent(
                                activity,
                                ResidenceHomeActivity.class
                        );

                break;

            default:
                return;
        }

        intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        activity.startActivity(intent);
        activity.finish();
    }

    public static boolean hasRole(
            UserRole currentRole,
            UserRole requiredRole
    ) {

        return currentRole != null
                && currentRole == requiredRole;
    }
}
