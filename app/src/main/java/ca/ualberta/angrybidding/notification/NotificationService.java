package ca.ualberta.angrybidding.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.util.ArrayList;

import ca.ualberta.angrybidding.ElasticSearchNotification;
import ca.ualberta.angrybidding.ElasticSearchUser;
import ca.ualberta.angrybidding.R;
import ca.ualberta.angrybidding.User;

public class NotificationService extends Service {
    private NotificationLoadingThread notificationLoadingThread = new NotificationLoadingThread();
    private ElasticSearchNotification.ListNotificationListener listener;
    private long lastHeadsUpNotificationTime;

    private ArrayList<Messenger> clients = new ArrayList<>();

    private final Messenger messenger = new Messenger(new IncomingHandler());

    public static final int MSG_CONNECT = 0;
    public static final int MSG_DISCONNECT = 1;
    public static final int MSG_NOTIFICATION_CALLBACK = 2;

    public static final long HEADS_UP_NOTIFICATION_COOLDOWN = 5 * 60 * 1000;

    class IncomingHandler extends Handler {
        /**
         * @param message Message
         */
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case MSG_CONNECT:
                    clients.add(message.replyTo);
                    break;
                case MSG_DISCONNECT:
                    clients.remove(message.replyTo);
                    break;
                default:
                    super.handleMessage(message);
            }
        }
    }

    /**
     * Sends notification
     *
     * @param notificationWrapper NotificationWrapper
     */
    //Korea
    private void sendNotificationToAllClients(NotificationWrapper notificationWrapper) {
        Gson gson = new Gson();
        String className = notificationWrapper.getClass().getName();
        String notificationString = gson.toJson(notificationWrapper);
        for (int i = clients.size() - 1; i >= 0; i--) {
            try {
                Bundle bundle = new Bundle();
                bundle.putString("className", className);
                bundle.putString("notification", notificationString);
                Message message = Message.obtain(null, MSG_NOTIFICATION_CALLBACK, 0, 0);
                message.setData(bundle);
                clients.get(i).send(message);
            } catch (RemoteException e) {
                // The client is dead.  Remove it from the list;
                // we are going through the list from back to front
                // so this is safe to do inside the loop.
                clients.remove(i);
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * Starts service background activity
     *
     * @param intent  Intent
     * @param flags   Flags
     * @param startID StartID
     * @return START_STICKY
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {
        super.onStartCommand(intent, flags, startID);
        // onStartCommand can be called multiple times, check if it is already called
        if (listener != null) {
            return START_STICKY;
        }
        listener = new ElasticSearchNotification.ListNotificationListener() {

            @Override
            public void onResult(ArrayList<ElasticSearchNotification> notifications) {
                //Create
                ArrayList<NotificationWrapper> notificationWrappers = new NotificationFactory().parseNotifications(notifications);
                for (final NotificationWrapper notificationWrapper : notificationWrappers) {
                    try {
                        notificationWrapper.onReceived(NotificationService.this, new NotificationCallback() {
                            @Override
                            public void callBack() {
                                if (clients.size() > 0) {
                                    sendNotificationToAllClients(notificationWrapper);
                                } else {
                                    createNotification(notificationWrapper);
                                }
                            }
                        });
                    } catch (Throwable t) {
                        Log.e("NotificationService", t.getMessage(), t);
                    }
                }
            }

            @Override
            public void onError(VolleyError error) {
                Log.e("NotificationService", error.getMessage(), error);
            }
        };
        notificationLoadingThread.start();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        notificationLoadingThread.interrupt();
    }

    /**
     * Create notification
     * <p>
     * Notification Channel needed for API 26 and above
     *
     * @param notificationWrapper NotificationWrapper
     */
    //Kanji Character
    protected void createNotification(NotificationWrapper notificationWrapper) {
        int priority = NotificationCompat.PRIORITY_DEFAULT;
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastHeadsUpNotificationTime > HEADS_UP_NOTIFICATION_COOLDOWN) {
            priority = NotificationCompat.PRIORITY_HIGH;
            lastHeadsUpNotificationTime = currentTime;
        }
        //Build Int
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(notificationWrapper.getParentStack());
        stackBuilder.addNextIntent(notificationWrapper.getIntent(this));
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        //Build Notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "GeneralNotification")
                .setDefaults(android.app.Notification.DEFAULT_VIBRATE)
                .setLights(0xff1bccf1, 500, 100)
                .setSmallIcon(R.drawable.ic_logo)
                .setContentIntent(resultPendingIntent)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.ic_logo))
                .setContentTitle(notificationWrapper.getTitle(this))
                .setContentText(notificationWrapper.getContent(this))
                .setAutoCancel(true)
                .setPriority(priority);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //Notification Channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("GeneralNotification",
                    "General Notification",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(notificationWrapper.getNotificationID(), builder.build());
    }

    private class NotificationLoadingThread extends Thread {
        public void run() {
            try {
                while (!isInterrupted()) {
                    Log.d("NewNotificationService", "run()");
                    User user = ElasticSearchUser.getMainUser(NotificationService.this);
                    if (user != null) {
                        ElasticSearchNotification.listNotSeenNotificationByUsername(NotificationService.this, user.getUsername(), listener);
                    }
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            } catch (Throwable t) {
                Log.e("NotificationService", t.getMessage(), t);
            }
        }
    }
}
