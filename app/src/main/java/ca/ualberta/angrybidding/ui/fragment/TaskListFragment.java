package ca.ualberta.angrybidding.ui.fragment;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
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
import ca.ualberta.angrybidding.ElasticSearchUser;
import ca.ualberta.angrybidding.R;
import ca.ualberta.angrybidding.ui.view.TaskView;

/**
 * Fragment with RecyclerView and SwipeRefreshLayout for list of tasks
 */
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

    /**
     * Refreshes the list
     */
    public void refresh() {
        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
            onRefresh();
        }
    }

    /**
     * Remove existing tasks
     */
    public void clear() {
        tasks.clear();
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    /**
     * Child classes can override this to there own list generation
     */
    public void onRefresh() {
        clear();
    }

    /**
     * Set refreshing to false for SwipeRefreshLayout
     */
    public void finishRefresh() {
        swipeRefreshLayout.setRefreshing(false);
    }

    /**
     * Check if there is a duplicate in the list
     * @param task Task to check
     * @return Does duplicate exist
     */
    public boolean hasDuplicate(ElasticSearchTask task) {
        for (ElasticSearchTask t : tasks) {
            if (t.getID().equals(task.getID())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Add task to the list
     * @param task Task to add
     */
    public void addTask(ElasticSearchTask task) {
        if (!hasDuplicate(task)) {
            tasks.add(task);
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    /**
     * Removes task from the list
     * @param task Task to remove
     */
    public void removeTask(ElasticSearchTask task) {
        if (tasks.remove(task)) {
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    /**
     * Bind a task to a TaskView
     * @param taskView TaskView to bind with
     * @param task Task to Bind with
     */
    protected void onBindView(TaskView taskView, final ElasticSearchTask task) {

        taskView.setTask(task);
        taskView.usePopupMenu(ElasticSearchUser.getMainUser(getContext()), new TaskView.OnTaskChangeListener() {

            @Override
            public void onDelete() {
                refresh();
            }

            @Override
            public void onEdit() {
                refresh();
            }
        });
    }

    /**
     * Creates new TaskView
     * @return newly created TaskView
     */
    protected TaskView createTaskView() {
        TaskView taskView = new TaskView(getContext());
        return taskView;
    }

}


