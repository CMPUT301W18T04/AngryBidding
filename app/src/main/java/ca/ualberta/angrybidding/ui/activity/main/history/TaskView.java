package ca.ualberta.angrybidding.ui.activity.main.history;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import ca.ualberta.angrybidding.ElasticSearchTask;
import ca.ualberta.angrybidding.R;


/**
 * View for displaying a simple summary of a task including title, username and description
 */
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

    /**
     * loadViews() inflates the LinearLayout and connect contents(title and owner of the task) to
     * the ID in layout file
     */
    protected void loadViews() {
        container = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.view_task, this, true);

        titleTextView = findViewById(R.id.taskTitle);
        ownerTextView = findViewById(R.id.taskOwner);
    }

    /**
     * Initialize
     */
    protected void init() {
        loadViews();
    }

    /**
     * Binds specific task to this view
     * @param task Task object to bind
     */
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

    /**
     * @return View container
     */
    public LinearLayout getContainer() {
        return container;
    }

    /**
     * @return Task that is currently binded to the view
     */
    public ElasticSearchTask getElasticSearchTask() {
        return elasticSearchTask;
    }

    /**
     * @return Title TextView
     */
    public TextView getTitleTextView() {
        return titleTextView;
    }

    /**
     * @return Owner/Username TextView
     */
    public TextView getOwnerTextView() {
        return ownerTextView;
    }
}
