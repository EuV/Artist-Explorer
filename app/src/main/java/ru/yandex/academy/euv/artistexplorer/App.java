package ru.yandex.academy.euv.artistexplorer;

import android.app.Application;
import android.os.Handler;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.squareup.okhttp.OkHttpClient;

public class App extends Application {
    private static Thread uiThread;
    private static Handler handler;

    @Override
    public void onCreate() {
        super.onCreate();

        uiThread = Thread.currentThread();
        handler = new Handler(getApplicationContext().getMainLooper());

        ImagePipelineConfig config = OkHttpImagePipelineConfigFactory
                .newBuilder(this, new OkHttpClient())
                .build();
        Fresco.initialize(this, config);
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
}
