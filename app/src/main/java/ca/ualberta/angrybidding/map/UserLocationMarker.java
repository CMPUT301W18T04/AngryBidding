package ca.ualberta.angrybidding.map;

import android.content.Context;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.slouple.android.AdvancedActivity;
import com.slouple.android.PermissionRequest;
import com.slouple.android.PermissionRequestListener;

import java.util.ArrayList;

import ca.ualberta.angrybidding.R;

/**
 * LocationMarker that displays device's current location
 */
public class UserLocationMarker extends MapObject {
    private ArrayList<FusedLocationManagerListener> onLocationChangeListeners = new ArrayList<>();

    private FusedLocationManager fusedLocationManager;

    private String permissionError;
    private String providerError;
    private String locationNotSetError;

    public UserLocationMarker(Context context) {
        this(context, null);
    }

    public UserLocationMarker(Context context, final FusedLocationManagerListener onLocationChangeListener) {
        super(context, new LocationPoint(0, 0));
        if (onLocationChangeListener != null) {
            onLocationChangeListeners.add(onLocationChangeListener);
        }
        permissionError = context.getString(R.string.requestGPSPermission);
        providerError = context.getString(R.string.requestGPSEnable);
        locationNotSetError = context.getString(R.string.waitForLocation);

        FrameLayout layout = (FrameLayout) LayoutInflater.from(context).inflate(R.layout.view_user_location_marker, this, false);
        addView(layout);
        setVisibility(INVISIBLE);
        final FusedLocationManagerListener selfLocationPointListener = new FusedLocationManagerListener() {
            @Override
            public void onLocationChanged(final Location location, final LocationPoint locationPoint) {
                UserLocationMarker.this.post(new Runnable() {
                    @Override
                    public void run() {
                        UserLocationMarker.this.onLocationChange(locationPoint);
                        for (FusedLocationManagerListener listener : onLocationChangeListeners) {
                            listener.onLocationChanged(location, locationPoint);
                        }
                    }
                });
            }

            @Override
            public void onProviderEnabled() {
                Log.d("UserLocationMarker", "onProviderEnabled");
                if (getLastLocationPoint() != null) {
                    UserLocationMarker.this.setVisibility(VISIBLE);
                }
                for (FusedLocationManagerListener listener : onLocationChangeListeners) {
                    listener.onProviderEnabled();
                }
            }

            @Override
            public void onProviderDisabled() {
                Log.d("UserLocationMarker", "onProviderDisabled");
                UserLocationMarker.this.setVisibility(INVISIBLE);
                for (FusedLocationManagerListener listener : onLocationChangeListeners) {
                    listener.onProviderDisabled();
                }
            }
        };
        fusedLocationManager = new FusedLocationManager((AdvancedActivity) getContext(), selfLocationPointListener);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("super", super.onSaveInstanceState());
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            super.onRestoreInstanceState(bundle.getParcelable("super"));
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    public void addLocationListener() {
        fusedLocationManager.addListeners();
    }

    public void removeLocationListener() {
        fusedLocationManager.removeListeners();
        setVisibility(INVISIBLE);
    }

    public void addOnLocationChangeListener(FusedLocationManagerListener onLocationChangeListener) {
        onLocationChangeListeners.add(onLocationChangeListener);
    }

    public void removeOnLocationChangeListener(FusedLocationManagerListener onLocationChangeListener) {
        onLocationChangeListeners.remove(onLocationChangeListener);
    }

    public void clearOnLocationChangeListener() {
        onLocationChangeListeners.clear();
    }

    public void stop() {
        removeLocationListener();
    }

    @Override
    public void onMapViewLocationChange() {
        super.onMapViewLocationChange();
    }

    @Override
    public void onMapViewPost() {
        super.onMapViewPost();
        updateLayout();
        requestLayout();
        invalidate();
    }

    @Override
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        setOffset(new Point(getWidth() / 2, (int) (getHeight() * 0.85)));
        setScaleX(1);
        setScaleY(1);
    }

    public void updateLayout() {
    }

    protected void onLocationChange(LocationPoint locationPoint) {
        this.setVisibility(VISIBLE);
        this.setLocation(locationPoint);
    }

    public PermissionRequest getPermissionRequest(PermissionRequestListener listener) {
        return fusedLocationManager.getPermissionRequest(listener);
    }

    public boolean hasPermission() {
        return fusedLocationManager.hasPermission();
    }

    public boolean isProviderEnabled() {
        return fusedLocationManager.hasEnabledProvider();
    }

    public LocationPoint getLastLocationPoint() {
        if (!isProviderEnabled()) {
            return null;
        }
        return fusedLocationManager.getBestGuessLocationPoint();
    }

    /**
     * Whether UserLocationMarker can be used
     * @param showAlert Whether to show alert dialogue
     * @return Whether UserLocationMarker can be used
     */
    public boolean isAvailable(boolean showAlert) {
        //Permission
        if (!hasPermission()) {
            if (showAlert) {
                Toast.makeText(getContext(), permissionError,
                        Toast.LENGTH_LONG).show();
            }
            return false;
        }
        //Provider
        if (!isProviderEnabled()) {
            if (showAlert) {
                Toast.makeText(getContext(), providerError,
                        Toast.LENGTH_LONG).show();
            }
            return false;
        }
        //Last Location
        LocationPoint lastLocationPoint = getLastLocationPoint();
        if (lastLocationPoint == null) {
            if (showAlert) {
                Toast.makeText(getContext(), locationNotSetError,
                        Toast.LENGTH_LONG).show();
            }
            return false;
        }

        return true;
    }
}
