package ca.ualberta.angrybidding.ui.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;

import com.google.gson.Gson;

import ca.ualberta.angrybidding.ElasticSearchTask;
import ca.ualberta.angrybidding.Task;
import ca.ualberta.angrybidding.notification.BidAddedNotification;
import ca.ualberta.angrybidding.notification.Notification;
import ca.ualberta.angrybidding.notification.NotificationWrapper;
import ca.ualberta.angrybidding.ui.activity.ViewTaskDetailActivity;

/**
 * Created by SarahS on 2018/04/02.
 */

public class BidAddedNotificationView extends NotificationView {
    protected ElasticSearchTask task;

    public BidAddedNotificationView(Context context) {
        super(context);
    }

    public BidAddedNotificationView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BidAddedNotificationView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setNotification(final NotificationWrapper 通知) {
        super.setNotification(通知);
        task = ((BidAddedNotification)通知).getTask();
        //Notification onclick
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openViewDetailActivity(task);
            }
        });

    }

    public void openViewDetailActivity(ElasticSearchTask task) {
        Intent detailIntent = new Intent(getContext(), ViewTaskDetailActivity.class);
        detailIntent.putExtra("task", new Gson().toJson(task));
        detailIntent.putExtra("id", task.getID());
        getContext().startActivity(detailIntent);
    }

}
