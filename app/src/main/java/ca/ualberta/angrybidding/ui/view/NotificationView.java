package ca.ualberta.angrybidding.ui.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import ca.ualberta.angrybidding.ElasticSearchNotification;
import ca.ualberta.angrybidding.ElasticSearchTask;
import ca.ualberta.angrybidding.R;
import ca.ualberta.angrybidding.Task;
import ca.ualberta.angrybidding.notification.Notification;
import ca.ualberta.angrybidding.notification.NotificationWrapper;
import ca.ualberta.angrybidding.ui.activity.ViewTaskDetailActivity;

/**
 * Created by SarahS on 2018/04/01.
 */

public class NotificationView extends LinearLayout{
    //Copy from TaskView
    protected ElasticSearchNotification elasticSearchNotification;
    protected NotificationWrapper notificationWrapper;

    protected LinearLayout container;
    protected TextView titleTextView;
    protected TextView messageTextView;

    public NotificationView(Context context) {
        this(context, null);
    }

    public NotificationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NotificationView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     * Assigns members to corresponding views
     */
    protected void loadViews() {
        container = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.view_notification, this, true);

        titleTextView = findViewById(R.id.notificationTitle);
        messageTextView = findViewById(R.id.notificationMessage);
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
    //kanji character TODO
    public void setNotification(final NotificationWrapper 通知) {
        this.notificationWrapper = 通知;
        titleTextView.setVisibility(View.VISIBLE);
        titleTextView.setText(通知.getTitle(getContext()));
        messageTextView.setText(通知.getContent(getContext()));
    }

    public LinearLayout getContainer() {
        return container;
    }

    public ElasticSearchNotification getElasticSearchNotification() {
        return elasticSearchNotification;
    }

    public TextView getTitleTextView() {
        return titleTextView;
    }


}
