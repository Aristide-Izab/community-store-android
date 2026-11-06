package com.example.communitystore;

import android.app.Application;

import com.example.communitystore.shared.utils.SystemBarInsets;

/**
 * Application entry point.
 *
 * Registers the shared system bar handling so every screen keeps its
 * content below the status bar and above the navigation bar / keyboard
 * on Android 15+, where apps are always drawn edge-to-edge.
 */
public class CommunityStoreApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(new SystemBarInsets());
    }
}
