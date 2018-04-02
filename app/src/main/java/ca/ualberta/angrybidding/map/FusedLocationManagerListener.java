package ca.ualberta.angrybidding.map;

import android.location.Location;

public abstract class FusedLocationManagerListener {
    public abstract void onLocationChanged(Location location, LocationPoint locationPoint);
    public abstract void onProviderDisabled();
    public abstract void onProviderEnabled();
}
