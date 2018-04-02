package ca.ualberta.angrybidding.map;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.TextView;

import com.slouple.postphere.R;

public class SavedLocationView extends CardView {
    private TextView titleTextView;
    private ScalableMapView mapView;
    private Button exploreButton;
    private Button deleteButton;

    public SavedLocationView(Context context) {
        this(context, null);
    }

    public SavedLocationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SavedLocationView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (attrs == null) {
            init();
        } else {
            TypedArray ta = getContext().obtainStyledAttributes(attrs, null, defStyle, 0);

            ta.recycle();
            init();
        }
    }

    private void init() {
        inflate(getContext(), R.layout.view_saved_location, this);
        titleTextView = (TextView) findViewById(R.id.savedLocationTitle);
        //popupMenuButton = (PopupMenuButton) findViewById(R.id.savedLocationPopupMenuButton);
        mapView = (ScalableMapView) findViewById(R.id.savedLocationMapView);
        exploreButton = (Button) findViewById(R.id.savedLocationExploreButton);
        deleteButton = (Button) findViewById(R.id.savedLocationDeleteButton);
    }

    public void setTitle(String title){
        titleTextView.setText(title);
    }

    public String getTitle(){
        return (String) titleTextView.getText();
    }

    public Button getExploreButton(){
        return exploreButton;
    }

    public Button getDeleteButton(){
        return deleteButton;
    }

    public void setLocation(LocationPoint locationPoint){
        mapView.setLocation(locationPoint);
    }

    public LocationPoint getLocation(){
        return mapView.getLocation();
    }
}
