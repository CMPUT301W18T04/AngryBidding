package ca.ualberta.angrybidding;

import android.location.Location;

/**
 * Data type for a point of location on an Mercator map
 * Supports z / zoom
 */
public class LocationPoint {
    private double longitude;
    private double latitude;
    private transient double x;
    private transient double y;
    private transient int z;

    /**
     * Convert android.location.Location to LocationPoint
     * @param location Location object
     */
    public LocationPoint(Location location) {
        this(location.getLatitude(), location.getLongitude());
    }

    /**
     * @param latitude Latitude
     * @param longitude Longitude
     */
    public LocationPoint(double latitude, double longitude) {
        this.z = 1;
        setLongitude(longitude);
        setLatitude(latitude);
    }

    /**
     * @param x X cord on map
     * @param y Y cord on map
     * @param z Zoom
     */
    public LocationPoint(double x, double y, int z) {
        this.z = z;
        setX(x);
        setY(y);
    }

    /**
     * Set Longitude
     * X will also be updated
     * @param longitude Longitude
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
        this.x = longitudeToX(longitude, z);
    }

    /**
     * Set Latitude
     * Y will also be updated
     * @param latitude Latitude
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
        this.y = latitudeToY(latitude, z);
    }

    /**
     * Set X
     * Longitude will also be updated
     * @param x X cord on map
     */
    public void setX(double x) {
        this.x = x;
        this.longitude = xToLongitude(getRelativeX(), z);
    }

    /**
     * Set Y
     * Latitude will also be updated
     * @param y Y cord on map
     */
    public void setY(double y) {
        this.y = y;
        this.latitude = yToLatitude(getRelativeY(), z);
    }

    /**
     * Set Z, Zoom
     * X and Y will both be updated
     * @param z Zoom
     */
    public void setZ(int z) {
        if (z < 1) {
            z = 1;
        }
        double factor = Math.pow(2, (z - this.z));
        x = x * factor;
        y = y * factor;
        this.z = z;
    }

    /**
     * @return Longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * @return Latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Get X
     * @return X cord on map
     */
    public double getX() {
        return x;
    }

    /**
     * Get Y
     * @return Y cord on map
     */
    public double getY() {
        return y;
    }

    /**
     * Get zoom
     * @return Zoom
     */
    public int getZ() {
        return z;
    }

    /**
     * @return new instance of LocationPoint with the exact same values
     */
    public LocationPoint copy() {
        return new LocationPoint(getX(), getY(), getZ());
    }

    /**
     * @return Relative X cord when map wraps around
     */
    public double getRelativeX() {
        if (x > 0) {
            return x % Math.pow(2, z);
        } else {
            return Math.pow(2, z) - Math.abs(x % Math.pow(2, z));
        }
    }

    /**
     * @return Relative Y cord when map wraps around
     */
    public double getRelativeY() {
        if (y > 0) {
            return y % Math.pow(2, z);
        } else {
            return Math.pow(2, z) - Math.abs(y % Math.pow(2, z));
        }
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

    /**
     * Convert LocationPoint string to object
     * @param string LocationPoint string
     * @return Converted LocationPoint
     */
    public static LocationPoint fromString(String string) {
        String[] parts = string.split(",");
        return new LocationPoint(Double.valueOf(parts[0]), Double.valueOf(parts[1]), Integer.valueOf(parts[2]));
    }


    //http://wiki.openstreetmap.org/wiki/Slippy_map_tilenames#Mathematics

    /**
     * Convert Longitude to X
     * @param longitude Longitude
     * @param z Zoom level
     * @return X cord on map
     */
    public static double longitudeToX(double longitude, int z) {
        int n = (int) Math.pow((double) 2, (double) z);
        return n * ((longitude + 180) / 360);
    }

    /**
     * Convert Latitude to Y
     * @param latitude Latitude
     * @param z Zoom level
     * @return Y cord on map
     */
    public static double latitudeToY(double latitude, int z) {
        int n = (int) Math.pow((double) 2, (double) z);
        double latitudeRad = latitude / 360 * 2 * Math.PI;
        return n * (1 - (Math.log(Math.tan(latitudeRad) + 1 / Math.cos(latitudeRad)) / Math.PI)) / 2;
    }

    /**
     * Convert X to Longitude
     * @param x X cord on map
     * @param z Zoom level
     * @return Longitude
     */
    public static double xToLongitude(double x, int z) {
        int n = (int) Math.pow((double) 2, (double) z);
        return x / n * 360 - 180;
    }

    /**
     * Convert Y to Latitude
     * @param y Y cord on map
     * @param z Zoom level
     * @return Latitude
     */
    public static double yToLatitude(double y, int z) {
        int n = (int) Math.pow((double) 2, (double) z);
        return Math.atan(Math.sinh(Math.PI - y / n * 2 * Math.PI)) * 180 / Math.PI;
    }
}