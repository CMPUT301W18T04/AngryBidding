package ca.ualberta.angrybidding.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.gson.Gson;
import com.slouple.android.AdvancedActivity;

import ca.ualberta.angrybidding.ElasticSearchTask;
import ca.ualberta.angrybidding.R;

public class ViewTaskDetailActivity extends AdvancedActivity {

    private ElasticSearchTask elasticSearchTask;
    private TextView taskDetailTitle;
    private TextView taskDetailOwner;
    private TextView taskDetailDescription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_task_detail);

        Intent intent = getIntent();
        String taskJson = intent.getStringExtra("task");
        elasticSearchTask = new Gson().fromJson(taskJson, ElasticSearchTask.class);

        taskDetailTitle = findViewById(R.id.taskDetailTitle);
        taskDetailOwner = findViewById(R.id.taskDetailOwner);
        taskDetailDescription = findViewById(R.id.taskDetailDescription);

        taskDetailTitle.setText(elasticSearchTask.getTitle());
        taskDetailOwner.setText(elasticSearchTask.getUser().getUsername());
        taskDetailDescription.setText(elasticSearchTask.getDescription());

    }




}
