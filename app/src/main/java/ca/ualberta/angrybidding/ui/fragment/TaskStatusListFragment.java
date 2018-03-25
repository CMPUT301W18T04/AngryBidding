package ca.ualberta.angrybidding.ui.fragment;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.slouple.android.AdvancedFragment;
import com.slouple.android.widget.adapter.DummyAdapter;

import java.util.ArrayList;

import ca.ualberta.angrybidding.ElasticSearchTask;
import ca.ualberta.angrybidding.ElasticSearchUser;
import ca.ualberta.angrybidding.R;
import ca.ualberta.angrybidding.ui.view.TaskView;


public class TaskStatusListFragment extends TaskListFragment {
    protected Spinner spinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        spinner = view.findViewById(R.id.historySpinner);
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
                R.array.statusStringArray, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        return view;
    }

    public String getSelectedSpinnerItem() {
        return spinner.getSelectedItem().toString();
    }
}


