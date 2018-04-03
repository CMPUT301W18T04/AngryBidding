package ca.ualberta.angrybidding.notification;

import java.util.ArrayList;


public class NotificationFactory<T extends Notification> {

    /**
     * @param notifications notifications
     * @return notification wrappers
     */
    public ArrayList<NotificationWrapper> parseNotifications(ArrayList<T> notifications) {
        ArrayList<NotificationWrapper> notificationWrappers = new ArrayList<>();
        for (Notification notification : notifications) {
            NotificationWrapper notificationWrapper = newInstance(notification.getNotificationType(), notification);
            if (notificationWrapper != null) {
                notificationWrappers.add(notificationWrapper);
            }
        }
        return notificationWrappers;
    }

    /**
     * @param notificationType NotificationType
     * @param notification     notification
     * @return new Notification depending on NotificationType
     */
    public NotificationWrapper newInstance(String notificationType, Notification notification) {
        switch (notificationType) {
            case "BidAdded":
                return new BidAddedNotification(notification);
        }
        return null;
    }
}
