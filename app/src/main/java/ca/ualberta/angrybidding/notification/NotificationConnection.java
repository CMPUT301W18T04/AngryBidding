package ca.ualberta.angrybidding.notification;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

//import com.slouple.android.notification.*;

/**
 * Created by SarahS on 2018/03/31.
 */

public abstract class NotificationConnection implements ServiceConnection {
    private Messenger serviceMessenger;
    private Messenger clientMessenger = new Messenger(new NewNotificationClientMessageHandler());

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        serviceMessenger = new Messenger(iBinder);
        connect();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        serviceMessenger = null;
    }

    //Automatically called after bind
    public void connect() {
        if (serviceMessenger != null) {
            try {
                Message message = Message.obtain(null, NotificationService.MSG_CONNECT);
                message.replyTo = clientMessenger;
                serviceMessenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    //Call disconnect before unbind
    public void disconnect() {
        if (serviceMessenger != null) {
            try {
                Message message = Message.obtain(null, NotificationService.MSG_DISCONNECT);
                message.replyTo = clientMessenger;
                serviceMessenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public abstract NotificationWrapper onDeserializeNotification(String className, String json);

    public abstract void onReceivedNotification(NotificationWrapper notificationWrapper);

    class NewNotificationClientMessageHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case NotificationService.MSG_NOTIFICATION_CALLBACK:
                    String className = message.getData().getString("className");
                    String json = message.getData().getString("notification");
                    NotificationWrapper notificationWrapper = onDeserializeNotification(className, json);
                    onReceivedNotification(notificationWrapper);
                    break;
                default:
                    super.handleMessage(message);
            }
        }
    }
}
