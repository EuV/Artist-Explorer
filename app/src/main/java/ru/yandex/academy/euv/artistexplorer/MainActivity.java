package ru.yandex.academy.euv.artistexplorer;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;

import ru.yandex.academy.euv.artistexplorer.fragment.ArtistDetailFragment;
import ru.yandex.academy.euv.artistexplorer.fragment.ArtistListFragment;
import ru.yandex.academy.euv.artistexplorer.fragment.ArtistListFragment.OnArtistSelectedListener;

public class MainActivity extends AppCompatActivity implements OnArtistSelectedListener {
    private static final String KEY_LAST_VIEWED_ARTIST_NAME = "last_viewed_artist_name";

    /**
     * Used to set artist name into the toolbar in case of activity recreation.
     * Another approach is to set title from fragment with artist details itself
     * using callback in onAttach(), but this one is much more simpler.
     */
    private String lastViewedArtistName;


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
        outState.putString(KEY_LAST_VIEWED_ARTIST_NAME, lastViewedArtistName);
    }


    /**
     * Sets up support toolbar itself, its callback and 'UP' button behavior
     */
    private void setUpToolbar(@Nullable Bundle savedInstanceState) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        if (savedInstanceState != null) {
            lastViewedArtistName = savedInstanceState.getString(KEY_LAST_VIEWED_ARTIST_NAME);
        }

        syncToolbarState();

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
    }


    /**
     * Shows or hides toolbar's 'UP' button when there is something on the back stack.
     * In current implementation, the button is shown when artist details are on the screen
     * and is hidden when list of artists is displayed.
     * Also, manages toolbar title: sets it to artist name or resets to default value.
     */
    private void syncToolbarState() {
        boolean artistDetailsVisible = (getSupportFragmentManager().getBackStackEntryCount() != 0);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(artistDetailsVisible ? lastViewedArtistName : getString(R.string.label_artists));
        actionBar.setDisplayHomeAsUpEnabled(artistDetailsVisible);
    }


    @Override
    public void onArtistSelected(@NonNull Artist artist) {

        // Save artist name before placing fragment since fragment manipulations
        // will trigger syncToolbarState() which uses this name
        lastViewedArtistName = artist.getName();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, ArtistDetailFragment.newInstance(artist))
                .addToBackStack(null)
                .commit();
    }
}
