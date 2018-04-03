package ca.ualberta.angrybidding.notification;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.VolleyError;

import java.util.HashMap;

import ca.ualberta.angrybidding.Bid;
import ca.ualberta.angrybidding.ElasticSearchTask;
import ca.ualberta.angrybidding.R;
import ca.ualberta.angrybidding.User;
import ca.ualberta.angrybidding.ui.activity.main.MainActivity;

public class BidAddedNotification extends NotificationWrapper {
    private User user;
    private ElasticSearchTask task;
    private String taskId;

    /**
     * @param notification notification
     */
    public BidAddedNotification(Notification notification) {
        super(notification);
    }

    /**
     * @param parameters Parameters {"BidUser",username},{"TaskId",Id}
     */
    @Override
    protected void loadEntryParameters(HashMap<String, String> parameters) {
        user = new User(parameters.get("BidUser"));
        taskId = parameters.get("TaskId");
    }

    /**
     * @param context Context
     * @return Returns notification title
     */
    @Override
    public String getTitle(Context context) {
        return "Bids Added on Your Task";
    }

    /**
     * @param context Context
     * @return Returns notification description (message)
     */
    @Override
    public String getContent(Context context) {
        String price = "unknown";
        for (Bid bid : task.getBids()) {
            if (bid.getUser().getUsername().equals(user.getUsername())) {
                price = bid.getPriceString();
                break;
            }
        }

        return "user " + user.getUsername() + " added bid of " + price + " on your task " + task.getTitle();
    }

    /**
     * @return Task that is bidded
     */
    public ElasticSearchTask getTask() {
        return task;
    }

    /**
     * @return Notification Id
     */
    @Override
    public int getNotificationID() {
        return 0;
    }

    /**
     * @param context Context
     * @return Returns intent
     */
    @Override
    public Intent getIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(intent.getFlags());
        intent.putExtra("fragmentID", R.id.nav_notification);
        return intent;
    }

    /**
     * @return MainActivity class
     */
    @Override
    public Class<?> getParentStack() {
        return MainActivity.class;
    }

    /**
     * @param context  Context
     * @param callback Callback
     */
    @Override
    public void onReceived(Context context, final NotificationCallback callback) {
        //Get task
        ca.ualberta.angrybidding.ElasticSearchTask.getTask(context, taskId, new ElasticSearchTask.GetTaskListener() {
            @Override
            public void onFound(ElasticSearchTask tasks) {
                task = tasks;
                loadCallback(callback);
            }

            @Override
            public void onNotFound() {
                Log.e("BidAddedNotification", "No Task Found");
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("BidAddedNotification", error.getMessage(), error);
            }
        });
    }

    /**
     * @param callback Callback
     */
    private void loadCallback(NotificationCallback callback) {
        callback.callBack();
    }
}
