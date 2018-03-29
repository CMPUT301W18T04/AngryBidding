package ca.ualberta.angrybidding;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by SarahS on 2018/03/29.
 */

/*public class NotificationService extends Service {
    private NotificationLoadingThread notificationLoadingThread = new NotificationLoadingThread();
    private NotificationLoader loader;
    private long lastHeadsUpNotificationTime;

    private ArrayList<Messenger> clients = new ArrayList<Messenger>();

    private final Messenger messenger = new Messenger(new IncomingHandler());

    public static final int MSG_CONNECT = 0;
    public static final int MSG_DISCONNECT = 1;
    public static final int MSG_NOTIFICATION_CALLBACK = 2;

    public static final long HEADS_UP_NOTIFICATION_COOLDOWN = 5 * 60 * 1000;

    class IncomingHandler extends Handler {
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

    private void sendNotificationToAllClients(Notification notification){
        Gson gson = new Gson();
        String className = notification.getClass().getName();
        String notificationString = gson.toJson(notification);
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
    public void onCreate(){
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID){
        super.onStartCommand(intent, flags, startID);
        // onStartCommand can be called multiple times, check if it is already called
        if(loader != null){
            return START_STICKY;
        }
        loader = new NotificationLoader(this, "GetNewNotification", new NotificationResponseListener() {
            @Override
            public void onSuccess(ArrayList<Notification> notifications) {
                for(final Notification notification: notifications){
                    try {
                        notification.onReceived(NewNotificationService.this, new NotificationCallback() {
                            @Override
                            public void callBack() {
                                if(clients.size() > 0){
                                    sendNotificationToAllClients(notification);
                                }else{
                                    createNotification(notification);
                                }
                            }
                        });
                    }catch (Throwable t){
                        Log.e("NewNotificationService", t.getMessage(), t);
                    }
                }
            }

            @Override
            public void onError(String error) {
                if(error != null) {
                    Log.e("NewNotificationService", error);
                }
            }
        });
        notificationLoadingThread.start();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        notificationLoadingThread.interrupt();
    }

    protected void createNotification(Notification notification){
        int priority = NotificationCompat.PRIORITY_DEFAULT;
        long currentTime = System.currentTimeMillis();
        if(currentTime - lastHeadsUpNotificationTime > HEADS_UP_NOTIFICATION_COOLDOWN){
            priority = NotificationCompat.PRIORITY_HIGH;
            lastHeadsUpNotificationTime = currentTime;
        }

        //Build Intent
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(notification.getParentStack());
        stackBuilder.addNextIntent(notification.getIntent(this));
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        //Build Notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setDefaults(android.app.Notification.DEFAULT_VIBRATE)
                .setLights(0xff1bccf1, 500, 100)
                .setSmallIcon(R.drawable.logo_circle_128)
                .setContentIntent(resultPendingIntent)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.logo_circle_128))
                .setContentTitle(notification.getTitle(this))
                .setContentText(notification.getContent(this))
                .setAutoCancel(true)
                .setPriority(priority);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notification.getNotificationID(), builder.build());
    }

    private class NotificationLoadingThread extends Thread{
        public void run(){
            try {
                while (!isInterrupted()) {
                    Log.d("NewNotificationService", "run()");
                    User user = User.getMainUserSession(NewNotificationService.this);
                    if (user != null && user.getSession() != null) {
                        loader.setUser(user);
                        loader.load();
                    }
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }catch(Throwable t){
                Log.e("NewNotificationService", t.getMessage(), t);
            }
        }
    }
}*/
