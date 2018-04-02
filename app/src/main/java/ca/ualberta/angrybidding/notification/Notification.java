package ca.ualberta.angrybidding.notification;

/**
 * Created by SarahS on 2018/03/27.
 */

//Copy from Entry.java

import java.util.HashMap;

import ca.ualberta.angrybidding.User;

//Notes: - public abstract class NewNotificationConnection implements ServiceConnection

public class Notification {
    private User user;
    private String notificationType;
    private boolean seen;
    //private ArrayList<Parameter> parameters;

    private HashMap<String, String> parameters;

    public Notification(User user, String notificationType, HashMap<String, String> parameters, boolean seen) {
        this.user = user;
        this.notificationType = notificationType;
        this.parameters = parameters;
        this.seen = false;
    }

    public User getUser() {
        return user;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getNotificationType() {
        return this.notificationType;
    }

    public HashMap<String, String> getParameterMap() {
        return parameters;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public boolean isSeen() {
        return seen;
    }
}