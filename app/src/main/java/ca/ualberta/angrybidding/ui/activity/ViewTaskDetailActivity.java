package ca.ualberta.angrybidding.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.slouple.android.AdvancedActivity;
import com.slouple.android.widget.adapter.DummyAdapter;

import ca.ualberta.angrybidding.Bid;
import ca.ualberta.angrybidding.ElasticSearchTask;
import ca.ualberta.angrybidding.R;
import ca.ualberta.angrybidding.ui.view.BidView;

public class ViewTaskDetailActivity extends AdvancedActivity {
    private ElasticSearchTask elasticSearchTask;

    private TextView titleTextView;
    private TextView ownerTextView;
    private TextView descriptionTextView;
    private TextView bidsLable;
    private RecyclerView bidRecyclerView;
    /**
     * Creates ViewTaskDetailActivity
     * Gets task object from Intent using Gson
     * Assigns members to according view objects
     *
     * @param savedInstanceState State of saved instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_task_detail);

        Intent intent = getIntent();
        String taskJson = intent.getStringExtra("task");
        elasticSearchTask = new Gson().fromJson(taskJson, ElasticSearchTask.class);

        titleTextView = findViewById(R.id.taskDetailTitle);
        ownerTextView = findViewById(R.id.taskDetailOwner);
        descriptionTextView = findViewById(R.id.taskDetailDescription);
        bidsLable = findViewById(R.id.taskDetailBidsLabel);
        bidRecyclerView = findViewById(R.id.taskDetailBids);

        titleTextView.setText(elasticSearchTask.getTitle());
        ownerTextView.setText(elasticSearchTask.getUser().getUsername());
        descriptionTextView.setText(elasticSearchTask.getDescription());

        if(elasticSearchTask.getBids().size() < 1){
            bidsLable.setVisibility(View.GONE);
            bidRecyclerView.setVisibility(View.GONE);
        }else{
            bidRecyclerView.setAdapter(new DummyAdapter<Bid, BidView>(elasticSearchTask.getBids()) {
                @Override
                public BidView createView(int i) {
                    return new BidView(ViewTaskDetailActivity.this);
                }

                @Override
                public void onBindView(BidView bidView, Bid bid) {
                    bidView.setBid(bid);
                }

                @Override
                public void onReachingLastItem(int i) {

                }
            });
            bidRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
    }

}
