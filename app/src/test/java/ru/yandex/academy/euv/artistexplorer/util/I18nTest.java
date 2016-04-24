package ru.yandex.academy.euv.artistexplorer.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import ru.yandex.academy.euv.artistexplorer.App;
import ru.yandex.academy.euv.artistexplorer.R;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * A bit awful example of Unit testing, but half a loaf is better than none.
 * Checks that the application chooses right string endings depending
 * on the last digit of a given number.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(App.class)
public class I18nTest {
    private static final String RUS_ITEMS_FORMAT_1 = "%d штука";
    private static final String RUS_ITEMS_FORMAT_2 = "%d штуки";
    private static final String RUS_ITEMS_FORMAT_3 = "%d штук";

    private static final Map<Integer, String> itemsNumberAsString = new HashMap<>();

    static {
        itemsNumberAsString.put(0, "0 штук");
        itemsNumberAsString.put(1, "1 штука");
        itemsNumberAsString.put(15, "15 штук");
        itemsNumberAsString.put(21, "21 штука");
        itemsNumberAsString.put(24, "24 штуки");
        itemsNumberAsString.put(26, "26 штук");
    }

    @Mock
    Context mockContext;


    @Test
    @SuppressWarnings("all")
    public void testAlbums() throws Exception {
        PowerMockito.mockStatic(App.class);
        BDDMockito.given(App.getContext()).willReturn(mockContext);

        when(mockContext.getString(R.string.format_albums_zero, 0)).thenReturn(format(RUS_ITEMS_FORMAT_3, 0));
        when(mockContext.getString(R.string.format_albums_one, 1)).thenReturn(format(RUS_ITEMS_FORMAT_1, 1));
        when(mockContext.getString(R.string.format_albums_ten_twenty, 15)).thenReturn(format(RUS_ITEMS_FORMAT_3, 15));
        when(mockContext.getString(R.string.format_albums_X1, 21)).thenReturn(format(RUS_ITEMS_FORMAT_1, 21));
        when(mockContext.getString(R.string.format_albums_X2_4, 24)).thenReturn(format(RUS_ITEMS_FORMAT_2, 24));
        when(mockContext.getString(R.string.format_albums_X5_0, 26)).thenReturn(format(RUS_ITEMS_FORMAT_3, 26));

        for (Entry<Integer, String> item : itemsNumberAsString.entrySet()) {
            assertEquals(item.getValue(), I18n.albums(item.getKey()));
        }
    }


    @Test
    @SuppressWarnings("all")
    public void testTracks() throws Exception {
        PowerMockito.mockStatic(App.class);
        BDDMockito.given(App.getContext()).willReturn(mockContext);

        when(mockContext.getString(R.string.format_tracks_zero, 0)).thenReturn(format(RUS_ITEMS_FORMAT_3, 0));
        when(mockContext.getString(R.string.format_tracks_one, 1)).thenReturn(format(RUS_ITEMS_FORMAT_1, 1));
        when(mockContext.getString(R.string.format_tracks_ten_twenty, 15)).thenReturn(format(RUS_ITEMS_FORMAT_3, 15));
        when(mockContext.getString(R.string.format_tracks_X1, 21)).thenReturn(format(RUS_ITEMS_FORMAT_1, 21));
        when(mockContext.getString(R.string.format_tracks_X2_4, 24)).thenReturn(format(RUS_ITEMS_FORMAT_2, 24));
        when(mockContext.getString(R.string.format_tracks_X5_0, 26)).thenReturn(format(RUS_ITEMS_FORMAT_3, 26));

        for (Entry<Integer, String> item : itemsNumberAsString.entrySet()) {
            assertEquals(item.getValue(), I18n.tracks(item.getKey()));
        }
    }
}
