package ru.yandex.academy.euv.artistexplorer.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public final class Preferences {
    private static final String KEY_NOTIFICATIONS_ENABLED = "notifications_enabled";

    private Preferences() { /* */ }

    public static boolean isNotificationsEnabled(Context context) {
        return getPreferences(context).getBoolean(KEY_NOTIFICATIONS_ENABLED, true);
    }

    public static void saveNotificationSettings(Context context, boolean isEnabled) {
        getPreferences(context).edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, isEnabled).apply();
    }

    private static SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
}
