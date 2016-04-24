package ru.yandex.academy.euv.artistexplorer;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.AppBarLayout.Behavior;
import android.support.design.widget.AppBarLayout.Behavior.DragCallback;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout.LayoutParams;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;

import ru.yandex.academy.euv.artistexplorer.fragment.ArtistDetailsFragment;
import ru.yandex.academy.euv.artistexplorer.fragment.ArtistListFragment;
import ru.yandex.academy.euv.artistexplorer.fragment.ArtistListFragment.OnArtistSelectedListener;
import ru.yandex.academy.euv.artistexplorer.view.SquareDraweeView;

import static android.R.anim.fade_in;
import static android.R.anim.fade_out;

/**
 * Single activity application. Displays list of artists and artist details in
 * {@link ArtistListFragment} and {@link ArtistDetailsFragment} accordingly.
 * Uses support action bar collapsible in portrait mode.
 */
public class MainActivity extends AppCompatActivity implements OnArtistSelectedListener {
    private static final String KEY_LAST_VIEWED_ARTIST = "key_last_viewed_artist";

    /**
     * Used to set artist name and cover into the toolbar in case of activity recreation.
     */
    private Artist lastViewedArtist;

    /**
     * Indicates which fragment is currently visible.
     */
    private boolean artistDetailsVisible;

    /**
     * Responsible for status bar color and toolbar title.
     */
    private CollapsingToolbarLayout collapsingToolbar;

    /**
     * Responsible for toolbar expanding and collapsing.
     */
    private AppBarLayout appBarLayout;

    /**
     * Covers placed in a toolbar.
     * Visible with artist details in portrait orientation.
     */
    private SquareDraweeView toolbarCoverSmall;
    private SquareDraweeView toolbarCoverBig;


    /**
     * When creating the first time, displays {@link ArtistListFragment}.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpToolbar(savedInstanceState);

        if (savedInstanceState != null) {
            return;
        }

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, ArtistListFragment.newInstance())
                .commit();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_LAST_VIEWED_ARTIST, lastViewedArtist);
    }


    /**
     * Sets up all the things related to the collapsible support action bar.
     */
    @SuppressWarnings("deprecation")
    private void setUpToolbar(@Nullable Bundle savedInstanceState) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        if (savedInstanceState != null) {
            lastViewedArtist = savedInstanceState.getParcelable(KEY_LAST_VIEWED_ARTIST);
        }

        toolbarCoverSmall = (SquareDraweeView) findViewById(R.id.img_toolbar_cover_small);
        toolbarCoverBig = (SquareDraweeView) findViewById(R.id.img_toolbar_cover_big);

        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);

        // Some tuning required only in portrait orientation
        if (collapsingToolbar != null) {
            collapsingToolbar.setStatusBarScrimColor(getResources().getColor(R.color.black_12));

            // Expand/collapse toolbar via dragging only when artist details are shown
            Behavior behavior = new Behavior();
            behavior.setDragCallback(new DragCallback() {
                @Override
                public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                    return artistDetailsVisible;
                }
            });

            appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
            LayoutParams params = (LayoutParams) appBarLayout.getLayoutParams();
            params.setBehavior(behavior);
        }

        // Callback of the 'back arrow' button in the toolbar
        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        getSupportFragmentManager().addOnBackStackChangedListener(new OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                syncToolbarState();
            }
        });

        syncToolbarState();
    }


    /**
     * Synchronizes toolbar visible state with fragment is shown.
     * Called every time fragment back stack is changed.
     */
    private void syncToolbarState() {
        artistDetailsVisible = (getSupportFragmentManager().getBackStackEntryCount() != 0);

        // Shows toolbar's 'UP' button when artist details are visible
        getSupportActionBar().setDisplayHomeAsUpEnabled(artistDetailsVisible);

        String title = artistDetailsVisible ? lastViewedArtist.getName() : getString(R.string.label_artists);

        // In landscape orientation, just set the title and return
        if (collapsingToolbar == null) {
            getSupportActionBar().setTitle(title);
            return;
        }

        collapsingToolbar.setTitle(title);

        if (artistDetailsVisible) {
            toolbarCoverSmall.setImageURI(Uri.parse(lastViewedArtist.getCover().getSmall()));
            toolbarCoverBig.setImageURI(Uri.parse(lastViewedArtist.getCover().getBig()));
        }

        // Expand toolbar with artist's cover if needed
        appBarLayout.setExpanded(artistDetailsVisible);
    }


    /**
     * Callback of the {@link ArtistListFragment}.
     * If called, it's time to show some artist details in {@link ArtistDetailsFragment}.
     */
    @Override
    public void onArtistSelected(@NonNull Artist artist) {

        // Save the artist before placing a fragment since fragment manipulations
        // will trigger syncToolbarState() which uses artist's name and cover
        lastViewedArtist = artist;

        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(fade_in, fade_out, fade_in, fade_out)
                .replace(R.id.fragment_container, ArtistDetailsFragment.newInstance(artist))
                .addToBackStack(null)
                .commit();
    }
}
