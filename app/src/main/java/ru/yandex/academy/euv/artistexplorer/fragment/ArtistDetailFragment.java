package ru.yandex.academy.euv.artistexplorer.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.yandex.academy.euv.artistexplorer.R;

public class ArtistDetailFragment extends Fragment {
    private static final String KEY_ARTIST_NAME = "key_artist_name";

    private String artistName;

    public static ArtistDetailFragment newInstance(@NonNull String artistName) {
        Bundle args = new Bundle();
        args.putString(KEY_ARTIST_NAME, artistName);
        ArtistDetailFragment fragment = new ArtistDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            artistName = getArguments().getString(KEY_ARTIST_NAME);
        } else {
            artistName = savedInstanceState.getString(KEY_ARTIST_NAME);
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artist_detail, container, false);
        ((TextView) view.findViewById(R.id.text_artist_name)).setText(artistName);
        return view;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(KEY_ARTIST_NAME, artistName);
    }
}
