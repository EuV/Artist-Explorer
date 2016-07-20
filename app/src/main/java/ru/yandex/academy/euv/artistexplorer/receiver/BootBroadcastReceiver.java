package ru.yandex.academy.euv.artistexplorer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ru.yandex.academy.euv.artistexplorer.util.Notifications;

public class BootBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Notifications.syncState(context);
    }
}