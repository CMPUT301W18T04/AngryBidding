package ca.ualberta.angrybidding.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.google.gson.Gson;
import com.slouple.android.Pointer;
import com.slouple.android.widget.adapter.DummyAdapter;
import com.slouple.util.Listener;

import java.util.ArrayList;

import ca.ualberta.angrybidding.R;
import ca.ualberta.angrybidding.map.LocationMarker;
import ca.ualberta.angrybidding.map.LocationPoint;
import ca.ualberta.angrybidding.map.MapObjectContainer;
import ca.ualberta.angrybidding.map.TouchableMapView;
import ca.ualberta.angrybidding.map.osm.NominatimPlace;

public class PickLocationActivity extends AngryBiddingActivity {

    public static final int REQUEST_CODE = 10010;

    private TouchableMapView mapView;
    private MapObjectContainer mapObjectContainer;

    private CoordinatorLayout appBarMain;
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private EditText searchEditText;

    private FloatingActionButton floatingActionButton;

    private RecyclerView searchRecycler;
    private View searchCover;
    private ArrayList<NominatimPlace> searchPlaces = new ArrayList<>();
    private LocationMarker searchLocationMarker;

    @Override
    protected int getColorID() {
        return R.color.colorPrimaryDark;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_location);

        appBarMain = (CoordinatorLayout) findViewById(R.id.pickLocationCoordinatorLayout);

        mapView = findViewById(R.id.pickLocationMapView);
        mapObjectContainer = findViewById(R.id.pickLocationMapObjectContainer);

        mapView.setOnHoldListener(new TouchableMapView.OnHoldListener() {
            @Override
            public void onHold(Pointer pointer) {
                Point difference = new Point(mapView.getWidth() / 2 - pointer.getX(), mapView.getHeight() / 2 - pointer.getY());
                double dMapX = -difference.x / (mapView.getMap().getTileSize() * mapView.getTotalScale());
                double dMapY = -difference.y / (mapView.getMap().getTileSize() * mapView.getTotalScale());
                LocationPoint locationPoint = mapView.getLocation();
                LocationPoint newLocation = new LocationPoint(locationPoint.getX(), locationPoint.getY(), locationPoint.getZ());
                newLocation.setX(locationPoint.getX() + dMapX);
                newLocation.setY(locationPoint.getY() + dMapY);
                setLocationMarker(newLocation, false);
            }
        });

        appBarLayout = (AppBarLayout) getLayoutInflater().inflate(R.layout.pick_location_activity_toolbar, appBarMain, false);
        appBarMain.addView(appBarLayout);
        final Toolbar toolbar = getToolbar(appBarMain, getLayoutInflater());
        setSupportActionBar(toolbar);

        final CoordinatorLayout.LayoutParams params = new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.MATCH_PARENT);
        ViewTreeObserver observer = toolbar.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int height = toolbar.getHeight();
                CoordinatorLayout.LayoutParams appBarParams = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
                height += appBarParams.topMargin + appBarParams.bottomMargin;
                params.setMargins(0, height, 0, 0);
                toolbar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        searchCover = findViewById(R.id.pickLocationSearchCover);
        searchCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSearch();
            }
        });

        searchRecycler = (RecyclerView) findViewById(R.id.pickLocationSearchRecycler);
        searchRecycler.setLayoutManager(new LinearLayoutManager(this));
        searchRecycler.setAdapter(new DummyAdapter<NominatimPlace, TextView>(searchPlaces){
            @Override
            public TextView createView(int viewType) {
                TextView textView = new TextView(PickLocationActivity.this);
                textView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                textView.setPadding(40, 40, 40, 40);
                textView.setTextSize(18);
                textView.setBackgroundColor(ContextCompat.getColor(PickLocationActivity.this, R.color.colorContentBackground));
                return textView;
            }

            @Override
            public void onBindView(TextView view, final NominatimPlace item) {
                view.setText(item.getDisplayName());
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hideSearch();
                        Point screenTileCount = mapView.getScreenTileCount();
                        item.getLocationPoint().setZ(item.getZ(screenTileCount.x, screenTileCount.y, mapView.getMinZoom(), mapView.getMap().getMaxZ()));
                        setLocationMarker(item.getLocationPoint(), true);
                    }
                });
            }

            @Override
            public void onReachingLastItem(int position) {

            }
        });
        searchRecycler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSearch();
            }
        });

        floatingActionButton = findViewById(R.id.pickLocationActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(searchLocationMarker == null){
                    Snackbar.make(v, R.string.noLocationSelected, Snackbar.LENGTH_LONG).show();
                }else{
                    Intent intent = new Intent();
                    intent.putExtra("locationPoint", new Gson().toJson(searchLocationMarker.getLocation()));
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });


    }

    public void setLocationMarker(LocationPoint locationPoint, boolean moveToLocation){
        if(moveToLocation) {
            mapView.setLocation(locationPoint);
        }
        if(searchLocationMarker != null){
            mapObjectContainer.removeView(searchLocationMarker);
        }
        searchLocationMarker = new LocationMarker(PickLocationActivity.this, locationPoint, false);
        mapObjectContainer.addView(searchLocationMarker);
    }

    public Toolbar getToolbar(ViewGroup rootView, LayoutInflater inflater) {
        if (toolbar == null) {
            toolbar = appBarLayout.findViewById(R.id.pickLocationToolbar);
        }
        searchEditText = (EditText) toolbar.findViewById(R.id.pickLocationToolbarSearch);
        searchEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showSearch();
                }
            }
        });
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String query = searchEditText.getText().toString();
                    searchPlaces.clear();
                    searchRecycler.getAdapter().notifyDataSetChanged();
                    InputMethodManager inputManager = (InputMethodManager) PickLocationActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    View focused = PickLocationActivity.this.getCurrentFocus();
                    if(focused != null) {
                        inputManager.hideSoftInputFromWindow(focused.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }

                    if(query.length() == 0){
                        hideSearch();
                        return true;
                    }

                    searchEditText.setEnabled(false);
                    NominatimPlace.search(query, mapView.getScreenBound(), PickLocationActivity.this, new Listener<ArrayList<NominatimPlace>>() {
                        @Override
                        public void onSuccess(ArrayList<NominatimPlace> newPlaces) {
                            searchPlaces.addAll(newPlaces);
                            searchRecycler.getAdapter().notifyDataSetChanged();
                            searchEditText.setEnabled(true);
                        }

                        @Override
                        public void onError(String error) {
                            searchEditText.setEnabled(false);
                        }
                    });
                    return true;
                }
                return false;
            }
        });
        return toolbar;
    }

    public void showSearch(){
        searchRecycler.setVisibility(View.VISIBLE);
        searchCover.setVisibility(View.VISIBLE);
    }

    public void hideSearch(){
        searchEditText.clearFocus();
        searchRecycler.setVisibility(View.GONE);
        searchCover.setVisibility(View.GONE);

    }
}
