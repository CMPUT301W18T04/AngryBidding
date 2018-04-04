package ca.ualberta.angrybidding.ui.activity.main.history;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.android.volley.VolleyError;

import java.util.ArrayList;

import ca.ualberta.angrybidding.ElasticSearchTask;
import ca.ualberta.angrybidding.ElasticSearchUser;
import ca.ualberta.angrybidding.R;
import ca.ualberta.angrybidding.Task;
import ca.ualberta.angrybidding.ui.fragment.TaskStatusListFragment;

/**
 * The TaskPostedFragment in HistoryFragment, and it will deal with the provided tasks.
 * Extends TaskListFragment for UI and basic functionality
 */
public class TaskProvidedFragment extends TaskStatusListFragment {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.taskProvidedStatusArray, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        return view;
    }

    /**
     * onRefresh() will update the task list and get the task list of current
     * user from the elastic search server, and add the tasks to the task ArrayList
     */
    @Override
    public void onRefresh() {
        super.onRefresh();
        Task.Status spinnerStatus = Task.Status.getStatus(super.getSelectedSpinnerItem());
        ElasticSearchTask.ListTaskListener listener = new ElasticSearchTask.ListTaskListener() {
            @Override
            public void onResult(ArrayList<ElasticSearchTask> newTasks) {
                tasks.addAll(newTasks);
                recyclerView.getAdapter().notifyDataSetChanged();
                finishRefresh();
            }

            @Override
            public void onError(VolleyError error) {
                Log.e("TaskProvidedFragment", error.getMessage(), error);
            }
        };
        if (spinnerStatus == Task.Status.BIDDED) {
            ElasticSearchTask.listTaskByBiddedUser(getContext(), ElasticSearchUser.getMainUser(getContext()).getUsername(), new Task.Status[]{spinnerStatus}, listener);
        } else {
            ElasticSearchTask.listTaskByChosenUser(getContext(), ElasticSearchUser.getMainUser(getContext()).getUsername(), new Task.Status[]{spinnerStatus}, listener);
        }

    }
}
