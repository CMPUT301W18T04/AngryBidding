package ca.ualberta.angrybidding.ui.activity.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.android.volley.VolleyError;
import com.github.clans.fab.FloatingActionButton;
import com.slouple.android.AdvancedFragment;
import com.slouple.android.PermissionRequest;
import com.slouple.android.PermissionRequestListener;

import java.util.ArrayList;

import ca.ualberta.angrybidding.ElasticSearchTask;
import ca.ualberta.angrybidding.R;
import ca.ualberta.angrybidding.Task;
import ca.ualberta.angrybidding.map.LocationPoint;
import ca.ualberta.angrybidding.map.MapObjectContainer;
import ca.ualberta.angrybidding.map.TouchableMapView;
import ca.ualberta.angrybidding.map.UserLocationMarker;
import ca.ualberta.angrybidding.ui.view.TaskMapObject;
import ca.ualberta.angrybidding.ui.view.TaskMapObjectContainer;

/**
 * Fragment to display all tasks on a map around user
 */
public class NearbyFragment extends AdvancedFragment implements IMainFragment {
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;

    private TouchableMapView mapView;
    private MapObjectContainer mapObjectContainer;
    private TaskMapObjectContainer taskMapObjectContainer;

    protected int currentLocationPermissionRequestCode;
    protected PermissionRequest currentLocationPermissionRequest;

    protected UserLocationMarker userLocationMarker;

    private NearbyTaskLoadingThread loadingThread;

    @Override
    public AppBarLayout getAppBarLayout(ViewGroup rootView, LayoutInflater inflater) {
        if (appBarLayout == null) {
            appBarLayout = (AppBarLayout) inflater.inflate(R.layout.nearby_fragment_toolbar, rootView, false);
        }
        return appBarLayout;

    }

    @Override
    public Toolbar getToolbar(ViewGroup rootView, LayoutInflater inflater) {
        if (toolbar == null) {
            toolbar = (Toolbar) getAppBarLayout(rootView, inflater).findViewById(R.id.nearby_fragment_toolbar);
        }
        return toolbar;
    }

    @Override
    public void onActionBarAdded(ActionBar actionBar) {

    }

    @Override
    public boolean shouldOffsetForToolbar() {
        return true;
    }

    @Override
    public void onVisible() {

    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final FrameLayout layout = (FrameLayout) inflater.inflate(R.layout.fragment_nearby, container, false);
        mapView = layout.findViewById(R.id.nearbyMapView);
        taskMapObjectContainer = layout.findViewById(R.id.nearbyTaskMapObjectContainer);
        mapObjectContainer = layout.findViewById(R.id.nearbyMapObjectContainer);

        userLocationMarker = new UserLocationMarker(getActivity());
        mapObjectContainer.addView(userLocationMarker);

        //Current Location Button
        final FloatingActionButton currentLocationButton = (FloatingActionButton) layout.findViewById(R.id.nearbyCurrentLocation);
        currentLocationPermissionRequest = userLocationMarker.getPermissionRequest(new PermissionRequestListener() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onGranted() {
                userLocationMarker.addLocationListener();
                currentLocationButton.callOnClick();
            }

            @Override
            public void onDenied(boolean shouldShowRationale) {
                Snackbar.make(layout, R.string.currentLocationNeedPermission, Snackbar.LENGTH_LONG)
                        .setAction(R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                            }
                        }).show();
            }
        });
        currentLocationPermissionRequestCode = getContext().registerPermissionRequest(currentLocationPermissionRequest);

        currentLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!userLocationMarker.hasPermission()){
                    getContext().submitPermissionRequest(currentLocationPermissionRequestCode);
                }else{
                    zoomOnCurrentLocation(true, 14);
                }
            }
        });

        loadingThread = new NearbyTaskLoadingThread();
        loadingThread.start();
        return layout;
    }

    private void zoomOnCurrentLocation(boolean showError, int z){
        if (userLocationMarker.isAvailable(showError)) {
            LocationPoint lastLocationPoint = userLocationMarker.getLastLocationPoint();
            lastLocationPoint.setZ(z);
            mapView.setLocation(lastLocationPoint);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("PostFragment", "onPause");
        getUserLocationMarker().removeLocationListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("PostFragment", "onResume");
        if(getUserLocationMarker().hasPermission()){
            getUserLocationMarker().addLocationListener();
        }
    }

    @Override
    public void onDestroyView(){
        Log.d("MapFragment", "onDestroyView");
        loadingThread.interrupt();
        getUserLocationMarker().stop();
        super.onDestroyView();
    }

    public UserLocationMarker getUserLocationMarker(){
        return userLocationMarker;
    }

    private class NearbyTaskLoadingThread extends Thread{
        @Override
        public void run(){
            while(!interrupted()){
                ElasticSearchTask.listTaskByLocationArea(getContext(), mapView.getScreenBound(), new Task.Status[]{Task.Status.REQUESTED, Task.Status.BIDDED}, new ElasticSearchTask.ListTaskListener() {
                    @Override
                    public void onResult(ArrayList<ElasticSearchTask> tasks) {
                        for(ElasticSearchTask task: tasks){
                            taskMapObjectContainer.addTask(task);
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {

                    }
                });
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    }
}
