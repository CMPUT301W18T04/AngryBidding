package ca.ualberta.angrybidding.map;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.slouple.Momentum;
import com.slouple.android.Pointer;
import com.slouple.android.input.MultiTouchListener;
import com.slouple.android.input.ScaleDetector;
import com.slouple.android.input.TapDetector;

import java.util.concurrent.atomic.AtomicLong;

import ca.ualberta.angrybidding.R;

public class TouchableMapView extends ScalableMapView {
    private AtomicLong dMapX = new AtomicLong(0);
    private AtomicLong dMapY = new AtomicLong(0);
    private Handler movementUpdateHandler = new Handler();
    private OnHoldListener onHoldListener;

    private boolean zoomInCenter = false;

    private boolean touchable = true;

    private Momentum scaleMomentum = new Momentum(0, new Momentum.MomentumListener() {
        @Override
        public void onVelocityChange(double velocity) {
        }

        @Override
        public void onDistanceChange(final double distance) {
            post(new Runnable() {
                @Override
                public void run() {
                    TouchableMapView.super.setZoom(distance);
                }
            });
        }
    });

    public TouchableMapView(Context context) {
        this(context, null);
    }

    public TouchableMapView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TouchableMapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (attrs == null) {
            init(true);
        } else {
            TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.TouchableMapView, defStyle, 0);
            boolean touchable = ta.getBoolean(R.styleable.TouchableMapView_touchable, true);
            ta.recycle();
            init(touchable);
        }
    }

    private void init(boolean touchable) {
        this.touchable = touchable;
        scaleMomentum.setFrictionMode(Momentum.FrictionMode.MULTIPLICATIVE);
        long delay = 10;
        double baseFriction = 0.3;
        scaleMomentum.setListenerDelay(delay);
        scaleMomentum.setFriction(Math.pow(baseFriction, 1.0 / (100.0 / delay)));
        scaleMomentum.setVelocityLimit(0.02);
    }

    public TouchableMapView(Context context, Map map, LocationPoint location, String bitmapLoaderName, float baseScale, boolean debugView) {
        super(context, map, location, bitmapLoaderName, 3, baseScale, debugView);
        init(true);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("super", super.onSaveInstanceState());
        bundle.putBoolean("touchable", touchable);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            touchable = bundle.getBoolean("touchable");
            super.onRestoreInstanceState(bundle.getParcelable("super"));
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    public void setTouchable(boolean touchable) {
        this.touchable = touchable;
    }

    public boolean isTouchable() {
        return touchable;
    }

    public void setZoomInCenter(boolean zoomInCenter) {
        this.zoomInCenter = zoomInCenter;
    }

    public boolean isZoomInCenter() {
        return zoomInCenter;
    }


    @Override
    protected void drawDebug(Canvas canvas) {
        super.drawDebug(canvas);
        debugPaint.setColor(Color.argb(100, 0, 0, 0));
        canvas.drawText("Avg Dis x: " + touchListener.getAverageDistance().x, getWidth() / 2, getHeight() / 2 + 300, debugPaint);
        canvas.drawText("Avg Dis y: " + touchListener.getAverageDistance().y, getWidth() / 2, getHeight() / 2 + 325, debugPaint);
        canvas.drawText("Avg Raw Dis x: " + touchListener.getAverageRawDistance().x, getWidth() / 2, getHeight() / 2 + 350, debugPaint);
        canvas.drawText("Avg Raw Dis y: " + touchListener.getAverageRawDistance().y, getWidth() / 2, getHeight() / 2 + 375, debugPaint);
        canvas.drawText("Pointer Size: " + touchListener.getPointers().size(), getWidth() / 2, getHeight() / 2 + 400, debugPaint);

        int offset = 0;
        for (Pointer pointer : touchListener.getPointers()) {
            canvas.drawText("Pointer ID: " + pointer.getPointerID(), getWidth() / 2, getHeight() / 2 - offset, debugPaint);
            offset += 25;
            canvas.drawText("Pointer Distance: " + pointer.getDistance().x + " " + pointer.getDistance().y, getWidth() / 2, getHeight() / 2 - offset, debugPaint);
            offset += 25;
            canvas.drawText("Pointer Raw Distance: " + pointer.getRawDistance().x + " " + pointer.getRawDistance().y, getWidth() / 2, getHeight() / 2 - offset, debugPaint);
            offset += 25;
        }
    }

    public void updateMovement(Point distance) {
        double dMapX = -distance.x / (getMap().getTileSize() * getTotalScale());
        double dMapY = -distance.y / (getMap().getTileSize() * getTotalScale());
        LocationPoint newLocation = new LocationPoint(getLocation().getX(), getLocation().getY(), getLocation().getZ());
        newLocation.setX(getLocation().getX() + dMapX);
        newLocation.setY(getLocation().getY() + dMapY);
        setLocation(newLocation);
    }

    protected MultiTouchListener touchListener = new MultiTouchListener(getContext(), this) {

        @Override
        public boolean onMove(Point distance) {
            updateMovement(getAverageRawDistance());
            return true;
        }
    };

    protected TapDetector tapDetector = new TapDetector(touchListener) {
        @Override
        public void onSingleTap(Pointer pointer) {
            Log.d("TouchableMapView", "SingleTap x: " + pointer.getX() + " y: " + pointer.getY());
            setZoom(getZoom() - 2);
        }

        @Override
        public void onDoubleTap(Pointer pointer) {
            Log.d("TouchableMapView", "DoubleTap x: " + pointer.getX() + " y: " + pointer.getY());
            if (!isZoomInCenter()) {
                Point difference = new Point(getWidth() / 2 - pointer.getX(), getHeight() / 2 - pointer.getY());
                updateMovement(difference);
            }
            setZoom(getZoom() + 2);
        }

        @Override
        public void onHold(Pointer pointer) {
            Log.d("TouchableMapView", "Hold x: " + pointer.getX() + " y: " + pointer.getY());
            if (onHoldListener != null) {
                onHoldListener.onHold(pointer);
            }
        }
    };

    protected ScaleDetector scaleDetector = new ScaleDetector(touchListener) {
        @Override
        public boolean onScaleBegin() {
            Log.d("TouchableMapView", "onScaleBegin");
            //scaleMomentum.beginDistanceUpdate(getZoom());
            return false;
        }

        @Override
        public boolean onScaleEnd() {
            Log.d("TouchableMapView", "onScaleEnd");
            //scaleMomentum.endDistanceUpdate();
            return false;
        }

        @Override
        public boolean onScale(double factor) {
            factor = Math.max(0.9f, Math.min(factor, 1.1f));
            setZoom(getZoomInt() - 1 + getZoomScale() * factor);
            //scaleMomentum.updateDistance(getZoom());
            return true;
        }
    };

    @Override
    public void setZoom(double zoom) {
        super.setZoom(zoom);
        //scaleMomentum.endMomentum();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (touchable) {
            return touchListener.onTouchEvent(event);
        } else {
            return false;
        }
    }

    public void setOnHoldListener(OnHoldListener onHoldListener) {
        this.onHoldListener = onHoldListener;
    }

    public interface OnHoldListener {
        void onHold(Pointer pointer);
    }
}
