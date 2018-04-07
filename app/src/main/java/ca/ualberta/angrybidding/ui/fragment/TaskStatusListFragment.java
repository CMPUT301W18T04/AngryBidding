package ca.ualberta.angrybidding.ui.fragment;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;

import com.slouple.android.widget.adapter.DummyAdapter;

import ca.ualberta.angrybidding.ElasticSearchTask;
import ca.ualberta.angrybidding.R;
import ca.ualberta.angrybidding.ui.view.TaskView;

/**
 * Fragment with task bar of status
 */
public class TaskStatusListFragment extends TaskListFragment {
    protected Spinner spinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        FrameLayout fl = (FrameLayout) inflater.inflate(R.layout.fragment_task_status_list, container, false);
        fl.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorBackground));

        swipeRefreshLayout = (SwipeRefreshLayout) fl.findViewById(R.id.taskListSwipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                TaskStatusListFragment.this.onRefresh();
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
                TaskStatusListFragment.this.onBindView(view, item);
            }

            @Override
            public void onReachingLastItem(int i) {

            }

        });

        spinner = fl.findViewById(R.id.taskListStatusSpinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TaskStatusListFragment.super.refresh();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                TaskStatusListFragment.super.refresh();
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.taskStatusArray, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        refresh();
        return fl;
    }

    public String getSelectedSpinnerItem() {
        return spinner.getSelectedItem().toString();
    }
}


