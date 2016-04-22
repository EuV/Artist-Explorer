package ru.yandex.academy.euv.artistexplorer.util;

import ru.yandex.academy.euv.artistexplorer.App;
import ru.yandex.academy.euv.artistexplorer.R;

/**
 * Utility class selects appropriate unit endings.
 */
public final class I18n {
    private I18n() { /* */ }

    public static String albums(int albums) {
        int strId;
        switch (getEndingType(albums)) {
            case ZERO:
                strId = R.string.format_albums_zero;
                break;
            case ONE:
                strId = R.string.format_albums_one;
                break;
            case TEN_TWENTY:
                strId = R.string.format_albums_ten_twenty;
                break;
            case X1:
                strId = R.string.format_albums_X1;
                break;
            case X2_4:
                strId = R.string.format_albums_X2_4;
                break;
            case X5_0:
            default:
                strId = R.string.format_albums_X5_0;
                break;
        }
        return App.getContext().getString(strId, albums);
    }


    public static String tracks(int tracks) {
        int strId;
        switch (getEndingType(tracks)) {
            case ZERO:
                strId = R.string.format_tracks_zero;
                break;
            case ONE:
                strId = R.string.format_tracks_one;
                break;
            case TEN_TWENTY:
                strId = R.string.format_tracks_ten_twenty;
                break;
            case X1:
                strId = R.string.format_tracks_X1;
                break;
            case X2_4:
                strId = R.string.format_tracks_X2_4;
                break;
            case X5_0:
            default:
                strId = R.string.format_tracks_X5_0;
                break;
        }
        return App.getContext().getString(strId, tracks);
    }


    private static EndingType getEndingType(int number) {
        if (number < 1) return EndingType.ZERO;
        if (number == 1) return EndingType.ONE;
        if (10 <= number && number <= 20) return EndingType.TEN_TWENTY;

        int lastDigit = number % 10;
        if (lastDigit == 1) return EndingType.X1;
        if (2 <= lastDigit && lastDigit <= 4) return EndingType.X2_4;
        return EndingType.X5_0;
    }


    private enum EndingType {
        ZERO,
        ONE,
        TEN_TWENTY,
        X1,
        X2_4,
        X5_0
    }
}
