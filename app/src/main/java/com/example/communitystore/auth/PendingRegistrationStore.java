package com.example.communitystore.auth;

import android.content.Context;
import android.content.SharedPreferences;

public class PendingRegistrationStore {

    private static final String PREFS =
            "PendingRegistration";

    private static final String UID =
            "uid";

    private static final String DISPLAY_NAME =
            "displayName";

    private static final String UNIVERSITY_NAME =
            "universityName";

    private static final String EMAIL =
            "email";

    private static final String ROLE =
            "role";

    public static void save(
            Context context,
            String uid,
            String displayName,
            String universityName,
            String email,
            String role
    ) {

        SharedPreferences preferences =
                context.getSharedPreferences(
                        PREFS,
                        Context.MODE_PRIVATE
                );

        preferences.edit()
                .putString(UID, uid)
                .putString(DISPLAY_NAME, displayName)
                .putString(
                        UNIVERSITY_NAME,
                        universityName == null
                                ? ""
                                : universityName
                )
                .putString(EMAIL, email)
                .putString(ROLE, role)
                .apply();
    }

    public static String getUid(
            Context context
    ) {

        return getPreferences(context)
                .getString(UID, null);
    }

    public static String getDisplayName(
            Context context
    ) {

        return getPreferences(context)
                .getString(DISPLAY_NAME, "");
    }

    public static String getUniversityName(
            Context context
    ) {

        return getPreferences(context)
                .getString(
                        UNIVERSITY_NAME,
                        ""
                );
    }

    public static String getEmail(
            Context context
    ) {

        return getPreferences(context)
                .getString(EMAIL, "");
    }

    public static String getRole(
            Context context
    ) {

        return getPreferences(context)
                .getString(ROLE, null);
    }

    public static boolean belongsTo(
            Context context,
            String uid
    ) {

        String savedUid =
                getUid(context);

        return savedUid != null
                && savedUid.equals(uid);
    }

    public static void clear(
            Context context
    ) {

        getPreferences(context)
                .edit()
                .clear()
                .apply();
    }

    private static SharedPreferences getPreferences(
            Context context
    ) {

        return context.getSharedPreferences(
                PREFS,
                Context.MODE_PRIVATE
        );
    }
}
