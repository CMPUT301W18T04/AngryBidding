package ca.ualberta.angrybidding.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.gson.Gson;
import com.slouple.android.AdvancedActivity;

import ca.ualberta.angrybidding.ElasticSearchTask;
import ca.ualberta.angrybidding.R;

public class ViewTaskDetailActivity extends AdvancedActivity {

    private ElasticSearchTask elasticSearchTask;
    private TextView titleTextView;
    private TextView ownerTextView;
    private TextView descriptionTextView;

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

        titleTextView.setText(elasticSearchTask.getTitle());
        ownerTextView.setText(elasticSearchTask.getUser().getUsername());
        descriptionTextView.setText(elasticSearchTask.getDescription());

    }


}
