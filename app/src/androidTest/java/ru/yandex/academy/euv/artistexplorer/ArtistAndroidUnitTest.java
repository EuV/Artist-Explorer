package ru.yandex.academy.euv.artistexplorer;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * A Unit test requires some part of a real Android environment, thus running on a device.
 * Checks that {@link Artist} class has implemented {@link Parcelable} interface properly.
 */
@RunWith(AndroidJUnit4.class)
public class ArtistAndroidUnitTest {
    private static final int ID = 1;
    private static final String NAME = "Doge";
    private static final String GENRE_1 = "BARK";
    private static final String GENRE_2 = "HOWL";
    private static final int TRACKS = 100;
    private static final int ALBUMS = 500;
    private static final String LINK = "http://plspetdoge.com/";
    private static final String DESCRIPTION = "Such description!";
    private static final String COVER_SMALL = "https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_150x54dp.png";
    private static final String COVER_BIG = "https://yastatic.net/morda-logo/i/share-logo-ru.png";

    private Artist artist;


    @Before
    public void createArtist() {
        Artist.Cover cover = new Artist.Cover();
        cover.setSmall(COVER_SMALL);
        cover.setBig(COVER_BIG);

        artist = new Artist();
        artist.setId(ID);
        artist.setName(NAME);
        artist.setGenres(Arrays.asList(GENRE_1, GENRE_2));
        artist.setTracks(TRACKS);
        artist.setAlbums(ALBUMS);
        artist.setLink(LINK);
        artist.setDescription(DESCRIPTION);
        artist.setCover(cover);
    }


    @Test
    public void creationFromParcel() {
        Parcel parcel = Parcel.obtain();
        artist.writeToParcel(parcel, artist.describeContents());

        parcel.setDataPosition(0);

        Artist recreatedArtist = Artist.CREATOR.createFromParcel(parcel);

        assertThat(recreatedArtist.getId(), is(ID));
        assertThat(recreatedArtist.getName(), is(NAME));
        assertThat(recreatedArtist.getGenres().get(0), is(GENRE_1));
        assertThat(recreatedArtist.getGenres().get(1), is(GENRE_2));
        assertThat(recreatedArtist.getTracks(), is(TRACKS));
        assertThat(recreatedArtist.getAlbums(), is(ALBUMS));
        assertThat(recreatedArtist.getLink(), is(LINK));
        assertThat(recreatedArtist.getDescription(), is(DESCRIPTION));
        assertThat(recreatedArtist.getCover().getSmall(), is(COVER_SMALL));
        assertThat(recreatedArtist.getCover().getBig(), is(COVER_BIG));
    }
}
