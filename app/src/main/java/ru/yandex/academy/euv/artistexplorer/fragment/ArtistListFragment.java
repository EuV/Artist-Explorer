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
import ru.yandex.academy.euv.artistexplorer.Artist;
import ru.yandex.academy.euv.artistexplorer.JsonLoader;
import ru.yandex.academy.euv.artistexplorer.JsonLoader.LoaderCallback;
import ru.yandex.academy.euv.artistexplorer.R;

public class ArtistListFragment extends Fragment implements LoaderCallback {
    private static final String KEY_ARTIST_LIST = "key_artist_list";

    private OnArtistSelectedListener host;
    private ArrayList<Artist> artistList;

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
        View rootView = inflater.inflate(R.layout.fragment_artist_list, container, false);

        rootView.findViewById(R.id.button_select_artist).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                host.onArtistSelected("Бременские музыканты");
            }
        });

        return rootView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            artistList = savedInstanceState.getParcelableArrayList(KEY_ARTIST_LIST);
        }

        if (artistList == null) {
            JsonLoader.loadArtistList(this);
        } else {
            ((TextView) getView().findViewById(R.id.text_tmp)).setText(artistList.get(0).getName());
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(KEY_ARTIST_LIST, artistList);
    }


    @Override
    public void onArtistListLoaded(@NonNull final ArrayList<Artist> artistList) {
        App.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ArtistListFragment.this.artistList = artistList;
                ((TextView) getView().findViewById(R.id.text_tmp)).setText(artistList.get(0).getName());
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
