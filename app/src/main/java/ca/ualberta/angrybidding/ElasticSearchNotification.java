package ca.ualberta.angrybidding;

import android.content.Context;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import ca.ualberta.angrybidding.elasticsearch.AddRequest;
import ca.ualberta.angrybidding.elasticsearch.AddResponseListener;
import ca.ualberta.angrybidding.elasticsearch.SearchRequest;
import ca.ualberta.angrybidding.elasticsearch.SearchResponseListener;
import ca.ualberta.angrybidding.elasticsearch.SearchResult;
import ca.ualberta.angrybidding.elasticsearch.TermAndQuery;
import ca.ualberta.angrybidding.elasticsearch.UpdateRequest;
import ca.ualberta.angrybidding.elasticsearch.UpdateResponseListener;
import ca.ualberta.angrybidding.notification.Notification;

public class ElasticSearchNotification extends Notification {
    public static final String ELASTIC_SEARCH_INDEX = "notification";

    private transient String id;

    /**
     * @param id               ElasticSearch object id of the notification
     * @param user             User of who created the task that was bidded
     * @param notificationType Notification Type
     * @param parameters       Parameter containing Bidded user username and Task ID
     * @param seen             Boolean if the notification was seen or not
     */
    public ElasticSearchNotification(String id, User user, String notificationType, HashMap<String, String> parameters, boolean seen) {
        super(user, notificationType, parameters, false);
        this.id = id;
    }

    /**
     * Set ElasticSearch object ID
     *
     * @param id ElasticSearch object ID
     */
    private void setID(String id) {
        this.id = id;
    }

    /**
     * @return ElasticSearch id
     */
    public String getID() {
        return this.id;
    }

    /**
     * Adds new notification
     *
     * @param context      Context
     * @param notification Notification
     * @param listener     Listener to call when new notification re made
     */
    public static void addNotification(Context context, Notification notification, AddResponseListener listener) {
        try {
            JSONObject jsonObject = new JSONObject(new Gson().toJson(notification));
            AddRequest request = new AddRequest(ELASTIC_SEARCH_INDEX, jsonObject, listener);
            request.submit(context);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * lists all the notification
     *
     * @param context  Context
     * @param username Username of who created the task that was bidded
     * @param listener Listener to call on response
     */
    public static void listNotificationByUsername(Context context, String username, final ListNotificationListener listener) {
        TermAndQuery query = new TermAndQuery();
        query.addTerm("user.username", username);
        SearchRequest request = new SearchRequest(ELASTIC_SEARCH_INDEX, query, new SearchResponseListener() {
            @Override
            public void onResult(SearchResult searchResult) {
                listener.onResult(parseNotifications(searchResult));
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError(error);
            }
        });
        request.submit(context);
    }

    /**
     * lists all the notification not seen
     *
     * @param context  Context
     * @param username Username of who created the task that was bidded
     * @param listener Listener to call on response
     */
    public static void listNotSeenNotificationByUsername(final Context context, String username, final ListNotificationListener listener) {
        listNotSeenNotificationByUsername(context, username, true, listener);
    }

    /**
     * lists all the notification not seen
     *
     * @param context   Context
     * @param username  Username of who created the task that was bidded
     * @param setToSeen Boolean set to seen
     * @param listener  Listener to call on response
     */
    public static void listNotSeenNotificationByUsername(final Context context, String username, final boolean setToSeen, final ListNotificationListener listener) {
        TermAndQuery query = new TermAndQuery();
        query.addTerm("user.username", username);
        query.addTerm("seen", "false");
        final SearchRequest request = new SearchRequest(ELASTIC_SEARCH_INDEX, query, new SearchResponseListener() {
            @Override
            public void onResult(SearchResult searchResult) {
                ArrayList<ElasticSearchNotification> notifications = parseNotifications(searchResult);
                if (setToSeen) {
                    for (ElasticSearchNotification notification : notifications) {
                        notification.setSeen(true);
                        updateNotification(context, notification.getID(), notification, new UpdateResponseListener() {
                            @Override
                            public void onCreated(String id) {

                            }

                            @Override
                            public void onUpdated(int version) {

                            }

                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        });
                    }
                }
                listener.onResult(notifications);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError(error);
            }
        });
        request.submit(context);
    }

    /**
     * Updates the notification
     *
     * @param context      Context
     * @param id           Id of the notification
     * @param notification Notification
     * @param listener     Listener to call on response
     */
    public static void updateNotification(Context context, String id, Notification notification, UpdateResponseListener listener) {
        try {
            UpdateRequest updateRequest = new UpdateRequest(ELASTIC_SEARCH_INDEX, id, new JSONObject(new Gson().toJson(notification)), listener);
            updateRequest.submit(context);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected static ArrayList<ElasticSearchNotification> parseNotifications(SearchResult searchResult) {
        ArrayList<ElasticSearchNotification> notifications = new ArrayList<>();
        for (int i = 0; i < searchResult.getSearchResultObjects().size(); i++) {
            SearchResult.SearchResultObject searchResultObject = searchResult.getSearchResultObjects().get(i);
            String notificationString = searchResultObject.getSource().toString();
            ElasticSearchNotification notification = new Gson().fromJson(notificationString, ElasticSearchNotification.class);
            notification.setID(searchResultObject.getId());
            notifications.add(notification);
        }
        return notifications;
    }

    /**
     * Listener for listing notification requests
     */
    public interface ListNotificationListener {
        void onResult(ArrayList<ElasticSearchNotification> notifications);

        void onError(VolleyError error);
    }

}
