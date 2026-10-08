package com.example.communitystore.core.session;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.communitystore.core.access.UserRole;

public class SessionManager {

    private static final String PREF_NAME =
            "CommunityStorePrefs";

    private static final String KEY_ACCOUNT_TYPE =
            "accountType";

    private final SharedPreferences preferences;

    public SessionManager(Context context) {

        preferences =
                context.getSharedPreferences(
                        PREF_NAME,
                        Context.MODE_PRIVATE
                );
    }

    public void saveUserRole(UserRole role) {

        if (role == null) {
            return;
        }

        preferences.edit()
                .putString(
                        KEY_ACCOUNT_TYPE,
                        role.getValue()
                )
                .apply();
    }

    public UserRole getUserRole() {

        String role =
                preferences.getString(
                        KEY_ACCOUNT_TYPE,
                        null
                );

        return UserRole.fromString(role);
    }

    public void clearSession() {

        preferences.edit()
                .remove(KEY_ACCOUNT_TYPE)
                .apply();
    }
}
