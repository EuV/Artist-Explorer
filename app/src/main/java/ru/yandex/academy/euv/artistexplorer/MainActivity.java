package ru.yandex.academy.euv.artistexplorer;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;

import ru.yandex.academy.euv.artistexplorer.fragment.ArtistDetailFragment;
import ru.yandex.academy.euv.artistexplorer.fragment.ArtistListFragment;
import ru.yandex.academy.euv.artistexplorer.fragment.ArtistListFragment.OnArtistSelectedListener;

public class MainActivity extends AppCompatActivity implements OnArtistSelectedListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpToolbar();

        if (savedInstanceState != null) {
            return;
        }

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, ArtistListFragment.newInstance())
                .commit();
    }


    /**
     * Sets up support toolbar itself, its callback and 'UP' button behavior
     */
    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        showUpButtonIfNeeded();

        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        getSupportFragmentManager().addOnBackStackChangedListener(new OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                showUpButtonIfNeeded();
            }
        });
    }


    /**
     * Shows or hides toolbar's 'UP' button when there is something on the back stack.
     * In current implementation, the button is shown when artist details are on the screen
     * and is hidden when list of artists is displayed.
     */
    private void showUpButtonIfNeeded() {
        boolean showUpButton = (getSupportFragmentManager().getBackStackEntryCount() != 0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(showUpButton);
    }


    @Override
    public void onArtistSelected(@NonNull String artistName) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, ArtistDetailFragment.newInstance(artistName))
                .addToBackStack(null)
                .commit();
    }
}
