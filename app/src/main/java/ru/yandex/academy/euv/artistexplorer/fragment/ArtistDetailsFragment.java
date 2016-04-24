package ru.yandex.academy.euv.artistexplorer.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.yandex.academy.euv.artistexplorer.Artist;
import ru.yandex.academy.euv.artistexplorer.R;
import ru.yandex.academy.euv.artistexplorer.util.I18n;
import ru.yandex.academy.euv.artistexplorer.view.SquareDraweeView;

public class ArtistDetailsFragment extends Fragment {
    private static final String KEY_ARTIST_NAME = "key_artist_name";

    private Artist artist;

    public static ArtistDetailsFragment newInstance(@NonNull Artist artist) {
        Bundle args = new Bundle();
        args.putParcelable(KEY_ARTIST_NAME, artist);
        ArtistDetailsFragment fragment = new ArtistDetailsFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_artist_details, container, false);

        // Only in landscape orientation
        if (rootView.findViewById(R.id.landscape_cover) != null) {

            // Small cover has already been cashed
            SquareDraweeView coverSmall = (SquareDraweeView) rootView.findViewById(R.id.img_artist_cover_small);
            coverSmall.setImageURI(Uri.parse(artist.getCover().getSmall()));

            // When loaded, a big cover will substitute a small cover
            SquareDraweeView coverBig = (SquareDraweeView) rootView.findViewById(R.id.img_artist_cover_big);
            coverBig.setImageURI(Uri.parse(artist.getCover().getBig()));
        }

        TextView genres = (TextView) rootView.findViewById(R.id.text_artist_genres);
        genres.setText(TextUtils.join(", ", artist.getGenres()));

        String albums = I18n.albums(artist.getAlbums());
        String tracks = I18n.tracks(artist.getTracks());
        TextView albumsAndTracks = (TextView) rootView.findViewById(R.id.text_artist_albums_and_tracks);
        albumsAndTracks.setText(getString(R.string.sep_by_middot, albums, tracks));

        TextView description = (TextView) rootView.findViewById(R.id.text_artist_description);
        description.setText(artist.getDescription());

        return rootView;
    }
}
