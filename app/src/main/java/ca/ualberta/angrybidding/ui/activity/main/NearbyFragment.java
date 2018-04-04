package ca.ualberta.angrybidding.ui.activity.main;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.android.volley.VolleyError;
import com.slouple.android.AdvancedFragment;

import java.util.ArrayList;

import ca.ualberta.angrybidding.ElasticSearchTask;
import ca.ualberta.angrybidding.R;
import ca.ualberta.angrybidding.Task;
import ca.ualberta.angrybidding.map.MapObjectContainer;
import ca.ualberta.angrybidding.map.TouchableMapView;
import ca.ualberta.angrybidding.ui.view.TaskMapObject;
import ca.ualberta.angrybidding.ui.view.TaskMapObjectContainer;

/**
 * Fragment to display all tasks on a map around user
 */
public class NearbyFragment extends AdvancedFragment implements IMainFragment {
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;

    private TouchableMapView mapView;
    private TaskMapObjectContainer taskMapObjectContainer;

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
        FrameLayout layout = (FrameLayout) inflater.inflate(R.layout.fragment_nearby, container, false);
        mapView = layout.findViewById(R.id.nearbyMapView);
        taskMapObjectContainer = layout.findViewById(R.id.nearbyMapObjectContainer);
        loadingThread = new NearbyTaskLoadingThread();
        loadingThread.start();
        taskMapObjectContainer.bringToFront();
        return layout;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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
