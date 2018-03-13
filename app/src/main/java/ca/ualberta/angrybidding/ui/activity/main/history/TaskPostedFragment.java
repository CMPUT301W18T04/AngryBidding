package ca.ualberta.angrybidding.ui.activity.main.history;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;
import com.slouple.android.Units;

import ca.ualberta.angrybidding.ElasticSearchTask;
import ca.ualberta.angrybidding.ElasticSearchUser;
import ca.ualberta.angrybidding.ui.fragment.TaskListFragment;
import ca.ualberta.angrybidding.ui.view.TaskView;

import java.util.ArrayList;

public class TaskPostedFragment extends TaskListFragment {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

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

            @Override
            public void onError(VolleyError error) {
                Log.e("TaskPostedFragment", error.getMessage(), error);
            }
        });
    }

    @Override
    protected TaskView createTaskView() {
        TaskView taskView = super.createTaskView();
        LinearLayout.LayoutParams bottomMargin = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        bottomMargin.setMargins(0, 0, 0, Units.dpToPX(20, getContext()));
        taskView.setLayoutParams(bottomMargin);
        return taskView;
    }
}