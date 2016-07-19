package ru.yandex.academy.euv.artistexplorer;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.AppBarLayout.Behavior;
import android.support.design.widget.AppBarLayout.Behavior.DragCallback;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout.LayoutParams;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import ru.yandex.academy.euv.artistexplorer.fragment.AboutFragment;
import ru.yandex.academy.euv.artistexplorer.fragment.ArtistDetailsFragment;
import ru.yandex.academy.euv.artistexplorer.fragment.ArtistListFragment;
import ru.yandex.academy.euv.artistexplorer.fragment.ArtistListFragment.OnArtistSelectedListener;
import ru.yandex.academy.euv.artistexplorer.view.SquareDraweeView;

import static android.R.anim.fade_in;
import static android.R.anim.fade_out;
import static ru.yandex.academy.euv.artistexplorer.MainActivity.VisibleFragment.ABOUT;
import static ru.yandex.academy.euv.artistexplorer.MainActivity.VisibleFragment.ARTIST_DETAILS;
import static ru.yandex.academy.euv.artistexplorer.MainActivity.VisibleFragment.ARTIST_LIST;

/**
 * Single activity application. Displays list of artists and artist details in
 * {@link ArtistListFragment} and {@link ArtistDetailsFragment} accordingly.
 * Uses support action bar collapsible in portrait mode.
 */
@SuppressWarnings("ConstantConditions")
public class MainActivity extends AppCompatActivity implements OnArtistSelectedListener {
    private static final String KEY_LAST_VIEWED_ARTIST = "key_last_viewed_artist";
    private static final String FEEDBACK_EMAIL = "vasil.ev.genij@gmail.com";
    private static final String FEEDBACK_SUBJECT = "Artist Explorer app";

    /**
     * Used to set artist name and cover into the toolbar in case of activity recreation.
     */
    private Artist lastViewedArtist;

    /**
     * Indicates which fragment is currently visible.
     */
    private VisibleFragment visibleFragment;

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                if (visibleFragment != ABOUT) {
                    replaceFragment(AboutFragment.newInstance());
                }
                return true;
            case R.id.feedback:
                sendFeedback();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
                    return visibleFragment == ARTIST_DETAILS;
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
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        Class<?> fragmentClass = (fragment == null) ? ArtistListFragment.class : fragment.getClass();

        String title;

        if (fragmentClass == ArtistListFragment.class) {
            visibleFragment = ARTIST_LIST;
            title = getString(R.string.label_artists);
        } else if (fragmentClass == ArtistDetailsFragment.class) {
            visibleFragment = ARTIST_DETAILS;
            title = lastViewedArtist.getName();
        } else {
            visibleFragment = ABOUT;
            title = getString(R.string.label_about);
        }

        // Shows toolbar's 'UP' button when 'artist details' or 'about' pages are visible
        getSupportActionBar().setDisplayHomeAsUpEnabled(visibleFragment != ARTIST_LIST);

        // In landscape orientation, just set the title and return
        if (collapsingToolbar == null) {
            getSupportActionBar().setTitle(title);
            return;
        }

        collapsingToolbar.setTitle(title);

        if (visibleFragment == ARTIST_DETAILS) {
            toolbarCoverSmall.setImageURI(Uri.parse(lastViewedArtist.getCover().getSmall()));
            toolbarCoverBig.setImageURI(Uri.parse(lastViewedArtist.getCover().getBig()));
        }

        // Expand toolbar with artist's cover if needed
        appBarLayout.setExpanded(visibleFragment == ARTIST_DETAILS);
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

        replaceFragment(ArtistDetailsFragment.newInstance(artist));
    }


    private void replaceFragment(@NonNull Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(fade_in, fade_out, fade_in, fade_out)
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }


    public void sendFeedback() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{FEEDBACK_EMAIL});
        intent.putExtra(Intent.EXTRA_SUBJECT, FEEDBACK_SUBJECT);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }


    enum VisibleFragment {
        ARTIST_LIST,
        ARTIST_DETAILS,
        ABOUT
    }
}
