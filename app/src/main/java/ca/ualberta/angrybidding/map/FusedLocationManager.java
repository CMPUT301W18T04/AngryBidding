package ca.ualberta.angrybidding.map;

import android.Manifest;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.slouple.android.AdvancedActivity;
import com.slouple.android.MissingPermissionException;
import com.slouple.android.PermissionRequest;
import com.slouple.android.PermissionRequestListener;

import java.util.HashMap;

/**
 * Location Manager that handles GPS events
 */
public class FusedLocationManager {
    private AdvancedActivity activity;
    private LocationManager locationManager;
    private FusedLocationManagerListener eventListener;

    private HashMap<String, LocationPointListener> locationListeners = new HashMap<>();

    private Location bestGuessLocation;

    public final static String PERMISSION_STRING = Manifest.permission.ACCESS_FINE_LOCATION;


    /**
     * @param activity Activity
     * @param eventListener Listener
     */
    public FusedLocationManager(AdvancedActivity activity, FusedLocationManagerListener eventListener) {
        this.activity = activity;
        this.eventListener = eventListener;
        locationManager = (LocationManager) activity.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
    }

    /**
     * Add all listener
     * @throws MissingPermissionException
     */
    public void addListeners() throws MissingPermissionException {
        addGpsListener();
        addNetworkListener();
        addPassiveListener();
    }

    public PermissionRequest getPermissionRequest(PermissionRequestListener listener) {
        return new PermissionRequest(PERMISSION_STRING, listener);
    }

    @SuppressWarnings("MissingPermission")
    private void addListener(final String name) {
        if (locationListeners.containsKey(name)) {
            return;
        }
        if (!hasPermission()) {
            throw new MissingPermissionException(PERMISSION_STRING);
        }

        final LocationPointListener locationPointListener = new LocationPointListener(name) {
            @Override
            public void onLocationChanged(Location location, LocationPoint locationPoint) {
                if (isBetterLocation(location, bestGuessLocation)) {
                    bestGuessLocation = location;
                    eventListener.onLocationChanged(location, locationPoint);
                }
            }

            @Override
            public void onProviderEnabled(String provider) {
                super.onProviderEnabled(provider);
                //Only calls when it's the first provider to be enabled
                for (LocationPointListener listener : locationListeners.values()) {
                    if (listener != this && listener.isProviderEnabled()) {
                        return;
                    }
                }
                eventListener.onProviderEnabled();
            }

            @Override
            public void onProviderDisabled(String provider) {
                super.onProviderDisabled(provider);
                //Only calls when the last provider is disabled
                for (LocationPointListener listener : locationListeners.values()) {
                    if (listener.isProviderEnabled()) {
                        Log.d("FusedLocationManager", "Provider " + listener.getProviderName() + " is still enabled");
                        return;
                    }
                }
                eventListener.onProviderDisabled();
            }
        };
        locationListeners.put(name, locationPointListener);
        locationManager.requestLocationUpdates(name, 0, 0, locationPointListener);
        locationPointListener.onLocationChanged(locationManager.getLastKnownLocation(name));

    }

    /**
     * GPS Listener
     */
    public void addGpsListener() {
        try {
            addListener(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            Log.e("FusedLocationManager", e.getMessage(), e);
        }
    }

    /**
     * Network Listener
     */
    public void addNetworkListener() {
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            addListener(LocationManager.NETWORK_PROVIDER);
        }
    }

    /**
     * Passive Listener
     */
    public void addPassiveListener() {
        if (locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {
            addListener(LocationManager.PASSIVE_PROVIDER);
        }
    }

    /**
     * @return Best Guess Location with current available listeners
     */
    public Location getBestGuessLocation() {
        return bestGuessLocation;
    }

    /**
     * @return Best Guess LocationPoint with current available listeners
     */
    public LocationPoint getBestGuessLocationPoint() {
        if (getBestGuessLocation() != null) {
            return new LocationPoint(getBestGuessLocation());
        } else {
            return null;
        }
    }

    /**
     * @return If app has location permission
     */
    public boolean hasPermission() {
        return activity.hasPermission(PERMISSION_STRING);
    }

    /**
     * Clear listeners
     */
    public void removeListeners() {
        for (LocationPointListener listener : locationListeners.values()) {
            locationManager.removeUpdates(listener);
        }
        locationListeners.clear();
    }

    public boolean isProviderEnabled(String providerName) {
        if (!locationListeners.containsKey(providerName)) {
            return false;
        }
        return locationListeners.get(providerName).isProviderEnabled();
    }

    public boolean hasEnabledProvider() {
        for (LocationPointListener listener : locationListeners.values()) {
            if (listener.isProviderEnabled()) {
                return true;
            }
        }
        return false;
    }

    private static final int TWO_MINUTES = 1000 * 60 * 2;

    /**
     * From: https://developer.android.com/guide/topics/location/strategies.html
     *
     * Determines whether one Location reading is better than the current Location fix
     *
     * @param location            The new Location that you want to evaluate
     * @param currentBestLocation The current Location fix, to which you want to compare the new one
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /**
     * Checks whether two providers are the same
     */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }
}
