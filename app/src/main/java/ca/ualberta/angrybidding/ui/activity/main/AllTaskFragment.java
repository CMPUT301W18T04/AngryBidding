package ca.ualberta.angrybidding.ui.activity.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;
import com.slouple.android.ResultRequest;
import com.slouple.android.Units;

import java.util.ArrayList;

import ca.ualberta.angrybidding.ElasticSearchTask;
import ca.ualberta.angrybidding.R;
import ca.ualberta.angrybidding.ui.activity.AddBidActivity;
import ca.ualberta.angrybidding.ui.activity.AddTaskActivity;
import ca.ualberta.angrybidding.ui.activity.EditTaskActivity;
import ca.ualberta.angrybidding.ui.fragment.TaskListFragment;
import ca.ualberta.angrybidding.ui.view.TaskView;

/**
 * Debug fragment for listing all tasks that is in the ElasticSearch
 */
public class AllTaskFragment extends TaskListFragment implements IMainFragment {
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;

    @Override
    public AppBarLayout getAppBarLayout(ViewGroup rootView, LayoutInflater inflater) {
        if (appBarLayout == null) {
            appBarLayout = (AppBarLayout) inflater.inflate(R.layout.all_task_fragment_toolbar, rootView, false);
        }
        return appBarLayout;

    }

    @Override
    public Toolbar getToolbar(ViewGroup rootView, LayoutInflater inflater) {
        if (toolbar == null) {
            toolbar = (Toolbar) getAppBarLayout(rootView, inflater).findViewById(R.id.all_task_fragment_toolbar);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup view, Bundle savedInstanceState){
        View layout = super.onCreateView(inflater, view, savedInstanceState);
        getContext().addResultRequest(new ResultRequest(AddTaskActivity.REQUEST_CODE) {
            @Override
            public void onResult(Intent intent) {
                refresh();
            }

            @Override
            public void onCancel(Intent intent) {
            }
        });

        getContext().addResultRequest(new ResultRequest(EditTaskActivity.REQUEST_CODE) {
            @Override
            public void onResult(Intent intent) {
                refresh();
            }

            @Override
            public void onCancel(Intent intent) {
            }
        });

        getContext().addResultRequest(new ResultRequest(AddBidActivity.REQUEST_CODE) {
            @Override
            public void onResult(Intent intent) {
                refresh();
            }

            @Override
            public void onCancel(Intent intent) {
            }
        });
        return layout;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    /**
     * Lists all tasks in the ElasticSearch
     */
    @Override
    public void onRefresh() {
        super.onRefresh();
        ElasticSearchTask.listTask(getContext(), new ElasticSearchTask.ListTaskListener() {
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
