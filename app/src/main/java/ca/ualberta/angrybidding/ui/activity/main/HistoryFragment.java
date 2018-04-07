package ca.ualberta.angrybidding.ui.activity.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.github.clans.fab.FloatingActionButton;
import com.slouple.android.AdvancedFragment;
import com.slouple.android.ResultRequest;

import ca.ualberta.angrybidding.R;
import ca.ualberta.angrybidding.ui.activity.AddBidActivity;
import ca.ualberta.angrybidding.ui.activity.AddTaskActivity;
import ca.ualberta.angrybidding.ui.activity.EditTaskActivity;
import ca.ualberta.angrybidding.ui.activity.ViewTaskDetailActivity;
import ca.ualberta.angrybidding.ui.activity.main.history.HistoryPageAdapter;

/**
 * HistoryFragement of MainActivity
 * Contains TaskPostedFragment and TaskProvidedFragment
 */
public class HistoryFragment extends AdvancedFragment implements IMainFragment {
    private HistoryPageAdapter pageAdapter;
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;

    private FrameLayout layout;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FloatingActionButton addTaskActionButton;

    @Override
    public AppBarLayout getAppBarLayout(ViewGroup rootView, LayoutInflater inflater) {
        if (appBarLayout == null) {
            appBarLayout = (AppBarLayout) inflater.inflate(R.layout.history_fragment_toolbar, rootView, false);
        }
        return appBarLayout;

    }

    @Override
    public Toolbar getToolbar(ViewGroup rootView, LayoutInflater inflater) {
        if (toolbar == null) {
            toolbar = getAppBarLayout(rootView, inflater).findViewById(R.id.history_fragment_toolbar);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = (FrameLayout) inflater.inflate(R.layout.fragment_history, container, false);
        viewPager = layout.findViewById(R.id.historyViewPager);
        pageAdapter = new HistoryPageAdapter(getChildFragmentManager(), getContext());
        viewPager.setAdapter(pageAdapter);
        tabLayout = layout.findViewById(R.id.historyTabLayout);
        tabLayout.setupWithViewPager(viewPager);

        //Add Task floating action button
        this.addTaskActionButton = layout.findViewById(R.id.addTaskActionButton);
        this.addTaskActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddTaskActivity.class);
                getContext().startActivityForResult(intent, AddTaskActivity.REQUEST_CODE);
            }
        });

        //Add task refresh result
        getContext().addResultRequest(new ResultRequest(AddTaskActivity.REQUEST_CODE) {
            @Override
            public void onResult(Intent intent) {
                pageAdapter.getTaskPostedFragment().refresh();
            }

            @Override
            public void onCancel(Intent intent) {
            }
        });

        getContext().addResultRequest(new ResultRequest(EditTaskActivity.REQUEST_CODE) {
            @Override
            public void onResult(Intent intent) {
                pageAdapter.getTaskPostedFragment().refresh();
            }

            @Override
            public void onCancel(Intent intent) {
            }
        });

        getContext().addResultRequest(new ResultRequest(AddBidActivity.REQUEST_CODE) {
            @Override
            public void onResult(Intent intent) {
                pageAdapter.getTaskProvidedFragment().refresh();
            }

            @Override
            public void onCancel(Intent intent) {
            }
        });

        getContext().addResultRequest(new ResultRequest(ViewTaskDetailActivity.REQUEST_CODE) {
            @Override
            public void onResult(Intent intent) {
                pageAdapter.getTaskProvidedFragment().refresh();
                pageAdapter.getTaskPostedFragment().refresh();
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
}
