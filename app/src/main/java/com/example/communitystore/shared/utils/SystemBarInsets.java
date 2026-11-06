package com.example.communitystore.shared.utils;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.communitystore.R;

/**
 * Android 15+ (API 35) always draws apps edge-to-edge when targetSdk is 35 or
 * higher. This app targets 37, so without this class the headers would sit
 * under the status bar and the bottom navigation under the gesture bar.
 *
 * Screens that handle insets themselves (the Residence screens) mark their
 * content view with R.id.tag_insets_handled and are skipped.
 */
public class SystemBarInsets implements Application.ActivityLifecycleCallbacks {

    private static final int EDGE_TO_EDGE_ENFORCED_SDK = 35;

    public static void markHandled(Activity activity) {
        View content = activity.findViewById(android.R.id.content);
        if (content != null) {
            content.setTag(R.id.tag_insets_handled, Boolean.TRUE);
        }
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        Window window = activity.getWindow();
        View content = activity.findViewById(android.R.id.content);
        if (window == null
                || content == null
                || content.getTag(R.id.tag_insets_handled) != null) {
            return;
        }

        WindowInsetsControllerCompat controller =
                WindowCompat.getInsetsController(window, window.getDecorView());
        controller.setAppearanceLightStatusBars(true);
        controller.setAppearanceLightNavigationBars(true);

        if (Build.VERSION.SDK_INT < EDGE_TO_EDGE_ENFORCED_SDK) {
            return;
        }

        content.setTag(R.id.tag_insets_handled, Boolean.TRUE);

        ViewCompat.setOnApplyWindowInsetsListener(content, (view, insets) -> {
            Insets bars = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                            | WindowInsetsCompat.Type.displayCutout()
            );
            Insets ime = insets.getInsets(WindowInsetsCompat.Type.ime());

            view.setPadding(
                    bars.left,
                    bars.top,
                    bars.right,
                    Math.max(bars.bottom, ime.bottom)
            );

            return WindowInsetsCompat.CONSUMED;
        });

        ViewCompat.requestApplyInsets(content);
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
    }
}
