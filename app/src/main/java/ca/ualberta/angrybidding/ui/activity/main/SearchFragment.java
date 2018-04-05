package ca.ualberta.angrybidding.ui.activity.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.slouple.android.ResultRequest;
import com.slouple.android.Units;

import java.util.ArrayList;

import ca.ualberta.angrybidding.ElasticSearchTask;
import ca.ualberta.angrybidding.R;
import ca.ualberta.angrybidding.Task;
import ca.ualberta.angrybidding.ui.activity.AddBidActivity;
import ca.ualberta.angrybidding.ui.activity.AddTaskActivity;
import ca.ualberta.angrybidding.ui.activity.EditTaskActivity;
import ca.ualberta.angrybidding.ui.fragment.TaskListFragment;
import ca.ualberta.angrybidding.ui.view.TaskView;

/**
 * Fragment for searching task based on keywords
 */
public class SearchFragment extends TaskListFragment implements IMainFragment {
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;

    private EditText searchEditText;

    @Override
    public AppBarLayout getAppBarLayout(ViewGroup rootView, LayoutInflater inflater) {
        if (appBarLayout == null) {
            appBarLayout = (AppBarLayout) inflater.inflate(R.layout.search_fragment_toolbar, rootView, false);
        }
        return appBarLayout;

    }

    @Override
    public Toolbar getToolbar(ViewGroup rootView, LayoutInflater inflater) {
        if (toolbar == null) {
            toolbar = getAppBarLayout(rootView, inflater).findViewById(R.id.search_fragment_toolbar);
        }
        searchEditText = toolbar.findViewById(R.id.search_fragment_toolbar_search);
        //IME Listener
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //Clear results
                    tasks.clear();
                    recyclerView.getAdapter().notifyDataSetChanged();
                    InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    View focused = getContext().getCurrentFocus();
                    if (focused != null) {
                        inputManager.hideSoftInputFromWindow(focused.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }

                    if (getKeywords().length == 0) {
                        return true;
                    }

                    searchEditText.setEnabled(false);
                    search();
                    return true;
                }
                return false;
            }
        });
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
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    /**
     * Call search again when being refreshed
     */
    @Override
    public void onRefresh() {
        super.onRefresh();
        search();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        swipeRefreshLayout.setRefreshing(false);
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
        return view;
    }

    @Override
    protected TaskView createTaskView() {
        TaskView taskView = super.createTaskView();
        LinearLayout.LayoutParams bottomMargin = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        bottomMargin.setMargins(0, 0, 0, Units.dpToPX(20, getContext()));
        taskView.setLayoutParams(bottomMargin);
        return taskView;
    }

    /**
     * @return String[] of keywords entered in searchEditText
     */
    public String[] getKeywords() {
        return searchEditText.getText().toString().trim().split(" ");
    }

    /**
     * Send a searchRequest and display search results
     */
    private void search() {
        if (searchEditText == null) {
            finishRefresh();
            return;
        }
        ElasticSearchTask.searchTaskByKeywords(getContext(), getKeywords(), new ElasticSearchTask.ListTaskListener() {
            @Override
            public void onResult(ArrayList<ElasticSearchTask> newTasks) {
                for (ElasticSearchTask task : newTasks) {
                    //Make sure task is not assigned and not completed
                    if (task.getStatus() != Task.Status.ASSIGNED && task.getStatus() != Task.Status.COMPLETED) {
                        tasks.add(task);
                    }
                }
                recyclerView.getAdapter().notifyDataSetChanged();
                searchEditText.setEnabled(true);
                finishRefresh();
            }

            @Override
            public void onError(VolleyError error) {
                Log.e("SearchFragment", error.getMessage(), error);
            }
        });
    }
}
