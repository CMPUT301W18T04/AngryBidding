package ca.ualberta.angrybidding.ui.activity.main.history;

import android.os.Bundle;
import android.util.Log;

import com.android.volley.VolleyError;

import ca.ualberta.angrybidding.ElasticSearchTask;
import ca.ualberta.angrybidding.ElasticSearchUser;
import ca.ualberta.angrybidding.elasticsearch.ElasticSearchResponseListener;

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
}

