package ru.yandex.academy.euv.artistexplorer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ru.yandex.academy.euv.artistexplorer.util.Notifications;

public class HeadsetBroadcastReceiver extends BroadcastReceiver {
    private static final String EXTRA_STATE = "state";

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isPlugged = (intent.getIntExtra(EXTRA_STATE, 0) == 1);
        if (isPlugged) {
            Notifications.show(context);
        } else {
            Notifications.hide(context);
        }
    }
}
