package ca.ualberta.angrybidding.map;

import android.os.Parcel;
import android.os.Parcelable;

public class LocationArea implements Parcelable {
    private LocationPoint min;
    private LocationPoint max;

    public LocationArea(LocationPoint min, LocationPoint max) {
        this.min = min;
        this.max = max;
    }

    public LocationPoint getMin() {
        return min;
    }

    public LocationPoint getMax() {
        return max;
    }

    @Override
    public boolean equals(Object area) {
        if (area instanceof LocationArea) {
            return getMin().equals(((LocationArea) area).getMin()) &&
                    getMax().equals(((LocationArea) area).getMax());
        }
        return false;
    }

    // Parcelable
    public LocationArea(Parcel in) {
        min = in.readParcelable(LocationPoint.class.getClassLoader());
        max = in.readParcelable(LocationPoint.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(min, flags);
        dest.writeParcelable(max, flags);
    }

    public static final Creator CREATOR = new Creator() {
        public LocationArea createFromParcel(Parcel in) {
            return new LocationArea(in);
        }

        public LocationArea[] newArray(int size) {
            return new LocationArea[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
}
