package ru.yandex.academy.euv.artistexplorer;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.squareup.okhttp.OkHttpClient;

import ru.yandex.academy.euv.artistexplorer.service.ReceiverService;

/**
 * Extends default {@link Application} implementation providing some functionality
 * available throughout the app. Also used to initialize Fresco library.
 */
public class App extends Application {
    private static Thread uiThread;
    private static Context context;
    private static Handler handler;

    @Override
    public void onCreate() {
        super.onCreate();

        uiThread = Thread.currentThread();
        context = getApplicationContext();
        handler = new Handler(context.getMainLooper());

        startReceiverService(context);

        ImagePipelineConfig config = OkHttpImagePipelineConfigFactory
                .newBuilder(this, new OkHttpClient())
                .build();
        Fresco.initialize(this, config);
    }


    /**
     * Convenient method to get the application context at any place in the app.
     * Use with caution.
     */
    public static Context getContext() {
        return context;
    }


    /**
     * Convenient method to do some work related to UI at any place in the app.
     */
    public static void runOnUiThread(Runnable action) {
        if (Thread.currentThread() == uiThread) {
            action.run();
        } else {
            handler.post(action);
        }
    }


    public static void startReceiverService(Context context) {
        context.startService(new Intent(context, ReceiverService.class));
    }
}
