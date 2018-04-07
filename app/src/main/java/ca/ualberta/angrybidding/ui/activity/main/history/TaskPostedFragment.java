package ca.ualberta.angrybidding.ui.activity.main.history;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.slouple.android.Units;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import ca.ualberta.angrybidding.AngryBiddingApplication;
import ca.ualberta.angrybidding.ElasticSearchTask;
import ca.ualberta.angrybidding.ElasticSearchUser;
import ca.ualberta.angrybidding.Task;
import ca.ualberta.angrybidding.TaskCache;
import ca.ualberta.angrybidding.elasticsearch.UpdateResponseListener;
import ca.ualberta.angrybidding.ui.fragment.TaskStatusListFragment;
import ca.ualberta.angrybidding.ui.view.TaskView;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

/**
 * The TaskPostedFragment in HistoryFragment, and it will deal with the posted tasks.
 * Extends TaskListFragment for UI and basic functionality
 */
public class TaskPostedFragment extends TaskStatusListFragment {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * onRefresh() will update the task list and get the task list of current
     * user from the elastic search server, and add the tasks to the task ArrayList
     */
    @Override
    public void onRefresh() {
        super.onRefresh();
        final String spinnerStatus = super.getSelectedSpinnerItem();
        ElasticSearchTask.listTaskByUser(getContext(), ElasticSearchUser.getMainUser(getContext()).getUsername(), new Task.Status[]{Task.Status.getStatus(spinnerStatus)}, new ElasticSearchTask.ListTaskListener() {
            @Override
            public void onResult(ArrayList<ElasticSearchTask> newTasks) {
                if (AngryBiddingApplication.isOffline) {
                    if (Task.Status.getStatus(spinnerStatus) == Task.Status.REQUESTED) {
                        TaskCache.synchronizeWithElasticSearch(getContext(), newTasks, new ElasticSearchTask.ListTaskListener() {
                            @Override
                            public void onResult(ArrayList<ElasticSearchTask> tasks) {
                                onRefresh();
                            }

                            @Override
                            public void onError(VolleyError error) {

                            }
                        });
                        AngryBiddingApplication.isOffline = false;
                        return;
                    }
                }

                tasks.addAll(newTasks);
                recyclerView.getAdapter().notifyDataSetChanged();
                if (Task.Status.getStatus(spinnerStatus) == Task.Status.REQUESTED) {
                    TaskCache.saveToFile(getContext(), newTasks);
                }
                finishRefresh();
            }

            /*
             * Show message when a error occurs
             */
            @Override
            public void onError(VolleyError error) {
                Log.e("TaskPostedFragment", error.getMessage(), error);
                /*
                Set the offline variable to true every time the connection goes offline
                 */
                AngryBiddingApplication.isOffline = true;
                Toast.makeText(getContext(), "The connection is interrupted", Toast.LENGTH_SHORT).show();
                if (Task.Status.getStatus(spinnerStatus) == Task.Status.REQUESTED) {
                    ArrayList<ElasticSearchTask> newTasks = TaskCache.readFromFile(getContext());
                    if (newTasks != null) {
                        tasks.addAll(newTasks);
                    }
                    recyclerView.getAdapter().notifyDataSetChanged();
                }

                finishRefresh();
            }
        });
    }

    /**
     * Creates new TaskView with margin
     *
     * @return TaskView with margin
     */
    @Override
    protected TaskView createTaskView() {
        TaskView taskView = super.createTaskView();
        LinearLayout.LayoutParams bottomMargin = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        bottomMargin.setMargins(0, 0, 0, Units.dpToPX(20, getContext()));
        taskView.setLayoutParams(bottomMargin);
        return taskView;
    }

}