package ru.yandex.academy.euv.artistexplorer;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ru.yandex.academy.euv.artistexplorer.fragment.ArtistDetailFragment;
import ru.yandex.academy.euv.artistexplorer.fragment.ArtistListFragment;
import ru.yandex.academy.euv.artistexplorer.fragment.ArtistListFragment.OnArtistSelectedListener;

public class MainActivity extends AppCompatActivity implements OnArtistSelectedListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            return;
        }

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, ArtistListFragment.newInstance())
                .commit();
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
