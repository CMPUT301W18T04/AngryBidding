package ca.ualberta.angrybidding.map;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ConcurrentSkipListSet;

import ca.ualberta.angrybidding.R;

/**
 * MapView with Scaling
 */
public class ScalableMapView extends MapView {
    private double baseScale;
    private double zoom;

    private int minZoom;

    private ArrayList<OnMapZoomChangeListener> onMapLocationChangeListeners = new ArrayList<>();

    public ScalableMapView(Context context) {
        this(context, null);
    }

    public ScalableMapView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScalableMapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (attrs == null) {
            init(1, 1.75f);
        } else {
            TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.ScalableMapView, defStyle, 0);
            int minZoom = ta.getInt(R.styleable.ScalableMapView_minZoom, 1);
            float baseScale = ta.getFloat(R.styleable.ScalableMapView_baseScale, 1.75f);
            ta.recycle();
            init(minZoom, baseScale);
        }
    }

    public ScalableMapView(Context context, Map map, LocationPoint location, String bitmapLoaderName, int minZoom, float baseScale, boolean debugView) {
        super(context, map, location, bitmapLoaderName, debugView);
        init(minZoom, baseScale);
    }

    private void init(int minZoom, float baseScale) {
        mapTiles = Collections.synchronizedSortedSet(new ConcurrentSkipListSet<>(new MapTileZComparator(false)));
        this.baseScale = baseScale;
        setZoom(super.getLocation().getZ());
        setMinZoom(minZoom);

    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("super", super.onSaveInstanceState());
        bundle.putDouble("zoom", zoom);
        bundle.putDouble("baseScale", baseScale);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            zoom = bundle.getDouble("zoom");
            baseScale = bundle.getDouble("baseScale");
            super.onRestoreInstanceState(bundle.getParcelable("super"));
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        canvas.scale((float) getTotalScale(), (float) getTotalScale(), centerX, centerY);
        super.onDraw(canvas);
    }

    @Override
    protected Rect getTileDrawLocation(MapTile tile) {
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        double factor = Math.pow(2, getZoomInt() - tile.getZ());
        int tileSize = (int) (getMap().getTileSize() * factor);
        int left = (int) (centerX - (location.getX() / factor - tile.getX()) * tileSize);
        int top = (int) (centerY - (location.getY() / factor - tile.getY()) * tileSize);

        int right = left + tileSize;
        int bottom = top + tileSize;

        Rect mapTileRect = new Rect(left, top, right, bottom);
        return mapTileRect;
    }

    /**
     * Draw debug info
     * @param canvas
     */
    @Override
    protected void drawDebug(Canvas canvas) {
        super.drawDebug(canvas);
        debugPaint.setStyle(Paint.Style.STROKE);
        debugPaint.setColor(Color.argb(50, 0, 0, 0));
        for (MapTile tile : mapTiles) {
            Rect mapTileRect = getTileDrawLocation(tile);
            if (tile.getState() == MapTile.LoadState.Initialized) {
                debugPaint.setColor(Color.RED);
            } else if (tile.getState() == MapTile.LoadState.Loading) {
                debugPaint.setColor(Color.YELLOW);
            } else if (tile.getState() == MapTile.LoadState.Loaded) {
                debugPaint.setColor(Color.GREEN);
            } else if (tile.getState() == MapTile.LoadState.Destroyed) {
                debugPaint.setColor(Color.BLACK);
            } else {
                debugPaint.setColor(Color.WHITE);
            }
            canvas.drawText(String.valueOf(tile.getZ()), mapTileRect.left, mapTileRect.top + 15, debugPaint);
            canvas.drawRect(mapTileRect.left, mapTileRect.top, mapTileRect.right, mapTileRect.bottom, debugPaint);
        }
        debugPaint.setColor(Color.argb(100, 0, 0, 0));
        canvas.drawText("Z Int: " + getZoomInt(), getWidth() / 2, getHeight() / 2 + 225, debugPaint);
        canvas.drawText("Zoom: " + getZoom(), getWidth() / 2, getHeight() / 2 + 250, debugPaint);
        canvas.drawText("Zoom Scale: " + getZoomScale(), getWidth() / 2, getHeight() / 2 + 275, debugPaint);
    }

    @Override
    protected void updateTileNum() {
        Log.d("ScalableMapView", "updateTileNum");
        Point newTileCount = new Point();
        newTileCount.x = (int) (getWidth() / (getMap().getTileSize() * getTotalScale()));
        newTileCount.y = (int) (getHeight() / (getMap().getTileSize() * getTotalScale()));
        setTileCount(newTileCount);
    }

    @Override
    protected void updateTiles() {
        Log.d("ScalableMapView", "updateTiles");
        ArrayList<MapTile> newMapTiles = new ArrayList<>();
        //Get new tiles
        //From (center - tileCount/2) to (center + tileCount/2)
        for (int i = -tileCount.x / 2; i <= tileCount.x / 2; i++) {
            for (int j = -tileCount.y / 2; j <= tileCount.y / 2; j++) {
                newMapTiles.add(
                        new MapTile(
                                map,
                                (int) Math.floor(location.getX()) + i, //Uses Math.floor as location X and Y can be negative
                                (int) Math.floor(location.getY()) + j,
                                getZoomInt(), bitmapLoader) {
                            public void onLoad() {
                                queueUpdateTile();
                                invalidate();
                            }
                        }
                );
            }
        }
        //Remove old tiles
        for (MapTile tile : mapTiles) {
            if (!newMapTiles.contains(tile)) {
                tile.cancelLoad();
                if (tile.getZ() == getZoomInt() || canMapTileBeRemoved(tile)) {
                    tile.destroy();
                    mapTiles.remove(tile);
                }
            }
        }

        //Add new tiles
        for (MapTile newTile : newMapTiles) {
            if (!mapTiles.contains(newTile)) {
                mapTiles.add(newTile);
            }
        }
        for (MapTile tile : mapTiles) {
            if (newMapTiles.contains(tile)) {
                tile.load();
            }
        }
    }

    /**
     * Determine whether a tile can be safely removed without leaving empty spots on the map
     * @param inputTile
     * @return
     */
    protected boolean canMapTileBeRemoved(MapTile inputTile) {
        if (!isTileInsideCanvas(inputTile)) {
            return true;
        } else if (inputTile.getZ() > getZoomInt()) {
            return hasParentMapTile(inputTile);
        } else if (inputTile.getZ() == getZoomInt() - 1) {
            return hasAllChildrenMapTile(inputTile);
        } else {
            return false;
        }
    }

    /**
     * @param inputTile
     * @return Whether the tile is inside of the canvas
     */
    protected boolean isTileInsideCanvas(MapTile inputTile) {
        Rect mapTileRect = getTileDrawLocation(inputTile);
        int paddingSize = tilePadding * getMap().getTileSize();
        Rect canvasRect = new Rect(-paddingSize, -paddingSize, getWidth() + paddingSize, getHeight() + paddingSize);
        return mapTileRect.intersect(canvasRect);
    }

    protected boolean hasAllChildrenMapTile(MapTile inputTile) {
        int childCount = 0;
        for (MapTile tile : mapTiles) {
            if (tile.getZ() - 1 != inputTile.getZ() || tile.getState() != MapTile.LoadState.Loaded) {
                continue;
            }
            if (tile.getCoordinate().isParent(inputTile.getCoordinate())) {
                childCount++;
                if (childCount == 4) {
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean hasParentMapTile(MapTile inputTile) {
        for (MapTile tile : mapTiles) {
            if (tile.getZ() != getZoomInt() || tile.getState() != MapTile.LoadState.Loaded) {
                continue;
            }
            if (inputTile.getCoordinate().isParent(tile.getCoordinate())) {
                return true;
            }
        }
        return false;
    }

    public void setLocation(LocationPoint location) {
        if (location.getZ() < minZoom) {
            location.setZ(minZoom);
        }
        super.setLocation(location);
        setZoom(location.getZ() + getZoomScale() - 1);
    }

    public double getScale() {
        return Math.pow(2, zoom - map.getMaxZ());
    }

    public double getZoom() {
        return zoom;
    }

    public int getMinZoom() {
        return minZoom;
    }

    public int getZoomInt() {
        return (int) getZoom();
    }

    public double getZoomScale() {
        return 1 + (getZoom() - getZoomInt());
    }

    public double getBaseScale() {
        return baseScale;
    }

    /**
     * @param zoom Decimal of Z
     */
    public void setZoom(double zoom) {
        zoom = Math.max(minZoom, Math.min(zoom, map.getMaxZ()));
        if (zoom != getZoom()) {
            this.zoom = zoom;
            if (getZoomInt() != location.getZ()) {
                LocationPoint newLocationPoint = this.location.copy();
                newLocationPoint.setZ(getZoomInt());
                setLocation(newLocationPoint);
            }
            updateTileNum();
            invalidate();
            for (OnMapZoomChangeListener listener : onMapLocationChangeListeners) {
                listener.onZoomChange(this.zoom);
            }
        }
    }

    public void setMinZoom(int minZoom) {
        this.minZoom = Math.max(minZoom, 1);
        if (this.location.getZ() < minZoom) {
            this.location.setZ(minZoom);
        }
        if (zoom < minZoom) {
            setZoom(minZoom);
        }
    }

    public void setBaseScale(double baseScale) {
        this.baseScale = baseScale;
        updateTileNum();
        invalidate();
        for (OnMapZoomChangeListener listener : onMapLocationChangeListeners) {
            listener.onBaseScaleChange(this.baseScale);
        }
    }

    public double getTotalScale() {
        return getZoomScale() * getBaseScale();
    }

    public void addOnMapZoomChangeListener(OnMapZoomChangeListener listener) {
        onMapLocationChangeListeners.add(listener);
    }

    public void removeOnMapZoomChangeListener(OnMapZoomChangeListener listener) {
        onMapLocationChangeListeners.remove(listener);
    }

    interface OnMapZoomChangeListener {
        void onZoomChange(double zoom);

        void onBaseScaleChange(double baseScale);
    }
}
