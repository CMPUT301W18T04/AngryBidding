package ca.ualberta.angrybidding.map;

import android.content.Context;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.slouple.android.Pointer;
import com.slouple.android.Units;
import com.slouple.android.input.TapDetector;

import ca.ualberta.angrybidding.R;

public class LocationMarker extends MapObject {
    private FrameLayout container;
    private ImageView icon;

    public LocationMarker(Context context, LocationPoint location, boolean touchable) {
        super(context, location);

        container = (FrameLayout) LayoutInflater.from(context).inflate(R.layout.view_location_marker, null, false);
        addView(container);
        icon = (ImageView) container.findViewById(R.id.locationMarkerIcon);

        setOffset(new Point(Units.dpToPX(25, getContext()), Units.dpToPX(50, getContext())));

        if(touchable) {
            final MapObjectTouchListener iconListener = new MapObjectTouchListener(getContext(), this, icon);
            new TapDetector(iconListener) {
                @Override
                public void onSingleTap(Pointer pointer) {
                    setVisibility(View.GONE);
                }

                @Override
                public void onDoubleTap(Pointer pointer) {
                    setVisibility(View.GONE);
                }

                @Override
                public void onHold(Pointer pointer) {
                    setVisibility(View.GONE);
                }
            };
            icon.setOnTouchListener(iconListener);
        }
    }

}
