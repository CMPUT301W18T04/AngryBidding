package ca.ualberta.angrybidding.ui.activity.main.history;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.slouple.android.widget.image.ImageSlider;

import ca.ualberta.angrybidding.ElasticSearchTask;
import ca.ualberta.angrybidding.R;

public class TaskView extends LinearLayout {
    protected ElasticSearchTask elasticSearchTask;

    protected LinearLayout container;
    protected TextView titleTextView;
    protected TextView ownerTextView;

    public TaskView(Context context) {
        this(context, null);
    }

    public TaskView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TaskView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    protected void loadViews() {
        container = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.view_task, this, true);

        titleTextView = findViewById(R.id.taskTitle);
        ownerTextView = findViewById(R.id.taskOwner);
    }

    protected void init() {
        loadViews();
    }

    public void setTask(ElasticSearchTask task) {
        this.elasticSearchTask = task;
        if (task.getTitle() == null) {
            titleTextView.setText("Missing Title");
        } else {
            titleTextView.setVisibility(View.VISIBLE);
            titleTextView.setText(task.getTitle());
        }

        if (task.getUser().getUsername() == null) {
            ownerTextView.setText("Missing User");
        } else {
            ownerTextView.setVisibility(View.VISIBLE);
            ownerTextView.setText(task.getUser().getUsername());
        }
    }

    public LinearLayout getContainer() {
        return container;
    }

    public ElasticSearchTask getElasticSearchTask() {
        return elasticSearchTask;
    }

    public TextView getTitleTextView() {
        return titleTextView;
    }

    public TextView getOwnerTextView() {
        return ownerTextView;
    }
}
