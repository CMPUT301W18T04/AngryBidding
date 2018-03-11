package ca.ualberta.angrybidding.ui.activity.main.history;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.slouple.android.AdvancedFragment;
import com.slouple.android.widget.adapter.DummyAdapter;

import java.util.ArrayList;

import ca.ualberta.angrybidding.ElasticSearchTask;
import ca.ualberta.angrybidding.R;

public class TaskListFragment extends AdvancedFragment {
    protected SwipeRefreshLayout swipeRefreshLayout;
    protected RecyclerView recyclerView;
    protected ArrayList<ElasticSearchTask> tasks = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FrameLayout fl = (FrameLayout) inflater.inflate(R.layout.fragment_task_list, container, false);
        fl.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorBackground));

        swipeRefreshLayout = (SwipeRefreshLayout) fl.findViewById(R.id.taskListSwipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                TaskListFragment.this.onRefresh();
            }
        });

        recyclerView = (RecyclerView) fl.findViewById(R.id.taskListRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new DummyAdapter<ElasticSearchTask, TaskView>(tasks) {
            @Override
            public TaskView createView(int viewType) {
                return createTaskView();
            }

            @Override
            public void onBindView(TaskView view, ElasticSearchTask item) {
                TaskListFragment.this.onBindView(view, item);
            }

            @Override
            public void onReachingLastItem(int i) {

            }

        });
        refresh();

        return fl;
    }

    public void refresh() {
        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
            onRefresh();
        }
    }

    public void clear() {
        tasks.clear();
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    public void onRefresh() {
        clear();
    }

    public void finishRefresh() {
        swipeRefreshLayout.setRefreshing(false);
    }

    public boolean hasDuplicate(ElasticSearchTask task) {
        for (ElasticSearchTask t : tasks) {
            if (t.getID().equals(task.getID())) {
                return true;
            }
        }
        return false;
    }

    public void addTask(ElasticSearchTask task) {
        if (!hasDuplicate(task)) {
            tasks.add(task);
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    public void removeTask(ElasticSearchTask task) {
        if (tasks.remove(task)) {
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    protected void onBindView(TaskView taskView, ElasticSearchTask task) {
        taskView.setTask(task);
    }

    protected TaskView createTaskView() {
        TaskView taskView = new TaskView(getContext());
        return taskView;
    }
}


