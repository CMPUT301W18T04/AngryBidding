package ca.ualberta.angrybidding.notification;

import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;

import java.util.HashMap;

import ca.ualberta.angrybidding.ElasticSearchTask;
import ca.ualberta.angrybidding.Task;
import ca.ualberta.angrybidding.User;
import ca.ualberta.angrybidding.ui.activity.main.MainActivity;
import ca.ualberta.angrybidding.R;

/**
 * Created by SarahS on 2018/03/31.
 */

public class BidAddedNotification extends NotificationWrapper {
    private User user;
    private Task task;
    protected ElasticSearchTask elasticSearchTask;
    /*=private User commenter;
    private com.postphere.post.Entry commentEntry;
    private volatile int loadedCount = 0;*/

    public BidAddedNotification(Notification notification) {
        super(notification);
    }

    @Override
    protected void loadEntryParameters(HashMap<String, String> parameters) {
        user = new User(parameters.get("BidUser"));
        //task = new ElasticSearchTask(Integer.getInteger(parameters.get("TaskId")));
        //task from task ID?
        //task = new ? (parameters.get("TaskID"));
        /*commenter = new User(Integer.parseInt(parameters.get("UserID")));
        commentEntry = new com.postphere.post.Entry(Integer.parseInt(parameters.get("EntryID")));*/
    }

    @Override
    public String getTitle(Context context) {
        return  "Bids Added on Your Task";
        //return null;
    }

    @Override
    public String getContent(Context context) {
        return "user "+ user.getUsername() +" added bid of $"+ task.getBids().toString() +" on your task" + task.getTitle();
        //return null;
    }

    @Override
    public int getNotificationID() {
        return 0;
    }

    @Override
    //Copy
    public Intent getIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(intent.getFlags());
        intent.putExtra("fragmentID", R.id.nav_notification);
        return intent;
        //return null;
    }

    @Override
    //Copy
    public Class<?> getParentStack() {
        return MainActivity.class;
        //return null;
    }

    //unknown
    @Override
    public void onReceived(Context context, NotificationCallback callback) {
    }
}
