package ca.ualberta.angrybidding.notification;

import java.util.ArrayList;

/**
 * Created by SarahS on 2018/03/29.
 */
//Copy from NotificationFactory.java

public class NotificationFactory < T extends Notification >{

    public ArrayList<NotificationWrapper> parseNotifications(ArrayList<T> notifications){
        ArrayList<NotificationWrapper> notificationWrappers = new ArrayList<>();
        for(Notification notification : notifications){
            NotificationWrapper notificationWrapper = newInstance(notification.getNotificationType(), notification);
            if(notificationWrapper != null){
                notificationWrappers.add(notificationWrapper);
            }
        }
        return notificationWrappers;
    }

    public NotificationWrapper newInstance(String notificationType, Notification notification){
        switch (notificationType){
            case "BidAdded": return new BidAddedNotification(notification);
            //case "Accept etc ": return new RateNotification(entry);
        }
        return null;
    }
}
