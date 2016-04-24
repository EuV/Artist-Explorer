package ru.yandex.academy.euv.artistexplorer;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Data model.
 * Supplied to JSON parser input along with raw data;
 * when build passed through the app as a self-contained object.
 */
public class Artist implements Parcelable {
    private int id;
    private String name;
    private List<String> genres = new ArrayList<>();
    private int tracks;
    private int albums;
    private String link;
    private String description;
    private Cover cover;

    public Artist() { /* default constructor for the JSON parser */ }

    private Artist(Parcel in) {
        id = in.readInt();
        name = in.readString();
        genres = in.createStringArrayList();
        tracks = in.readInt();
        albums = in.readInt();
        link = in.readString();
        description = in.readString();
        cover = in.readParcelable(Cover.class.getClassLoader());
    }

    public static final Creator<Artist> CREATOR = new Creator<Artist>() {
        @Override
        public Artist createFromParcel(Parcel in) {
            return new Artist(in);
        }

        @Override
        public Artist[] newArray(int size) {
            return new Artist[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeStringList(genres);
        dest.writeInt(tracks);
        dest.writeInt(albums);
        dest.writeString(link);
        dest.writeString(description);
        dest.writeParcelable(cover, flags);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public int getTracks() {
        return tracks;
    }

    public void setTracks(int tracks) {
        this.tracks = tracks;
    }

    public int getAlbums() {
        return albums;
    }

    public void setAlbums(int albums) {
        this.albums = albums;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Cover getCover() {
        return cover;
    }

    public void setCover(Cover cover) {
        this.cover = cover;
    }


    public static class Cover implements Parcelable {
        private String small;
        private String big;

        public Cover() { /* default constructor for the JSON parser */ }

        private Cover(Parcel in) {
            small = in.readString();
            big = in.readString();
        }

        public static final Creator<Cover> CREATOR = new Creator<Cover>() {
            @Override
            public Cover createFromParcel(Parcel in) {
                return new Cover(in);
            }

            @Override
            public Cover[] newArray(int size) {
                return new Cover[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(small);
            dest.writeString(big);
        }

        public String getSmall() {
            return small;
        }

        public void setSmall(String small) {
            this.small = small;
        }

        public String getBig() {
            return big;
        }

        public void setBig(String big) {
            this.big = big;
        }
    }
}
