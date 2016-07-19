package ru.yandex.academy.euv.artistexplorer.receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;

import ru.yandex.academy.euv.artistexplorer.MainActivity;
import ru.yandex.academy.euv.artistexplorer.R;

import static android.content.Context.NOTIFICATION_SERVICE;

public class HeadsetBroadcastReceiver extends BroadcastReceiver {
    private static final String YANDEX_MUSIC_PKG = "ru.yandex.music";
    private static final String YANDEX_RADIO_PKG = "ru.yandex.radio";
    private static final String EXTRA_STATE = "state";
    private static final int NOTIFICATION_ID = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isPlugged = (intent.getIntExtra(EXTRA_STATE, 0) == 1);
        if (isPlugged) {
            showNotification(context);
        } else {
            hideNotification(context);
        }
    }


    private void showNotification(Context context) {
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


    private void hideNotification(Context context) {
        getManager(context).cancel(NOTIFICATION_ID);
    }


    private NotificationManager getManager(Context context) {
        return (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
    }


    private Intent get3rdPartyIntent(Context context, String pkg) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(pkg);
        if (intent == null) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + pkg));
        }
        return intent;
    }


    private PendingIntent getPendingIntent(Context context, Intent intent) {
        return PendingIntent.getActivity(context, 0, intent, 0);
    }
}
