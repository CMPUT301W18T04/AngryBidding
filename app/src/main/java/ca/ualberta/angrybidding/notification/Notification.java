package ca.ualberta.angrybidding.notification;

import java.util.HashMap;

import ca.ualberta.angrybidding.User;

public class Notification {
    private User user;
    private String notificationType;
    private boolean seen;

    private HashMap<String, String> parameters;

    /**
     * @param user             User who created the task bidded
     * @param notificationType Notification Type
     * @param parameters       {"BidUser",username},{"TaskId",Id}
     * @param seen             Boolean if notification seen or not
     */
    public Notification(User user, String notificationType, HashMap<String, String> parameters, boolean seen) {
        this.user = user;
        this.notificationType = notificationType;
        this.parameters = parameters;
        this.seen = false;
    }

    /**
     * @return User who created the task bidded
     */
    public User getUser() {
        return user;
    }

    /**
     * sets notification type
     *
     * @param notificationType Notification Type
     */
    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    /**
     * @return Notification Type
     */
    public String getNotificationType() {
        return this.notificationType;
    }

    /**
     * @return parameters
     */
    public HashMap<String, String> getParameterMap() {
        return parameters;
    }

    /**
     * sets seen
     *
     * @param seen Boolean if notification seen or not
     */
    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    /**
     * @return seen
     */
    public boolean isSeen() {
        return seen;
    }
}