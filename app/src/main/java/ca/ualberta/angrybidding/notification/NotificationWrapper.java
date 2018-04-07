package ca.ualberta.angrybidding.notification;

import android.content.Context;
import android.content.Intent;

import java.util.HashMap;

public abstract class NotificationWrapper {
    protected Notification notification;

    /**
     * @param notification Notification
     */
    public NotificationWrapper(Notification notification) {
        this.notification = notification;
        loadEntryParameters(notification.getParameterMap());
    }

    protected abstract void loadEntryParameters(HashMap<String, String> parameters);

    public Notification getNotification() {
        return notification;
    }

    public abstract String getTitle(Context context);

    public abstract String getContent(Context context);

    public abstract int getNotificationID();

    public abstract Intent getIntent(Context context);

    public abstract Class<?> getParentStack();

    public abstract void onReceived(Context context, NotificationCallback callback);

}
