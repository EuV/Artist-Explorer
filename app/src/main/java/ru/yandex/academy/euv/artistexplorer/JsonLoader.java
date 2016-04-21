package ru.yandex.academy.euv.artistexplorer;

import android.support.annotation.NonNull;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Facade for manipulating with JSON data in the application.
 * Allows to download raw data from the server, cache and parse it.
 * Because of small size of the project (and YAGNI principle)
 * all these things are encapsulated in this single class.
 */
public final class JSONLoader {
    private static final String ARTISTS_JSON_URL = "http://cache-spb02.cdn.yandex.net/download.cdn.yandex.net/mobilization-2016/artists.json";
    private static final OkHttpClient okHttpClient = new OkHttpClient();

    /**
     * Used for asynchronous loading data from the server;
     * separates app logic and presentation level.
     * All types of errors are split into two groups for simplicity.
     */
    public interface LoaderCallback {
        void onArtistListLoaded(@NonNull ArrayList<String> artistList);
        void failedToDownloadData();
        void failedToParseData();
    }

    private JSONLoader() { /* */ }


    public static void loadArtistList(final LoaderCallback callback) {
        Request request = new Request.Builder().url(ARTISTS_JSON_URL).build();
        okHttpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Request request, IOException e) {
                callback.failedToDownloadData();
            }

            @Override
            public void onResponse(Response response) {
                if (response.isSuccessful()) {
                    try {
                        ArrayList<String> artistList = new ArrayList<>();
                        artistList.add(response.body().string());
                        callback.onArtistListLoaded(artistList);
                    } catch (IOException e) {
                        callback.failedToParseData();
                    }
                } else {
                    callback.failedToDownloadData();
                }
            }
        });
    }
}
