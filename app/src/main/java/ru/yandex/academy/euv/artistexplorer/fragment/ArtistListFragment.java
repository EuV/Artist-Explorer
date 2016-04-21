package ru.yandex.academy.euv.artistexplorer.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import ru.yandex.academy.euv.artistexplorer.App;
import ru.yandex.academy.euv.artistexplorer.JSONLoader;
import ru.yandex.academy.euv.artistexplorer.R;

public class ArtistListFragment extends Fragment implements JSONLoader.LoaderCallback {
    private static final String KEY_ARTIST_LIST = "key_artist_list";

    private OnArtistSelectedListener host;
    private ArrayList<String> artistList;

    public interface OnArtistSelectedListener {
        void onArtistSelected(@NonNull String artistName);
    }


    public static ArtistListFragment newInstance() {
        return new ArtistListFragment();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        host = (OnArtistSelectedListener) context;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            artistList = savedInstanceState.getStringArrayList(KEY_ARTIST_LIST);
        }

        View rootView = inflater.inflate(R.layout.fragment_artist_list, container, false);

        if (artistList == null) {
            JSONLoader.loadArtistList(this);
        } else {
            ((TextView) rootView.findViewById(R.id.text_tmp)).setText(artistList.get(0));
        }

        rootView.findViewById(R.id.button_select_artist).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                host.onArtistSelected("Бременские музыканты");
            }
        });

        return rootView;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putStringArrayList(KEY_ARTIST_LIST, artistList);
    }


    @Override
    public void onArtistListLoaded(@NonNull final ArrayList<String> artistList) {
        App.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ArtistListFragment.this.artistList = artistList;
                ((TextView) getView().findViewById(R.id.text_tmp)).setText(artistList.get(0));
            }
        });
    }


    @Override
    public void failedToDownloadData() {

    }


    @Override
    public void failedToParseData() {

    }
}
