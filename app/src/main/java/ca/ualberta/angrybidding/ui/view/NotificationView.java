package ca.ualberta.angrybidding.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import ca.ualberta.angrybidding.ElasticSearchNotification;
import ca.ualberta.angrybidding.R;
import ca.ualberta.angrybidding.notification.NotificationWrapper;

public class NotificationView extends LinearLayout {
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
     * @param notificationWrapper NotificationWrapper
     */
    //kanji character
    public void setNotification(final NotificationWrapper notificationWrapper) {
        this.notificationWrapper = notificationWrapper;
        titleTextView.setVisibility(View.VISIBLE);
        titleTextView.setText(notificationWrapper.getTitle(getContext()));
        messageTextView.setText(notificationWrapper.getContent(getContext()));
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
