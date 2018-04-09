package ca.ualberta.angrybidding.map;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

/**
 * Location Point Listener used in FusedLocationManager
 */
public abstract class LocationPointListener implements LocationListener {
    private boolean providerEnabled = false;
    private String providerName;

    public LocationPointListener(String providerName) {
        this.providerName = providerName;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location == null) {
            return;
        }
        if (!isProviderEnabled()) {
            onProviderEnabled(providerName);
        }
        LocationPoint locationPoint = new LocationPoint(location);
        onLocationChanged(location, locationPoint);
    }

    public abstract void onLocationChanged(Location location, LocationPoint locationPoint);

    public String getProviderName() {
        return providerName;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        switch (status) {
            case LocationProvider.AVAILABLE:
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                break;
            case LocationProvider.OUT_OF_SERVICE:
                break;
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("LocationPointListener", "onProviderEnabled " + provider);
        providerEnabled = true;
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("LocationPointListener", "onProviderDisabled " + provider);
        providerEnabled = false;
    }

    public boolean isProviderEnabled() {
        return providerEnabled;
    }
}