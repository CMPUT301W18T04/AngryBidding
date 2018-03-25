package ca.ualberta.angrybidding.ui.activity.main.history;

import android.os.Bundle;
import android.util.Log;

import com.android.volley.VolleyError;

import java.util.ArrayList;

import ca.ualberta.angrybidding.ElasticSearchTask;
import ca.ualberta.angrybidding.ElasticSearchUser;
import ca.ualberta.angrybidding.ui.fragment.TaskListFragment;

/**
 * The TaskPostedFragment in HistoryFragment, and it will deal with the provided tasks.
 * Extends TaskListFragment for UI and basic functionality
 */
public class TaskProvidedFragment extends TaskListFragment {
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
        ElasticSearchTask.listTaskByUser(getContext(), ElasticSearchUser.getMainUser(getContext()).getUsername(), new ElasticSearchTask.ListTaskListener() {
            @Override
            public void onResult(ArrayList<ElasticSearchTask> newTasks) {
                tasks.addAll(newTasks);
                recyclerView.getAdapter().notifyDataSetChanged();
                finishRefresh();
            }

            /*
             * Show message when a error occurs
             */
            @Override
            public void onError(VolleyError error) {
                Log.e("TaskPostedFragment", error.getMessage(), error);
            }
        });
    }
}
