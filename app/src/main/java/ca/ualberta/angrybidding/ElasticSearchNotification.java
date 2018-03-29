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

public class ElasticSearchNotification extends Notification{
    public static final String ELASTIC_SEARCH_INDEX = "notification";

    private transient String id;

    public ElasticSearchNotification(String id, User user, String notificationType, HashMap<String, String> parameters, boolean seen) {
        super(user, notificationType, parameters, false);
        this.id = id;
    }

    /**
     * Set ElasticSearch object ID
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

    public static void addNotification(Context context, Notification notification, AddResponseListener listener){
        try {
            JSONObject jsonObject = new JSONObject(new Gson().toJson(notification));
            AddRequest request = new AddRequest(ELASTIC_SEARCH_INDEX, jsonObject, listener);
            request.submit(context);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void listNotificationByUsername(Context context, String username, final ListNotificationListener listener){
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

    public static void listNotSeenNotificationByUsername(Context context, String username, final ListNotificationListener listener){
        TermAndQuery query = new TermAndQuery();
        query.addTerm("user.username", username);
        query.addTerm("seen", "false");
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

    public interface ListNotificationListener{
        void onResult(ArrayList<ElasticSearchNotification> notifications);
        void onError(VolleyError error);
    }

}
