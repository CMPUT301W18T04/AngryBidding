package ca.ualberta.angrybidding.ui.activity.main;

import android.content.Context;
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
import com.slouple.android.Units;

import java.util.ArrayList;

import ca.ualberta.angrybidding.ElasticSearchTask;
import ca.ualberta.angrybidding.R;
import ca.ualberta.angrybidding.ui.fragment.TaskListFragment;
import ca.ualberta.angrybidding.ui.view.TaskView;

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
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
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

    @Override
    public void onRefresh() {
        super.onRefresh();
        search();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        swipeRefreshLayout.setRefreshing(false);
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

    public String[] getKeywords() {
        return searchEditText.getText().toString().trim().split(" ");
    }

    private void search() {
        if (searchEditText == null) {
            return;
        }
        ElasticSearchTask.searchTaskByKeywords(getContext(), getKeywords(), new ElasticSearchTask.ListTaskListener() {
            @Override
            public void onResult(ArrayList<ElasticSearchTask> newTasks) {
                for (ElasticSearchTask task : newTasks) {
                    if (task.getChosenBid() == null && !task.isCompleted()) {
                        tasks.add(task);
                    }
                }
                recyclerView.getAdapter().notifyDataSetChanged();
                searchEditText.setEnabled(true);
                finishRefresh();
            }

            /*
            Show message when a error occurs
             */
            @Override
            public void onError(VolleyError error) {
                Log.e("SearchFragment", error.getMessage(), error);
            }
        });
    }
}
