package ru.yandex.academy.euv.artistexplorer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Facade for manipulating with JSON data in the application.
 * Allows to download raw data from the server, cache and parse it.
 * Because of small size of the project (and YAGNI principle)
 * all these things are encapsulated in this single class.
 */
public final class JsonLoader {
    private static final String TAG = JsonLoader.class.getSimpleName();
    private static final String FILENAME = "artists.json";
    private static final String ARTISTS_JSON_URL = "http://cache-spb02.cdn.yandex.net/download.cdn.yandex.net/mobilization-2016/artists.json";
    private static final OkHttpClient okHttpClient = new OkHttpClient();

    /**
     * Used for asynchronous loading data from the server;
     * separates app logic and presentation level.
     * All types of errors are split into two groups for simplicity.
     */
    public interface LoaderCallback {
        void onArtistListLoaded(@NonNull ArrayList<Artist> artistList);
        void failedToDownloadData();
        void failedToParseData();
    }

    private JsonLoader() { /* */ }


    /**
     * TODO
     * May return data almost immediate (e.g. from cache), so must be called
     * when a target view has been prepared (i.e. in current implementation
     * should be invoked after onCreateView() returns the root view).
     */
    public static void loadArtistList(final LoaderCallback callback) {
        String rawData = readFromDisk();
        if (rawData != null) {
            ArrayList<Artist> artistList = (ArrayList<Artist>) JSON.parseArray(rawData, Artist.class);
            if (artistList == null) {
                artistList = new ArrayList<>();
            }
            callback.onArtistListLoaded(artistList);
            return;
        }

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
                        String rawData = response.body().string();

                        saveToDisk(rawData);

                        ArrayList<Artist> artistList = (ArrayList<Artist>) JSON.parseArray(rawData, Artist.class);
                        if (artistList == null) {
                            artistList = new ArrayList<>();
                        }

                        final ArrayList<Artist> finalArtistList = artistList;
                        App.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                callback.onArtistListLoaded(finalArtistList);
                            }
                        });
                    } catch (JSONException | IOException e) {
                        callback.failedToParseData();
                    }
                } else {
                    callback.failedToDownloadData();
                }
            }
        });
    }


    private static void saveToDisk(String rawData) {
        try {
            FileOutputStream fos = App.getContext().openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(rawData.getBytes());
            fos.close();
        } catch (IOException e) {
            Log.e(TAG, "Failed to save server response to internal storage", e);
        }
    }


    @Nullable
    private static String readFromDisk() {
        String rawData = null;

        try {
            FileInputStream fis = App.getContext().openFileInput(FILENAME);

            // Read the whole file
            Scanner scanner = new Scanner(fis).useDelimiter("\\A");
            if (scanner.hasNext()) {
                rawData = scanner.next();
            }

            fis.close();
        } catch (FileNotFoundException e) {
            // Just move on
        } catch (IOException e) {
            Log.e(TAG, "Failed to read data from internal storage", e);
        }

        return rawData;
    }
}
