package ru.yandex.academy.euv.artistexplorer.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.yandex.academy.euv.artistexplorer.Artist;
import ru.yandex.academy.euv.artistexplorer.R;

public class ArtistDetailFragment extends Fragment {
    private static final String KEY_ARTIST_NAME = "key_artist_name";

    private Artist artist;

    public static ArtistDetailFragment newInstance(@NonNull Artist artist) {
        Bundle args = new Bundle();
        args.putParcelable(KEY_ARTIST_NAME, artist);
        ArtistDetailFragment fragment = new ArtistDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        artist = getArguments().getParcelable(KEY_ARTIST_NAME);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artist_detail, container, false);
        ((TextView) view.findViewById(R.id.text_artist_name)).setText(artist.getName());
        return view;
    }
}
