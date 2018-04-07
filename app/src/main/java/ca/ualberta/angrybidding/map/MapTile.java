package ca.ualberta.angrybidding.map;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;

import com.slouple.android.BitmapLoader;
import com.slouple.android.BitmapQueueEntry;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

public class MapTile {
    public enum LoadState {Initialized, Loading, Loaded, Destroyed}

    private static final Bitmap DEFAULT_TILE_BITMAP = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888);

    private MapTileCoordinate coordinate;

    private URL url;
    private Map map;

    private Bitmap bitmap;
    private BitmapLoader bitmapLoader;
    private BitmapQueueEntry entry;

    private LoadState state;

    public MapTile(Map map, int x, int y, int z, final BitmapLoader bitmapLoader) {
        this(map, new MapTileCoordinate(x, y, z), bitmapLoader);
    }

    public MapTile(Map map, MapTileCoordinate coordinate, final BitmapLoader bitmapLoader) {
        this.coordinate = coordinate;
        int x = coordinate.getX();
        int y = coordinate.getY();
        int z = coordinate.getZ();

        bitmap = DEFAULT_TILE_BITMAP;
        int maxTile = (int) Math.pow(2, z);

        int relativeX = x;
        if (x > maxTile - 1) {
            relativeX = (x % maxTile);
        } else if (x < 0) {
            relativeX = maxTile - 1 + ((x + 1) % maxTile);
        }

        int relativeY = y;
        if (y > maxTile - 1) {
            relativeY = (y % maxTile);
        } else if (y < 0) {
            relativeY = maxTile - 1 + ((y + 1) % maxTile);
        }

        this.map = map;

        try {
            this.url = new URL(map.getURL().replace("{x}", String.valueOf(relativeX))
                    .replace("{y}", String.valueOf(relativeY))
                    .replace("{z}", String.valueOf(z)));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        this.bitmapLoader = bitmapLoader;

        state = LoadState.Initialized;
    }

    public void load() {
        if (state != LoadState.Initialized) {
            return;
        }
        if (entry == null) {
            Calendar expireTime = Calendar.getInstance();
            expireTime.add(Calendar.HOUR, 24);
            entry = new BitmapQueueEntry(url.toString(), expireTime, true, null, Bitmap.Config.RGB_565, null) {
                Handler handler;
                Runnable runnable;

                @Override
                public void onLoad(Bitmap bitmap, File cacheFile) {
                    setBitmap(bitmap);
                }

                @Override
                public void onLoadError(Throwable e) {
                    handler = new Handler(Looper.getMainLooper());
                    runnable = new Runnable() {
                        @Override
                        public void run() {
                            if (entry != null) {
                                load();
                            }
                        }
                    };
                    handler.postDelayed(runnable, 1000);
                }
            };
        }
        bitmapLoader.addToQueue(entry);
        state = LoadState.Loading;
    }

    public int getX() {
        return this.coordinate.getX();
    }

    public int getY() {
        return this.coordinate.getY();
    }

    public int getZ() {
        return this.coordinate.getZ();
    }

    public MapTileCoordinate getCoordinate() {
        return coordinate;
    }

    public Map getMap() {
        return this.map;
    }

    public URL getUrl() {
        return url;
    }

    public void destroy() {
        bitmapLoader.removeFromQueue(entry);
        state = LoadState.Destroyed;
    }

    public void cancelLoad() {
        if (state == LoadState.Loading) {
            state = LoadState.Initialized;
            bitmapLoader.removeFromQueue(entry);
        }
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        state = LoadState.Loaded;
        onLoad();
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void onLoad() {
    }

    public LoadState getState() {
        return state;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof MapTile) {
            return this.getCoordinate().equals(((MapTile) other).getCoordinate()) &&
                    this.getMap().equals(((MapTile) other).getMap());
        }
        return false;
    }

}
