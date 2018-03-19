package ca.ualberta.angrybidding.ui.view;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.slouple.android.AdvancedActivity;
import com.slouple.android.ResultRequest;
import com.slouple.android.widget.button.PopupMenuButton;

import ca.ualberta.angrybidding.ElasticSearchTask;
import ca.ualberta.angrybidding.ElasticSearchUser;
import ca.ualberta.angrybidding.R;
import ca.ualberta.angrybidding.elasticsearch.DeleteResponseListener;
import ca.ualberta.angrybidding.ui.activity.AddBidActivity;
import ca.ualberta.angrybidding.ui.activity.EditTaskActivity;
import ca.ualberta.angrybidding.ui.activity.ViewTaskDetailActivity;

/**
 * Represents a view of the task object
 * Part of a list
 */
public class TaskView extends LinearLayout {
    protected ElasticSearchTask elasticSearchTask;

    protected LinearLayout container;
    protected TextView titleTextView;
    protected TextView ownerTextView;
    protected TextView descriptionTextView;
    protected PopupMenuButton popupMenuButton;

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

        descriptionTextView.setText(task.getDescription());

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openViewDetailActivity();
            }
        });

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

    public PopupMenuButton getPopupMenuButton() {
        return popupMenuButton;
    }

    /**
     * Uses the pop up menu
     * Checks if the user is the owner of the task and displays menu accordingly
     * Evokes different actions for different cases
     *
     * @param user     The user using the pop up menu
     * @param listener The listener to be called when task changes
     */
    public void usePopupMenu(final ElasticSearchUser user, final OnTaskChangeListener listener) {
        getPopupMenuButton().getPopupMenu().getMenu().clear();
        if (elasticSearchTask.getUser().equals(user)) {
            getPopupMenuButton().setMenuRes(R.menu.task_popup_self);
        } else {
            getPopupMenuButton().setMenuRes(R.menu.task_popup_other);
        }

        getPopupMenuButton().getPopupMenu().setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.taskPopupViewDetail:
                        openViewDetailActivity();
                        break;
                    case R.id.taskPopupEditTask:
                        openEditTaskActivity(listener);
                        break;
                    case R.id.taskPopupDeleteTask:
                        ElasticSearchTask.deleteTask(getContext(), elasticSearchTask.getID(), new DeleteResponseListener() {
                            @Override
                            public void onDeleted(String id) {
                                listener.onDelete();
                            }

                            @Override
                            public void onNotFound() {

                            }

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("TaskView", "taskView: " + error);
                            }
                        });
                        break;
                    case R.id.taskPopupBidTask:
                        openAddBidActivity(listener);

                        break;

                }

                return true;
            }
        });
    }

    /**
     * Opens AddBidActivity
     * @param listener calls bid is added
     */
    public void openAddBidActivity(final OnTaskChangeListener listener) {
        ((AdvancedActivity) getContext()).addResultRequest(new ResultRequest(AddBidActivity.REQUEST_CODE) {
            @Override
            public void onResult(Intent intent) {
                listener.onEdit();
            }

            @Override
            public void onCancel(Intent intent) {

            }
        });
        Intent bidIntent = new Intent(getContext(), AddBidActivity.class);
        bidIntent.putExtra("task", new Gson().toJson(elasticSearchTask));
        bidIntent.putExtra("id", elasticSearchTask.getID());
        ((AdvancedActivity) getContext()).startActivityForResult(bidIntent, AddBidActivity.REQUEST_CODE);
    }

    /**
     * Opens ViewDetailActivity
     */
    public void openViewDetailActivity() {
        Intent detailIntent = new Intent(TaskView.this.getContext(), ViewTaskDetailActivity.class);
        detailIntent.putExtra("task", new Gson().toJson(elasticSearchTask));
        getContext().startActivity(detailIntent);
    }

    /**
     * Opens EditTaskActivity
     * @param listener calls when task is edited
     */
    public void openEditTaskActivity(final OnTaskChangeListener listener) {
        Intent editIntent = new Intent(getContext(), EditTaskActivity.class);
        editIntent.putExtra("task", new Gson().toJson(elasticSearchTask));
        editIntent.putExtra("id", elasticSearchTask.getID());
        ((AdvancedActivity) getContext()).startActivityForResult(editIntent, EditTaskActivity.REQUEST_CODE);
    }

    /**
     * The listener called on delete or edit
     */
    public interface OnTaskChangeListener {
        void onDelete();

        void onEdit();
    }


}
