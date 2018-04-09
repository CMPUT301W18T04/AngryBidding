package ca.ualberta.angrybidding.map;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Map data type
 */
public class Map implements Parcelable {
    private String name;
    private String url;
    private int tileSize;
    private int maxZ;

    /**
     * @param name Name of the map
     * @param url Url to load tiles from
     * @param tileSize Tile size
     * @param maxZ Max Z
     */
    public Map(String name, String url, int tileSize, int maxZ) {
        this.name = name;
        this.url = url.startsWith("http://") || url.startsWith("https://") ? url : "http://" + url;
        this.tileSize = tileSize;
        this.maxZ = maxZ;
    }

    public String getName() {
        return name;
    }

    public String getURL() {
        return url;
    }

    public int getTileSize() {
        return tileSize;
    }

    public int getMaxZ() {
        return maxZ;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Map) {
            return getName().equals(((Map) other).getName()) &&
                    getTileSize() == ((Map) other).getTileSize() &&
                    getMaxZ() == ((Map) other).getMaxZ() &&
                    getURL().equals(((Map) other).getURL());
        }
        return false;
    }

    // Parcelable
    public Map(Parcel in) {
        name = in.readString();
        url = in.readString();
        tileSize = in.readInt();
        maxZ = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(url);
        dest.writeInt(tileSize);
        dest.writeInt(maxZ);
    }

    public static final Creator CREATOR = new Creator() {
        public Map createFromParcel(Parcel in) {
            return new Map(in);
        }

        public Map[] newArray(int size) {
            return new Map[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
}