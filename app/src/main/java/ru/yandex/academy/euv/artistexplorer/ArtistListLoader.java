package ru.yandex.academy.euv.artistexplorer;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static ru.yandex.academy.euv.artistexplorer.ArtistListLoader.State.*;

/**
 * Provides a list of Artists to the application.
 * <p/>
 * Loads data from the web or from cache and parses it.
 * Returns to the caller list of artists or error code if any.
 * <p/>
 * For ease of reading and better maintaining of data flow, the loader
 * is implemented as a singleton state machine running on its own thread.
 * <p/>
 * There are many ways to improve the loader, but for now
 * the current implementation is quite enough.
 */
public final class ArtistListLoader extends HandlerThread {
    private static final String TAG = ArtistListLoader.class.getSimpleName();
    private static final String FILENAME = "artists.json";
    private static final String ARTISTS_JSON_URL = "http://cache-spb02.cdn.yandex.net/download.cdn.yandex.net/mobilization-2016/artists.json";

    private static ArtistListLoader artistListLoaderInstance;

    private OkHttpClient okHttpClient = new OkHttpClient();
    private Handler loaderThreadHandler;
    private LoaderCallback callback;

    /**
     * List of artists as a JSON string.
     * Is an input/output value of the FSM states.
     */
    private String rawData;

    /**
     * Parsed list of artists.
     * Is an input/output value of the FSM states.
     */
    private ArrayList<Artist> artistList;

    /**
     * Root cause of an occurred error.
     * Is an input/output value of the FSM states.
     */
    private RootCause rootCause;


    /**
     * Should be implemented by the caller in order to receive the data loaded
     * or to be informed if any error has occurred.
     */
    public interface LoaderCallback {
        void onArtistListLoaded(@NonNull ArrayList<Artist> artistList);
        void failedToLoadData(@NonNull RootCause rootCause);
    }


    private ArtistListLoader() {
        super(TAG + "Thread");
        start();
        loaderThreadHandler = new Handler(getLooper());
    }


    public static ArtistListLoader getInstance() {
        if (artistListLoaderInstance == null) {
            artistListLoaderInstance = new ArtistListLoader();
        }
        return artistListLoaderInstance;
    }


    /**
     * Entry point. Is called on the UI thread.
     * The rest of the methods are invoked on a loader thread.
     * <p/>
     * May return data almost immediate (e.g. from cache), so must be called
     * after the target view has been prepared (i.e. in current implementation
     * should be invoked after onCreateView() returns the root view).
     */
    public void load(final LoaderCallback callback, final boolean forced) {
        loaderThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                loadInBackground(callback, forced);
            }
        });
    }


    /**
     * Finite-state machine. Loads list of artists from the web or from cache
     * or returns appropriate error code.
     *
     * @param callback for return of artist list or error code.
     * @param forced   load data from web ignoring cache.
     */
    private void loadInBackground(LoaderCallback callback, boolean forced) {
        Log.d(TAG, "loadInBackground()");

        this.callback = callback;

        State state = forced ? CHECK_CONNECTION : LOAD_FROM_DISK;

        while (state != STOP) {
            switch (state) {
                case CHECK_CONNECTION:
                    state = checkConnection() ? LOAD_FROM_WEB : FAILURE;
                    break;

                case LOAD_FROM_WEB:
                    state = loadFromWeb() ? SAVE_TO_DISK : FAILURE;
                    break;

                case SAVE_TO_DISK:
                    saveToDisk();
                    state = PARSE_JSON;
                    break;

                case LOAD_FROM_DISK:
                    state = loadFromDisk() ? PARSE_JSON : CHECK_CONNECTION;
                    break;

                case PARSE_JSON:
                    state = parseJson() ? SUCCESS : REMOVE_FROM_DISK;
                    break;

                case SUCCESS:
                    success();
                    state = STOP;
                    break;

                case REMOVE_FROM_DISK:
                    removeFromDisk();
                    state = FAILURE;
                    break;

                case FAILURE:
                    failure();
                    state = STOP;
                    break;
            }
        }
    }


    /**
     * Checks for Internet connection.
     *
     * @return true if there is connection, false otherwise.
     */
    private boolean checkConnection() {
        Log.d(TAG, "checkConnection()");

        ConnectivityManager manager = (ConnectivityManager) App.getContext().getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            return true;
        } else {
            rootCause = RootCause.NO_CONNECTION;
            return false;
        }
    }


    /**
     * Loads JSON string of artists from the web.
     *
     * @return true if loading was successful, false otherwise.
     */
    private boolean loadFromWeb() {
        Log.d(TAG, "loadFromWeb()");

        rawData = null;
        try {
            Request request = new Request.Builder().url(ARTISTS_JSON_URL).build();
            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                rawData = response.body().string();
            }
        } catch (IOException e) {
            Log.d(TAG, "Failed to receive data from the server", e);
        }

        if (rawData == null) {
            rootCause = RootCause.IO_ERROR;
            return false;
        } else {
            return true;
        }
    }


    /**
     * Caches list of artists (saves JSON string to internal storage).
     */
    private void saveToDisk() {
        Log.d(TAG, "saveToDisk()");

        try {
            FileOutputStream fos = App.getContext().openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(rawData.getBytes());
            fos.close();
        } catch (IOException e) {
            Log.d(TAG, "Failed to save server response to internal storage", e);
        }
    }


    /**
     * Loads list of artists from the cache (reads JSON string from internal storage).
     *
     * @return true if there is something in the cache, false otherwise.
     */
    private boolean loadFromDisk() {
        Log.d(TAG, "loadFromDisk()");

        rawData = null;
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
            Log.d(TAG, "Failed to read data from internal storage", e);
        }

        return rawData != null;
    }


    /**
     * Parses JSON string of artists into ArrayList.
     *
     * @return true if the string has been parsed successfully, false otherwise.
     */
    private boolean parseJson() {
        Log.d(TAG, "parseJson()");

        artistList = null;
        try {
            artistList = (ArrayList<Artist>) JSON.parseArray(rawData, Artist.class);
        } catch (JSONException e) {
            Log.d(TAG, "Failed to parse JSON string", e);
        }

        if (artistList == null) {
            rootCause = RootCause.PARSING_ERROR;
            return false;
        } else {
            return true;
        }
    }


    /**
     * Returns artist list to the caller.
     */
    private void success() {
        Log.d(TAG, "success()");

        // Copy list due to multithreaded access to it
        final ArrayList<Artist> finalArtistList = new ArrayList<>(artistList);

        App.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callback.onArtistListLoaded(finalArtistList);
            }
        });
    }


    /**
     * Clears 'broken' cache.
     */
    private void removeFromDisk() {
        Log.d(TAG, "removeFromDisk()");

        App.getContext().deleteFile(FILENAME);
    }


    /**
     * Returns error code to the caller.
     */
    private void failure() {
        Log.d(TAG, "failure()");

        // Don't shoot your leg
        final RootCause finalRootCause = rootCause;

        App.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callback.failedToLoadData(finalRootCause);
            }
        });
    }


    /**
     * FSM states.
     */
    enum State {
        CHECK_CONNECTION,
        LOAD_FROM_WEB,
        SAVE_TO_DISK,
        LOAD_FROM_DISK,
        PARSE_JSON,
        SUCCESS,
        REMOVE_FROM_DISK,
        FAILURE,
        STOP
    }


    /**
     * 'Codes' of possible errors.
     * Limited to three types for simplicity.
     */
    public enum RootCause {
        NO_CONNECTION,
        IO_ERROR,
        PARSING_ERROR
    }
}
