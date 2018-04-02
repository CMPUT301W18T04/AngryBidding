package ca.ualberta.angrybidding.map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.RelativeLayout;

public class MapObject extends RelativeLayout {
    private LocationPoint location;
    private Point offset;

    public MapObject(Context context, LocationPoint location) {
        this(context, location, new Point(0, 0));
    }

    public MapObject(Context context, LocationPoint location, Point offset) {
        super(context);
        this.location = location;
        this.offset = offset;
        setWillNotDraw(false);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("super", super.onSaveInstanceState());
        bundle.putParcelable("location", location);
        bundle.putParcelable("offset", offset);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            location = bundle.getParcelable("location");
            offset = bundle.getParcelable("offset");
            super.onRestoreInstanceState(bundle.getParcelable("super"));
        }else{
            super.onRestoreInstanceState(state);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    public void onMapViewLocationChange() {
        LocationPoint mapViewLocation = getMapView().getLocation();
        location.setZ(mapViewLocation.getZ());

        double x;
        double y;
        //Warp if mapView location is at 1/4 edges of the map
        if (mapViewLocation.getRelativeX() < Math.pow(2, mapViewLocation.getZ() - 2) && location.getRelativeX() > Math.pow(2, mapViewLocation.getZ() - 1)) {
            x = location.getRelativeX() - Math.pow(2, mapViewLocation.getZ()) - mapViewLocation.getRelativeX();
        } else if (mapViewLocation.getRelativeX() > Math.pow(2, mapViewLocation.getZ()) / 4 * 3 && location.getRelativeX() < Math.pow(2, mapViewLocation.getZ() - 1)) {
            x = location.getRelativeX() + Math.pow(2, mapViewLocation.getZ()) - mapViewLocation.getRelativeX();
        } else {
            x = location.getRelativeX() - mapViewLocation.getRelativeX();
        }

        if (mapViewLocation.getRelativeY() < Math.pow(2, mapViewLocation.getZ() - 2) && location.getRelativeY() > Math.pow(2, mapViewLocation.getZ() - 1)) {
            y = location.getRelativeY() - Math.pow(2, mapViewLocation.getZ()) - mapViewLocation.getRelativeY();
        } else if (mapViewLocation.getRelativeY() > Math.pow(2, mapViewLocation.getZ()) / 4 * 3 && location.getRelativeY() < Math.pow(2, mapViewLocation.getZ() - 1)) {
            y = location.getRelativeY() + Math.pow(2, mapViewLocation.getZ()) - mapViewLocation.getRelativeY();
        } else {
            y = location.getRelativeY() - mapViewLocation.getRelativeY();
        }

        //Change to pixels
        x *= (getMapView().getMap().getTileSize());
        y *= (getMapView().getMap().getTileSize());

        if(getMapView() instanceof ScalableMapView){
            x *= ((ScalableMapView)getMapView()).getTotalScale();
            y *= ((ScalableMapView)getMapView()).getTotalScale();
        }

        //Center
        x += getMapView().getWidth() / 2;
        y += getMapView().getHeight() / 2;

        //Add object offsets
        x -= offset.x;
        y -= offset.y;

        //Scale with parent
        double parentScale = ((MapObjectContainer) (getParent())).getScale();
        x /= parentScale;
        y /= parentScale;

        setX((float) (x));
        setY((float) (y));
    }

    public void onMapViewPost() {
        onMapViewLocationChange();
    }

    public MapObjectContainer getContainer() {
        return (MapObjectContainer)getParent();
    }

    public MapView getMapView() {
        if(getContainer() == null){
            return null;
        }
        return getContainer().getMapView();
    }

    public void setOffset(Point offset) {
        this.offset = offset;
    }

    public LocationPoint getLocation() {
        return location;
    }

    public void setLocation(LocationPoint location){
        this.location = location;
        if(getMapView() != null) {
            onMapViewLocationChange();
        }
    }
}
