package ca.ualberta.angrybidding.ui.activity.main.history;

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
import com.slouple.android.widget.button.PopupMenuButton;
import com.slouple.android.widget.image.ImageSlider;

import ca.ualberta.angrybidding.ElasticSearchTask;
import ca.ualberta.angrybidding.ElasticSearchUser;
import ca.ualberta.angrybidding.R;
import ca.ualberta.angrybidding.User;
import ca.ualberta.angrybidding.elasticsearch.DeleteResponseListener;
import ca.ualberta.angrybidding.ui.activity.ViewTaskDetailActivity;

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

    protected void loadViews() {
        container = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.view_task, this, true);

        titleTextView = findViewById(R.id.taskTitle);
        ownerTextView = findViewById(R.id.taskOwner);
        descriptionTextView = findViewById(R.id.taskDescription);
        popupMenuButton = findViewById(R.id.taskPopupMenuButton);

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

        descriptionTextView.setText(task.getDescription());


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
                        //TODO open ViewTaskDetailActivity
                        Intent intent = new Intent(TaskView.this.getContext(), ViewTaskDetailActivity.class);
                        intent.putExtra("task", new Gson().toJson(elasticSearchTask));
                        getContext().startActivity(intent);
                        break;
                    case R.id.taskPopupEditTask:
                        //TODO open EditTaskActivity
                        break;
                    case R.id.taskPopupDeleteTask:
                        //TODO Elastic search remove task
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
                        //TODO Bid Task Activity
                        break;
                }

                return true;
            }
        });
    }

    public interface OnTaskChangeListener {
        void onDelete();

        void onEdit();
    }


}
