package ca.ualberta.angrybidding.ui.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.slouple.android.widget.button.PopupMenuButton;

import ca.ualberta.angrybidding.ElasticSearchTask;
import ca.ualberta.angrybidding.ElasticSearchUser;
import ca.ualberta.angrybidding.R;
import ca.ualberta.angrybidding.ui.activity.UserProfileActivity;

/**
 * Represents a view of the task object
 * Part of a list
 */
public class TaskView extends LinearLayout {
    protected ElasticSearchTask task;

    protected LinearLayout container;
    protected TextView titleTextView;
    protected TextView ownerTextView;
    protected TextView descriptionTextView;
    protected TaskPopupMenuButton popupMenuButton;

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
     * Assigns members to corresponding views
     */
    protected void loadViews() {
        container = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.view_task, this, true);

        titleTextView = findViewById(R.id.taskTitle);
        ownerTextView = findViewById(R.id.taskOwner);
        descriptionTextView = findViewById(R.id.taskDescription);
        popupMenuButton = findViewById(R.id.taskPopupMenuButton);

    }

    /**
     * Initializes the view
     */
    protected void init() {
        loadViews();
    }

    /**
     * Extracts the task object into the view
     *
     * @param task The task object
     */
    public void setTask(final ElasticSearchTask task, TaskPopupMenuButton.OnTaskChangeListener listener) {
        this.task = task;
        titleTextView.setText(task.getTitle());
        ownerTextView.setText(task.getUser().getUsername());
        descriptionTextView.setText(task.getDescription());

        popupMenuButton.setTask(task, ElasticSearchUser.getMainUser(getContext()), listener);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMenuButton.openViewDetailActivity();
            }
        });

        ownerTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), UserProfileActivity.class);
                intent.putExtra("username", task.getUser().getUsername());
                getContext().startActivity(intent);
            }
        });

    }

    public LinearLayout getContainer() {
        return container;
    }

    public ElasticSearchTask getElasticSearchTask() {
        return task;
    }

    public TextView getTitleTextView() {
        return titleTextView;
    }

    public TextView getOwnerTextView() {
        return ownerTextView;
    }

    public PopupMenuButton getPopupMenuButton() {
        return popupMenuButton;
    }


}
