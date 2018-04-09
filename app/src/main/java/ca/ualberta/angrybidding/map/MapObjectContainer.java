package ca.ualberta.angrybidding.map;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.slouple.android.ViewHelper;

import ca.ualberta.angrybidding.R;

/**
 * ViewGroup / Container for MapObjects
 */
public class MapObjectContainer extends RelativeLayout {

    public double scale = 1;
    public MapView mapView;
    private boolean postMapView = false;
    private int mapViewID;

    public MapObjectContainer(Context context) {
        this(context, null);
    }

    public MapObjectContainer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MapObjectContainer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (attrs == null) {
            init(-1);
        } else {
            TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.MapObjectContainer, defStyle, 0);
            int mapViewID = ta.getResourceId(R.styleable.MapObjectContainer_mapViewID, -1);
            ta.recycle();
            init(mapViewID);
        }
    }

    private void init(int mapViewID) {
        this.mapViewID = mapViewID;
    }

    public MapObjectContainer(Context context, int mapViewID) {
        super(context);
        init(mapViewID);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mapView = (MapView) ((ViewGroup) getParent()).findViewById(mapViewID);
        mapView.post(new Runnable() {
            @Override
            public void run() {
                postMapView = true;
                for (int i = 0; i < getChildCount(); i++) {
                    View child = getChildAt(i);
                    if (child instanceof MapObject) {
                        ((MapObject) child).onMapViewPost();
                    }
                }
            }
        });

        mapView.addOnMapLocationChangeListener(new MapView.OnMapLocationChangeListener() {
            @Override
            public void onLocationChange(LocationPoint newLocation) {
                MapObjectContainer.this.onMapViewLocationChange();
            }
        });

        if (mapView instanceof ScalableMapView) {
            ((ScalableMapView) mapView).addOnMapZoomChangeListener(new ScalableMapView.OnMapZoomChangeListener() {
                @Override
                public void onZoomChange(double zoom) {
                    MapObjectContainer.this.onMapViewLocationChange();
                }

                @Override
                public void onBaseScaleChange(double baseScale) {
                    MapObjectContainer.this.onMapViewLocationChange();
                }
            });
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("super", super.onSaveInstanceState());
        bundle.putDouble("scale", scale);
        bundle.putInt("mapViewID", mapViewID);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            scale = bundle.getDouble("scale");
            mapView = (MapView) ((Activity) getContext()).findViewById(bundle.getInt("mapViewID"));
            super.onRestoreInstanceState(bundle.getParcelable("super"));
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.scale((float) scale, (float) scale, mapView.getWidth() / 2, mapView.getHeight() / 2);
        super.onDraw(canvas);
    }

    /**
     * Notify all MapObjects
     */
    public void invalidateRecursively() {
        ViewHelper.invalidateRecursively(this);
    }

    private void onMapViewLocationChange() {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof MapObject) {
                ((MapObject) child).onMapViewLocationChange();
            }
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
    }

    /**
     * DO NOT USE
     * MapObjectContainer should only contain MapObjects
     * @param view
     */
    @Deprecated
    @Override
    public void addView(View view) {
        if (view instanceof MapObject) {
            addView((MapObject) view);
        }
    }

    /**
     * Add a MapObject
     * @param mapObject MapObject to add
     */
    public void addView(MapObject mapObject) {
        super.addView(mapObject);
        if (postMapView) {
            mapObject.onMapViewPost();
        }
    }

    @Deprecated
    @Override
    public void removeView(View view) {
        if (view instanceof MapObject) {
            removeView((MapObject) view);
        }
    }

    public void removeView(MapObject mapObject) {
        super.removeView(mapObject);
    }

    public MapView getMapView() {
        return mapView;
    }

    public double getScale() {
        return scale;
    }

    public boolean isPostMapView() {
        return postMapView;
    }

    public void setScale(double scale) {
        this.scale = scale;
        invalidateRecursively();
    }
}
