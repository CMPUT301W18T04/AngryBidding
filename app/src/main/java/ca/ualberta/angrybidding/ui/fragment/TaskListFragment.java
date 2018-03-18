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

/*
This class is the task list fragment which is the fragment of the bid list
 */
public class TaskListFragment extends AdvancedFragment {
    protected SwipeRefreshLayout swipeRefreshLayout;
    protected RecyclerView recyclerView;
    protected ArrayList<ElasticSearchTask> tasks = new ArrayList<>();

    /*
    This method is used to when the views are created, and it will inflate views
     */
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
    /*
    refresh() will refresh the bid list when it's called
     */
    public void refresh() {
        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
            onRefresh();
        }
    }

    /*
    clear() will clear the task ArrayList
     */
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

    /*
    This method will check if there's a duplicated task in the list
     */
    public boolean hasDuplicate(ElasticSearchTask task) {
        for (ElasticSearchTask t : tasks) {
            if (t.getID().equals(task.getID())) {
                return true;
            }
        }
        return false;
    }

    /*
    addTask will add a new task to the ArrayList
     */
    public void addTask(ElasticSearchTask task) {
        if (!hasDuplicate(task)) {
            tasks.add(task);
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    /*
    removeTask will delete a selected task from the ArrayList
     */
    public void removeTask(ElasticSearchTask task) {
        if (tasks.remove(task)) {
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }
    /*
    onBindView will bind a task to a taskView
     */
    protected void onBindView(TaskView taskView, final ElasticSearchTask task) {

        taskView.setTask(task);
        taskView.usePopupMenu(ElasticSearchUser.getMainUser(getContext()), new TaskView.OnTaskChangeListener() {

            @Override
            public void onDelete() {
                refresh();
                //removeTask(task);
            }

            @Override
            public void onEdit() {

            }
        });
    }


    /*
    This is the method to create a new taskView
     */
    protected TaskView createTaskView() {
        TaskView taskView = new TaskView(getContext());
        return taskView;
    }

}


