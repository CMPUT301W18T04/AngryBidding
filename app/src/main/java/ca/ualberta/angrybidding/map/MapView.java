package ca.ualberta.angrybidding.map;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.slouple.android.BitmapLoader;
import com.slouple.android.BitmapLoaderFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import ca.ualberta.angrybidding.R;

public class MapView extends View {
    protected Map map;
    protected BitmapLoader bitmapLoader;
    protected volatile LocationPoint location;

    protected int tilePadding = 1;
    protected Point tileCount = new Point();

    protected Set<MapTile> mapTiles = Collections.synchronizedSortedSet(new ConcurrentSkipListSet<MapTile>());

    private Handler updateTileHandler = new Handler();
    private Runnable updateTileRunnable;

    protected boolean debugView;
    protected Paint debugPaint = new Paint();

    protected ArrayList<OnMapLocationChangeListener> onMapLocationChangeListeners = new ArrayList<>();

    //Constants and Defaults
    public static Map defaultMap = new Map("PostPhere-Default", "http://maps.postphere.com/blue/{z}/{x}/{y}.png", 256, 18);
    public static final String DEFAULT_BITMAP_LOADER_NAME = "MapView";

    public MapView(Context context) {
        this(context, null);
    }

    public MapView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (attrs == null) {
            LocationPoint locationPoint = new LocationPoint(0, 0);
            locationPoint.setZ(3);
            init(defaultMap, locationPoint, DEFAULT_BITMAP_LOADER_NAME, false);
        } else {
            TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.MapView, defStyle, 0);
            //bitmapLoaderName
            String bitmapLoaderName = ta.getString(R.styleable.MapView_bitmapLoaderName);

            //map
            String mapURL = ta.getString(R.styleable.MapView_mapURL);
            Map map;
            if (mapURL != null) {
                int mapTileSize = ta.getInt(R.styleable.MapView_mapTileSize, 256);
                int mapMaxZ = ta.getInt(R.styleable.MapView_mapMaxZ, 18);
                map = new Map("ic_map", mapURL, mapTileSize, mapMaxZ);
            } else {
                map = defaultMap;
            }
            //location
            float latitude = ta.getFloat(R.styleable.MapView_locationLatitude, 0f);
            float longitude = ta.getFloat(R.styleable.MapView_locationLongitude, 0f);
            int z = ta.getInt(R.styleable.MapView_locationZ, 3);
            LocationPoint locationPoint = new LocationPoint(latitude, longitude);
            locationPoint.setZ(z);
            //debug
            boolean debugView = ta.getBoolean(R.styleable.MapView_debug, false);
            ta.recycle();
            init(map, locationPoint, bitmapLoaderName, debugView);
        }
    }

    public MapView(Context context, Map map, LocationPoint location, String bitmapLoaderName, boolean debugView) {
        super(context);
        init(map, location, bitmapLoaderName, debugView);
    }

    private void init(Map map, LocationPoint location, String bitmapLoaderName, boolean debugView) {
        this.map = map;
        this.location = location;
        if (bitmapLoaderName == null) {
            bitmapLoaderName = DEFAULT_BITMAP_LOADER_NAME;
        }
        this.bitmapLoader = BitmapLoaderFactory.getBitmapLoader(bitmapLoaderName);
        this.debugView = debugView;

        post(new Runnable() {
            @Override
            public void run() {
                updateTileNum();
                invalidate();
            }
        });
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("super", super.onSaveInstanceState());
        bundle.putParcelable("map", map);
        bundle.putParcelable("location", location);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            map = bundle.getParcelable("map");
            location = bundle.getParcelable("location");
            super.onRestoreInstanceState(bundle.getParcelable("super"));
            updateTileNum();
            updateTiles();
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    @Override
    public void invalidate() {
        //Force to run on UI thread
        ((Activity) getContext()).runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        MapView.super.invalidate();
                    }
                }
        );
    }

    @Override
    protected void onMeasure(int width, int height) {
        super.onMeasure(width, height);
        if (isInEditMode()) {
            return;
        }
        updateTileNum();
        updateTiles();
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (isInEditMode()) {
            return;
        }
        updateTileNum();
        updateTiles();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isInEditMode()) {
            return;
        }
        for (MapTile tile : mapTiles) {
            Rect mapTileRect = getTileDrawLocation(tile);
            canvas.drawBitmap(tile.getBitmap(), null, mapTileRect, null);
        }

        if (debugView) {
            drawDebug(canvas);
        }
    }

    protected Rect getTileDrawLocation(MapTile tile) {
        int left;
        left = (int) (getWidth() / 2 - (location.getX() - tile.getX()) * map.getTileSize());
        int top;
        top = (int) (getHeight() / 2 - (location.getY() - tile.getY()) * map.getTileSize());

        int right = left + map.getTileSize();
        int bottom = top + map.getTileSize();

        Rect mapTileRect = new Rect(left, top, right, bottom);
        return mapTileRect;
    }

    protected void drawDebug(Canvas canvas) {
        debugPaint.setStyle(Paint.Style.FILL);
        debugPaint.setColor(Color.argb(100, 0, 0, 0));
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, 3, debugPaint);
        debugPaint.setTextSize(20f);
        canvas.drawText("X: " + location.getX(), getWidth() / 2, getHeight() / 2 + 25, debugPaint);
        canvas.drawText("Y: " + location.getY(), getWidth() / 2, getHeight() / 2 + 50, debugPaint);
        canvas.drawText("Z: " + location.getZ(), getWidth() / 2, getHeight() / 2 + 75, debugPaint);
        canvas.drawText("Width: " + getWidth(), getWidth() / 2, getHeight() / 2 + 100, debugPaint);
        canvas.drawText("Height: " + getHeight(), getWidth() / 2, getHeight() / 2 + 125, debugPaint);
        canvas.drawText("Tile X: " + tileCount.x, getWidth() / 2, getHeight() / 2 + 150, debugPaint);
        canvas.drawText("Tile Y: " + tileCount.y, getWidth() / 2, getHeight() / 2 + 175, debugPaint);
        canvas.drawText("Tile Padding: " + tilePadding, getWidth() / 2, getHeight() / 2 + 200, debugPaint);

        debugPaint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), debugPaint);

        canvas.drawRect(0, 0, this.getWidth(), this.getHeight(), debugPaint);
    }

    public Map getMap() {
        return map;
    }

    public LocationPoint getLocation() {
        return location;
    }

    public void setLocation(LocationPoint location) {
        if ((int) this.location.getX() != (int) location.getX() || (int) this.location.getY() != (int) location.getY() ||
                this.location.getZ() != location.getZ()) {
            this.location = location; //Set new location has to be before updateTiles
            queueUpdateTile();
        } else {
            this.location = location;
        }
        invalidate();
        for (OnMapLocationChangeListener listener : onMapLocationChangeListeners) {
            listener.onLocationChange(this.location.copy());
        }
    }

    public void setTilePadding(int tilePadding) {
        this.tilePadding = tilePadding;
        updateTileNum();
    }

    protected void updateTileNum() {
        Point newTileCount = new Point();
        newTileCount.x = (int) Math.ceil(getWidth() / (map.getTileSize()));
        newTileCount.y = (int) Math.ceil(getHeight() / (map.getTileSize()));
        setTileCount(newTileCount);
    }

    protected void setTileCount(Point newTileCount) {
        Log.d("MapView", "setTileCount");
        //Set to even number
        newTileCount.x = newTileCount.x % 2 == 1 ? newTileCount.x + 1 : newTileCount.x;
        newTileCount.y = newTileCount.y % 2 == 1 ? newTileCount.y + 1 : newTileCount.y;
        //Add Padding
        newTileCount.x += tilePadding * 2;
        newTileCount.y += tilePadding * 2;
        //Update only if changed
        if (tileCount.x != newTileCount.x || tileCount.x != newTileCount.y) {
            tileCount = newTileCount;
            queueUpdateTile();
        }
    }

    public void queueUpdateTile() {
        if (updateTileRunnable == null) {
            updateTileRunnable = new Runnable() {
                @Override
                public void run() {
                    updateTileRunnable = null;
                    updateTiles();
                }
            };
            updateTileHandler.postDelayed(updateTileRunnable, 100);
        }
    }

    protected void updateTiles() {
        Log.d("MapView", "updateTiles");
        ArrayList<MapTile> newMapTiles = new ArrayList<>();
        //Get new tiles
        for (int i = -tileCount.x / 2; i <= tileCount.x / 2; i++) {
            for (int j = -tileCount.y / 2; j <= tileCount.y / 2; j++) {
                newMapTiles.add(
                        new MapTile(
                                map,
                                (int) Math.floor(location.getX()) + i,
                                (int) Math.floor(location.getY()) + j,
                                location.getZ(), bitmapLoader) {
                            public void onLoad() {
                                invalidate();
                            }
                        }
                );
            }
        }
        //Remove old tiles
        for (MapTile tile : mapTiles) {
            if (!newMapTiles.contains(tile)) {
                tile.destroy();
                mapTiles.remove(tile);
            }
        }
        //Add new tiles
        for (MapTile newTile : newMapTiles) {
            if (!mapTiles.contains(newTile)) {
                mapTiles.add(newTile);
                newTile.load();
            }
        }
    }

    public LocationArea getBound() {
        LocationPoint min = new LocationPoint(location.getRelativeX() - tileCount.x / 2, location.getRelativeY() - tileCount.y / 2, location.getZ());
        LocationPoint max = new LocationPoint(location.getRelativeX() + tileCount.x / 2, location.getRelativeY() + tileCount.y / 2, location.getZ());
        return new LocationArea(min, max);
    }

    public LocationArea getScreenBound() {
        LocationPoint min = new LocationPoint(location.getRelativeX() - (tileCount.x - tilePadding) / 2, location.getRelativeY() - (tileCount.y - tilePadding) / 2, location.getZ());
        LocationPoint max = new LocationPoint(location.getRelativeX() + (tileCount.x - tilePadding) / 2, location.getRelativeY() + (tileCount.y - tilePadding) / 2, location.getZ());
        return new LocationArea(min, max);
    }

    public Point getTileCount() {
        return tileCount;
    }

    public Point getScreenTileCount() {
        return new Point(tileCount.x - tilePadding * 2, tileCount.y - tilePadding * 2);
    }

    public void clearCache() {
        bitmapLoader.clearCache();
    }

    public void addOnMapLocationChangeListener(OnMapLocationChangeListener listener) {
        onMapLocationChangeListeners.add(listener);
    }

    public void removeOnMapLocationChangeListener(OnMapLocationChangeListener listener) {
        onMapLocationChangeListeners.remove(listener);
    }

    interface OnMapLocationChangeListener {
        void onLocationChange(LocationPoint newLocation);
    }

}