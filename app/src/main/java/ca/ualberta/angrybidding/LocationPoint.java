package ca.ualberta.angrybidding;

import android.location.Location;

public class LocationPoint {
    private double longitude;
    private double latitude;
    private double x;
    private double y;
    private int z;

    public LocationPoint(Location location) {
        this(location.getLatitude(), location.getLongitude());
    }

    public LocationPoint(double latitude, double longitude) {
        this.z = 1;
        setLongitude(longitude);
        setLatitude(latitude);
    }

    public LocationPoint(double x, double y, int z) {
        this.z = z;
        setX(x);
        setY(y);
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
        this.x = longitudeToX(longitude, z);
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
        this.y = latitudeToY(latitude, z);
    }

    public void setX(double x) {
        this.x = x;
        this.longitude = xToLongitude(getRelativeX(), z);
    }

    public void setY(double y) {
        this.y = y;
        this.latitude = yToLatitude(getRelativeY(), z);
    }

    public void setZ(int z) {
        if (z < 1) {
            z = 1;
        }
        double factor = Math.pow(2, (z - this.z));
        x = x * factor;
        y = y * factor;
        this.z = z;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public LocationPoint copy() {
        return new LocationPoint(getX(), getY(), getZ());
    }

    public double getRelativeX() {
        if (x > 0) {
            return x % Math.pow(2, z);
        } else {
            return Math.pow(2, z) - Math.abs(x % Math.pow(2, z));
        }
    }

    public double getRelativeY() {
        if (y > 0) {
            return y % Math.pow(2, z);
        } else {
            return Math.pow(2, z) - Math.abs(y % Math.pow(2, z));
        }
    }

    public static LocationPoint newInstance(LocationPoint locationPoint) {
        return new LocationPoint(locationPoint.getX(), locationPoint.getY(), locationPoint.getZ());
    }

    @Override
    public boolean equals(Object point) {
        if (point instanceof LocationPoint) {
            return getLongitude() == ((LocationPoint) point).getLongitude() &&
                    getLatitude() == ((LocationPoint) point).getLatitude();
        }
        return false;
    }

    @Override
    public String toString() {
        return getX() + "," + getY() + "," + getZ();
    }

    public static LocationPoint fromString(String string) {
        String[] parts = string.split(",");
        return new LocationPoint(Double.valueOf(parts[0]), Double.valueOf(parts[1]), Integer.valueOf(parts[2]));
    }


    //http://wiki.openstreetmap.org/wiki/Slippy_map_tilenames#Mathematics
    public static double longitudeToX(double longitude, int z) {
        int n = (int) Math.pow((double) 2, (double) z);
        return n * ((longitude + 180) / 360);
    }

    public static double latitudeToY(double latitude, int z) {
        int n = (int) Math.pow((double) 2, (double) z);
        double latitudeRad = latitude / 360 * 2 * Math.PI;
        return n * (1 - (Math.log(Math.tan(latitudeRad) + 1 / Math.cos(latitudeRad)) / Math.PI)) / 2;
    }

    public static double xToLongitude(double x, int z) {
        int n = (int) Math.pow((double) 2, (double) z);
        return x / n * 360 - 180;
    }

    public static double yToLatitude(double y, int z) {
        int n = (int) Math.pow((double) 2, (double) z);
        return Math.atan(Math.sinh(Math.PI - y / n * 2 * Math.PI)) * 180 / Math.PI;
    }
}