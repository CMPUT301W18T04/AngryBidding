package ca.ualberta.angrybidding.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.slouple.android.AdvancedActivity;

import ca.ualberta.angrybidding.R;

public class ViewTaskDetailActivity extends AdvancedActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_task_detail);
    }
}
