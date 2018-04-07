package ca.ualberta.angrybidding.ui.view;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MenuItem;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.slouple.android.AdvancedActivity;
import com.slouple.android.widget.button.PopupMenuButton;

import ca.ualberta.angrybidding.ElasticSearchTask;
import ca.ualberta.angrybidding.ElasticSearchUser;
import ca.ualberta.angrybidding.R;
import ca.ualberta.angrybidding.elasticsearch.DeleteResponseListener;
import ca.ualberta.angrybidding.elasticsearch.UpdateResponseListener;
import ca.ualberta.angrybidding.ui.activity.AddBidActivity;
import ca.ualberta.angrybidding.ui.activity.EditTaskActivity;
import ca.ualberta.angrybidding.ui.activity.ViewTaskDetailActivity;

public class TaskPopupMenuButton extends PopupMenuButton {
    private ElasticSearchTask task;

    public TaskPopupMenuButton(Context context) {
        super(context);
    }

    public TaskPopupMenuButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TaskPopupMenuButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     *
     * Checks if the user is the owner of the task and displays menu accordingly
     * Evokes different actions for different cases
     *
     * @param user     The user using the pop up menu
     * @param listener The listener to be called when task changes without opening other activities.
     *                 ResultListener must be used if a new activity is used.
     */
    public void setTask(final ElasticSearchTask task, final ElasticSearchUser user, final OnTaskChangeListener listener) {
        this.task = task;
        getPopupMenu().getMenu().clear();
        if (task.getUser().equals(user)) {
            switch (task.getStatus()) {
                case REQUESTED:
                    setMenuRes(R.menu.task_popup_self_requested);
                    break;
                case ASSIGNED:
                    setMenuRes(R.menu.task_popup_self_assigned);
                    break;
                default:
                    setMenuRes(R.menu.task_popup_self);
                    break;
            }
        } else {
            switch (task.getStatus()) {
                case REQUESTED:
                case BIDDED:
                    setMenuRes(R.menu.task_popup_other_requested);
                    break;
                default:
                    setMenuRes(R.menu.task_popup_other);
                    break;
            }
        }

        getPopupMenu().setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.taskPopupViewDetail:
                        openViewDetailActivity();
                        break;
                    case R.id.taskPopupEditTask:
                        openEditTaskActivity(listener);
                        break;
                    case R.id.taskPopupSetDone:
                        setTaskCompleted(true, listener);
                        break;
                    case R.id.taskPopupSetNotDone:
                        setTaskCompleted(false, listener);
                        break;
                    case R.id.taskPopupDeleteTask:
                        deleteTask(listener);
                        break;
                    case R.id.taskPopupBidTask:
                        openAddBidActivity(listener);
                        break;

                }

                return true;
            }
        });
    }

    public void deleteTask(final OnTaskChangeListener listener){
        ElasticSearchTask.deleteTask(getContext(), task.getID(), new DeleteResponseListener() {
            @Override
            public void onDeleted(String id) {
                listener.onDelete();
            }

            @Override
            public void onNotFound() {

            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TaskPopupMenuButton", error.getMessage(), error);
            }
        });
    }

    public void setTaskCompleted(boolean completed, final OnTaskChangeListener listener){
        task.setCompleted(completed);
        ElasticSearchTask.updateTask(getContext(), task.getID(), task, new UpdateResponseListener() {
            @Override
            public void onCreated(String id) {
                listener.onEdit();
            }

            @Override
            public void onUpdated(int version) {
                listener.onEdit();
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TaskPopupMenuButton", error.getMessage(), error);
            }
        });

    }

    /**
     * Opens AddBidActivity
     *
     * @param listener calls bid is added
     */
    public void openAddBidActivity(final OnTaskChangeListener listener) {
        Intent bidIntent = new Intent(getContext(), AddBidActivity.class);
        bidIntent.putExtra("task", new Gson().toJson(task));
        bidIntent.putExtra("id", task.getID());
        ((AdvancedActivity) getContext()).startActivityForResult(bidIntent, AddBidActivity.REQUEST_CODE);
    }

    /**
     * Opens ViewDetailActivity
     */
    public void openViewDetailActivity() {
        Intent detailIntent = new Intent(getContext(), ViewTaskDetailActivity.class);
        detailIntent.putExtra("task", new Gson().toJson(task));
        detailIntent.putExtra("id", task.getID());
        ((AdvancedActivity) getContext()).startActivityForResult(detailIntent, ViewTaskDetailActivity.REQUEST_CODE);
    }

    /**
     * Opens EditTaskActivity
     *
     * @param listener calls when task is edited
     */
    public void openEditTaskActivity(final OnTaskChangeListener listener) {
        Intent editIntent = new Intent(getContext(), EditTaskActivity.class);
        editIntent.putExtra("task", new Gson().toJson(task));
        editIntent.putExtra("id", task.getID());
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
