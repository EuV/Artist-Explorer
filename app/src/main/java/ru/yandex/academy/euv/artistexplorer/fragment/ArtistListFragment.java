package ru.yandex.academy.euv.artistexplorer.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

import ru.yandex.academy.euv.artistexplorer.Artist;
import ru.yandex.academy.euv.artistexplorer.ArtistListLoader;
import ru.yandex.academy.euv.artistexplorer.ArtistListLoader.RootCause;
import ru.yandex.academy.euv.artistexplorer.ArtistListLoader.LoaderCallback;
import ru.yandex.academy.euv.artistexplorer.R;
import ru.yandex.academy.euv.artistexplorer.util.I18n;

/**
 * Fragment represents 'first' application screen with a list of artists.
 * Receives data via {@link ArtistListLoader}; displays them using {@link RecyclerView}.
 * Looks the same in both landscape and portrait screen orientation.
 */
public class ArtistListFragment extends Fragment implements LoaderCallback, OnRefreshListener {
    private static final String KEY_ARTIST_LIST = "key_artist_list";

    private OnArtistSelectedListener host;
    private ArrayList<Artist> artistList;

    private RecyclerView artistRecyclerView;
    private ArtistAdapter artistAdapter;
    private ProgressBar progressBar;
    private SwipeRefreshLayout refresher;


    /**
     * Should be implemented by host activity to be notified
     * when user selects a certain artist.
     */
    public interface OnArtistSelectedListener {
        void onArtistSelected(@NonNull Artist artist);
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

        artistRecyclerView = (RecyclerView) rootView.findViewById(R.id.artist_recycler_view);
        artistRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        artistRecyclerView.setAdapter(artistAdapter = new ArtistAdapter());

        // Thus disabling toolbar expansion when artist list is scrolled
        ViewCompat.setNestedScrollingEnabled(artistRecyclerView, false);

        progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);

        refresher = (SwipeRefreshLayout) rootView.findViewById(R.id.artist_list_refresher);
        refresher.setOnRefreshListener(this);
        refresher.setColorSchemeResources(R.color.accent, R.color.primary);

        return rootView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            artistList = savedInstanceState.getParcelableArrayList(KEY_ARTIST_LIST);
        }

        if (artistList == null) {
            loadArtistList(false);
        } else {
            onArtistListLoaded(artistList);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(KEY_ARTIST_LIST, artistList);
    }


    /**
     * Callback for {@link SwipeRefreshLayout}, called when swiping down
     * at the top of the artist list.
     */
    @Override
    public void onRefresh() {
        loadArtistList(true);
    }


    /**
     * Callback for {@link ArtistListLoader}, called when a list of artists
     * has been loaded successfully. May also be called directly
     * from the fragment itself in case of its recreation.
     * Performs UI updates and stores received data.
     */
    @Override
    public void onArtistListLoaded(@NonNull final ArrayList<Artist> artistList) {
        if (!isAdded()) return;
        this.artistList = artistList;
        artistAdapter.setArtistList(artistList);
        progressBar.setVisibility(View.GONE);
        refresher.setVisibility(View.VISIBLE);
        refresher.setRefreshing(false);
    }


    /**
     * Callback for {@link ArtistListLoader}, called when a list of artists
     * hasn't been loaded due to error occurred. Shows message is appropriate
     * for the given {@link RootCause} in a {@link Snackbar}.
     */
    @Override
    public void failedToLoadData(@NonNull RootCause rootCause) {
        if (!isAdded()) return;

        refresher.setRefreshing(false);

        int resId;
        switch (rootCause) {
            case NO_CONNECTION:
                resId = R.string.error_no_network_connection;
                break;
            case IO_ERROR:
                resId = R.string.error_failed_to_load_data;
                break;
            case PARSING_ERROR:
                resId = R.string.error_failed_to_parse_data;
                break;
            default:
                resId = R.string.error_unknown;
                break;
        }

        Snackbar snackbar = Snackbar.make(getView(), resId, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.label_retry, new OnClickListener() {
            @Override
            public void onClick(View v) {
                loadArtistList(true);
            }
        });
        snackbar.show();
    }


    /**
     * Initiates loading of artist list and sets refreshing animation.
     * This animation won't be visible when fragment has just been created and
     * fetches the data: an entire list view is hidden and a progress bar is shown.
     *
     * @param forced if true, the loader will be forced to load data from the web,
     *               even if it was successfully downloaded and cached previously.
     */
    private void loadArtistList(boolean forced) {
        ArtistListLoader.getInstance().load(this, forced);
        refresher.setRefreshing(true);
    }


    /**
     * Data adapter for the {@link RecyclerView}. Nothing unusual except that
     * covers are loaded asynchronously using Fresco library developed by Facebook.
     */
    private class ArtistAdapter extends RecyclerView.Adapter<ArtistViewHolder> implements OnClickListener {
        private ArrayList<Artist> artistList = new ArrayList<>();

        public void setArtistList(@NonNull ArrayList<Artist> artistList) {
            this.artistList = artistList;
            notifyDataSetChanged();
        }


        @Override
        public ArtistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View artistView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_artist_list_item, parent, false);
            artistView.setOnClickListener(this);
            return new ArtistViewHolder(artistView);
        }


        @Override
        public void onBindViewHolder(ArtistViewHolder holder, int position) {
            Artist artist = artistList.get(position);

            holder.cover.setImageURI(Uri.parse(artist.getCover().getSmall()));
            holder.name.setText(artist.getName());
            holder.genres.setText(TextUtils.join(", ", artist.getGenres()));

            String albums = I18n.albums(artist.getAlbums());
            String tracks = I18n.tracks(artist.getTracks());
            holder.albumsAndTracks.setText(getString(R.string.sep_by_comma, albums, tracks));
        }


        @Override
        public int getItemCount() {
            return artistList.size();
        }


        @Override
        public void onClick(View artistView) {
            int index = artistRecyclerView.getChildAdapterPosition(artistView);
            host.onArtistSelected(artistList.get(index));
        }
    }


    private class ArtistViewHolder extends RecyclerView.ViewHolder {
        final SimpleDraweeView cover;
        final TextView name;
        final TextView genres;
        final TextView albumsAndTracks;

        public ArtistViewHolder(View artistView) {
            super(artistView);
            cover = (SimpleDraweeView) artistView.findViewById(R.id.img_artist_cover);
            name = (TextView) artistView.findViewById(R.id.text_artist_name);
            genres = (TextView) artistView.findViewById(R.id.text_artist_genres);
            albumsAndTracks = (TextView) artistView.findViewById(R.id.text_artist_albums_and_tracks);
        }
    }
}
