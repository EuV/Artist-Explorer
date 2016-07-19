package ru.yandex.academy.euv.artistexplorer.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import ru.yandex.academy.euv.artistexplorer.receiver.HeadsetBroadcastReceiver;

public class ReceiverService extends Service {

    HeadsetBroadcastReceiver receiver = new HeadsetBroadcastReceiver();

    @Override
    public void onCreate() {
        registerReceiver(receiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
    }
}
