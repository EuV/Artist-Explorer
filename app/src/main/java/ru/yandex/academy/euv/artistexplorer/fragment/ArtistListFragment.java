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

import ru.yandex.academy.euv.artistexplorer.R;

public class ArtistListFragment extends Fragment {
    private OnArtistSelectedListener host;

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
        View view = inflater.inflate(R.layout.fragment_artist_list, container, false);

        view.findViewById(R.id.button_select_artist).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                host.onArtistSelected("Бременские музыканты");
            }
        });

        return view;
    }
}
