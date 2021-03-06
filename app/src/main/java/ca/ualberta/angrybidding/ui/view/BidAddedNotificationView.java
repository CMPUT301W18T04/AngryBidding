package ca.ualberta.angrybidding.ui.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;

import com.google.gson.Gson;
import com.slouple.android.AdvancedActivity;

import ca.ualberta.angrybidding.ElasticSearchTask;
import ca.ualberta.angrybidding.notification.BidAddedNotification;
import ca.ualberta.angrybidding.notification.NotificationWrapper;
import ca.ualberta.angrybidding.ui.activity.ViewTaskDetailActivity;

public class BidAddedNotificationView extends NotificationView {
    protected ElasticSearchTask task;
    protected String taskId;

    public BidAddedNotificationView(Context context) {
        super(context);
    }

    public BidAddedNotificationView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BidAddedNotificationView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Set Notification message and Title
     *
     * @param notificationWrapper NotificationWrapper
     */
    @Override
    public void setNotification(final NotificationWrapper notificationWrapper) {
        super.setNotification(notificationWrapper);
        task = ((BidAddedNotification) notificationWrapper).getTask();
        taskId = ((BidAddedNotification) notificationWrapper).getID();
        //Notification onclick
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openViewDetailActivity(task,taskId);
            }
        });

    }

    public void openViewDetailActivity(ElasticSearchTask task, String taskId) {
        Intent detailIntent = new Intent(getContext(), ViewTaskDetailActivity.class);
        detailIntent.putExtra("task", new Gson().toJson(task));
        detailIntent.putExtra("id", taskId);
        ((AdvancedActivity) getContext()).startActivityForResult(detailIntent, ViewTaskDetailActivity.REQUEST_CODE); 
    }

}
