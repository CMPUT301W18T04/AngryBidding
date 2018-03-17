package ca.ualberta.angrybidding;

import android.content.Context;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ca.ualberta.angrybidding.elasticsearch.AddRequest;
import ca.ualberta.angrybidding.elasticsearch.AddResponseListener;
import ca.ualberta.angrybidding.elasticsearch.DeleteRequest;
import ca.ualberta.angrybidding.elasticsearch.DeleteResponseListener;
import ca.ualberta.angrybidding.elasticsearch.ElasticSearchResponseListener;
import ca.ualberta.angrybidding.elasticsearch.MatchAllQuery;
import ca.ualberta.angrybidding.elasticsearch.SearchRequest;
import ca.ualberta.angrybidding.elasticsearch.SearchResponseListener;
import ca.ualberta.angrybidding.elasticsearch.SearchResult;
import ca.ualberta.angrybidding.elasticsearch.TermAndQuery;
import ca.ualberta.angrybidding.elasticsearch.UpdateRequest;
import ca.ualberta.angrybidding.elasticsearch.UpdateResponseListener;

public class ElasticSearchTask extends Task {
    public static final String ELASTIC_SEARCH_INDEX = "task";
    private transient String id;

    public ElasticSearchTask(String id, User user, String title, String description, LocationPoint locationPoint, ArrayList<Bid> bids) {
        super(user, title, description, locationPoint, bids);
        this.id = id;
    }

    public ElasticSearchTask(User user, String title, String description, LocationPoint locationPoint, Bid chosenBid) {
        super(user, title, description, locationPoint, chosenBid);
        this.id = id;
    }

    private void setID(String id) {
        this.id = id;
    }

    public String getID() {
        return this.id;
    }

    public static void addTask(Context context, Task task, AddResponseListener listener) {
        try {
            JSONObject jsonObject = new JSONObject(new Gson().toJson(task));
            AddRequest addRequest = new AddRequest(ELASTIC_SEARCH_INDEX, jsonObject, listener);
            addRequest.submit(context);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void deleteTask(Context context, String id, DeleteResponseListener listener) {
        DeleteRequest deleteRequest = new DeleteRequest(ELASTIC_SEARCH_INDEX, id, listener);
        deleteRequest.submit(context);
    }

    public static void updateTask(Context context, ElasticSearchTask task, UpdateResponseListener listener) {
        updateTask(context, task.getID(), task, listener);
    }

    public static void updateTask(Context context, String id, Task task, UpdateResponseListener listener) {
        try {
            JSONObject jsonObject = new JSONObject(new Gson().toJson(task));
            UpdateRequest addRequest = new UpdateRequest(ELASTIC_SEARCH_INDEX, id, jsonObject, listener);
            addRequest.submit(context);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void searchTaskByKeywords(Context context, String[] keywords, final ListTaskListener listener){
        TermAndQuery query = new TermAndQuery();
        for(String keyword : keywords){
            query.addTerm("description", keyword);
        }
        SearchRequest searchRequest = new SearchRequest(ELASTIC_SEARCH_INDEX, query, new SearchResponseListener() {
            @Override
            public void onResult(SearchResult searchResult) {
                listener.onResult(parseTasks(searchResult));
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError(error);
            }
        });
        searchRequest.submit(context);
    }

    public static void listTask(Context context, final ListTaskListener listener) {
        MatchAllQuery query = new MatchAllQuery();
        SearchRequest searchRequest = new SearchRequest(ELASTIC_SEARCH_INDEX, query, new SearchResponseListener() {
            @Override
            public void onResult(SearchResult searchResult) {
                listener.onResult(parseTasks(searchResult));
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError(error);
            }
        });
        searchRequest.submit(context);
    }

    public static void listTaskByUser(Context context, String username, final ListTaskListener listener) {
        TermAndQuery query = new TermAndQuery();
        query.addTerm("user.username", username.toLowerCase().trim());
        SearchRequest searchRequest = new SearchRequest(ELASTIC_SEARCH_INDEX, query, new SearchResponseListener() {
            @Override
            public void onResult(SearchResult searchResult) {
                listener.onResult(parseTasks(searchResult));
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError(error);
            }
        });
        searchRequest.submit(context);
    }

    protected static ArrayList<ElasticSearchTask> parseTasks(SearchResult searchResult) {
        ArrayList<ElasticSearchTask> tasks = new ArrayList<>();
        for (int i = 0; i < searchResult.getSearchResultObjects().size(); i++) {
            SearchResult.SearchResultObject searchResultObject = searchResult.getSearchResultObjects().get(i);
            String taskString = searchResultObject.getSource().toString();
            ElasticSearchTask task = new Gson().fromJson(taskString, ElasticSearchTask.class);
            task.setID(searchResultObject.getId());
            tasks.add(task);
        }
        return tasks;
    }

    public interface ListTaskListener {
        void onResult(ArrayList<ElasticSearchTask> tasks);

        void onError(VolleyError error);
    }
}
