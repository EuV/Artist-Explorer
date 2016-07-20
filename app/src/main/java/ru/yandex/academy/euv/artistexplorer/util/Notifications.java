package ru.yandex.academy.euv.artistexplorer.util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;

import ru.yandex.academy.euv.artistexplorer.MainActivity;
import ru.yandex.academy.euv.artistexplorer.R;
import ru.yandex.academy.euv.artistexplorer.service.HeadsetService;

import static android.content.Context.NOTIFICATION_SERVICE;

public final class Notifications {
    private static final String YANDEX_MUSIC_PKG = "ru.yandex.music";
    private static final String YANDEX_RADIO_PKG = "ru.yandex.radio";
    private static final int NOTIFICATION_ID = 0;

    private Notifications() { /* */ }

    @SuppressWarnings("deprecation")
    public static void syncState(Context context) {
        Intent headsetMonitor = new Intent(context, HeadsetService.class);
        boolean isEnabled = Preferences.isNotificationsEnabled(context);

        if (isEnabled) {
            context.startService(headsetMonitor);

            AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            if (am.isWiredHeadsetOn()) {
                show(context);
            }
        } else {
            context.stopService(headsetMonitor);
            hide(context);
        }
    }


    public static void show(Context context) {
        Bitmap appIcon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);

        PendingIntent appIntent = getPendingIntent(context, new Intent(context, MainActivity.class));
        PendingIntent musicIntent = getPendingIntent(context, get3rdPartyIntent(context, YANDEX_MUSIC_PKG));
        PendingIntent radioIntent = getPendingIntent(context, get3rdPartyIntent(context, YANDEX_RADIO_PKG));

        NotificationCompat.Builder builder = new Builder(context)
                .setContentTitle(context.getText(R.string.app_name))
                .setContentText(context.getText(R.string.label_headphones_connected))
                .setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(appIcon)
                .setOngoing(true)
                .setContentIntent(appIntent)
                .addAction(R.drawable.ic_notification, context.getText(R.string.label_music), musicIntent)
                .addAction(R.drawable.ic_notification, context.getText(R.string.label_radio), radioIntent);

        getManager(context).notify(NOTIFICATION_ID, builder.build());
    }


    public static void hide(Context context) {
        getManager(context).cancel(NOTIFICATION_ID);
    }


    private static NotificationManager getManager(Context context) {
        return (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
    }


    private static PendingIntent getPendingIntent(Context context, Intent intent) {
        return PendingIntent.getActivity(context, 0, intent, 0);
    }


    private static Intent get3rdPartyIntent(Context context, String pkg) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(pkg);
        if (intent == null) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + pkg));
        }
        return intent;
    }
}
