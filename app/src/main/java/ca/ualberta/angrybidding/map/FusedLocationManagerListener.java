package ca.ualberta.angrybidding.map;

import android.location.Location;

/**
 * Listener for FusedLocationManager
 */
public abstract class FusedLocationManagerListener {
    /**
     * Called when location changes
     * @param location New Location
     * @param locationPoint New Location Point
     */
    public abstract void onLocationChanged(Location location, LocationPoint locationPoint);

    public abstract void onProviderDisabled();

    public abstract void onProviderEnabled();
}
