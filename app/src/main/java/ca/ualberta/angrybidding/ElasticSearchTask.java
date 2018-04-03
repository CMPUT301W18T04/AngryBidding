package ca.ualberta.angrybidding;

import android.content.Context;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ca.ualberta.angrybidding.elasticsearch.AddRequest;
import ca.ualberta.angrybidding.elasticsearch.AddResponseListener;
import ca.ualberta.angrybidding.elasticsearch.BooleanSearchQuery;
import ca.ualberta.angrybidding.elasticsearch.DeleteRequest;
import ca.ualberta.angrybidding.elasticsearch.DeleteResponseListener;
import ca.ualberta.angrybidding.elasticsearch.GetRequest;
import ca.ualberta.angrybidding.elasticsearch.GetResponseListener;
import ca.ualberta.angrybidding.elasticsearch.MatchAllQuery;
import ca.ualberta.angrybidding.elasticsearch.MatchCondition;
import ca.ualberta.angrybidding.elasticsearch.NestedCondition;
import ca.ualberta.angrybidding.elasticsearch.SearchRequest;
import ca.ualberta.angrybidding.elasticsearch.SearchResponseListener;
import ca.ualberta.angrybidding.elasticsearch.SearchResult;
import ca.ualberta.angrybidding.elasticsearch.SearchSort;
import ca.ualberta.angrybidding.elasticsearch.TermCondition;
import ca.ualberta.angrybidding.elasticsearch.UpdateRequest;
import ca.ualberta.angrybidding.elasticsearch.UpdateResponseListener;
import ca.ualberta.angrybidding.map.LocationPoint;

public class ElasticSearchTask extends Task {
    public static final String ELASTIC_SEARCH_INDEX = "task";
    private transient String id;

    /**
     * @param id            ElasticSearch object id
     * @param user          User who created the task
     * @param title         Title of the task
     * @param description   Description of the task
     * @param locationPoint Location of the task
     * @param bids          Bids of the task
     */
    public ElasticSearchTask(String id, User user, String title, String description, LocationPoint locationPoint, ArrayList<Bid> bids) {
        super(user, title, description, locationPoint, bids);
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
     * @return ElasticSearch object ID
     */
    public String getID() {
        return this.id;
    }

    /**
     * Add a task to the ElasticSearch Server
     *
     * @param context  Context
     * @param task     Task to add
     * @param listener Listener to call on response
     */
    public static void addTask(Context context, Task task, AddResponseListener listener) {
        try {
            JSONObject jsonObject = new JSONObject(new Gson().toJson(task));
            AddRequest addRequest = new AddRequest(ELASTIC_SEARCH_INDEX, jsonObject, listener);
            addRequest.submit(context);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void getTask(Context context, String taskID, GetTaskListener listener) {
        GetRequest getRequest = new GetRequest(ELASTIC_SEARCH_INDEX, taskID, listener);
        getRequest.submit(context);
    }

    /**
     * Delete a task from the ElasticSearch Server
     *
     * @param context  Context
     * @param id       ID of the task
     * @param listener Listener to call on response
     */
    public static void deleteTask(Context context, String id, DeleteResponseListener listener) {
        DeleteRequest deleteRequest = new DeleteRequest(ELASTIC_SEARCH_INDEX, id, listener);
        deleteRequest.submit(context);
    }

    /**
     * Update a task in the ElasticSearch Server
     * This should not be used when the ElasticSearchTask object is passed by Gson since
     * ElasticSearchTask.id is transient which means it will not be parse as string with Gson
     *
     * @param context  Context
     * @param task     Task to edit
     * @param listener Listener to call on response
     */
    public static void updateTask(Context context, ElasticSearchTask task, UpdateResponseListener listener) {
        updateTask(context, task.getID(), task, listener);
    }

    /**
     * Update a task with associated id in the ElasticSearch Server
     *
     * @param context  Context
     * @param id       ID of the task
     * @param task     Task to edit
     * @param listener Listener to call on response
     */
    public static void updateTask(Context context, String id, Task task, UpdateResponseListener listener) {
        try {
            JSONObject jsonObject = new JSONObject(new Gson().toJson(task));
            UpdateRequest addRequest = new UpdateRequest(ELASTIC_SEARCH_INDEX, id, jsonObject, listener);
            addRequest.submit(context);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Search and list tasks that matches all keywords in the description
     *
     * @param context  Context
     * @param keywords List of keywords to search with
     * @param listener Listener to call on response
     */
    public static void searchTaskByKeywords(Context context, String[] keywords, final ListTaskListener listener) {
        BooleanSearchQuery query = new BooleanSearchQuery();

        for (String keyword : keywords) {
            query.getBoolCondition().addMust(new MatchCondition("description", keyword));
        }

        SearchSort searchSort = new SearchSort();
        searchSort.addField("_score", SearchSort.Order.DESC);
        query.addSearchSort(searchSort);

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

    /**
     * List all tasks
     *
     * @param context  Context
     * @param listener Listener to call on response
     */
    public static void listTask(Context context, final ListTaskListener listener) {
        MatchAllQuery query = new MatchAllQuery();
        SearchSort searchSort = new SearchSort();
        searchSort.addField("dateTime", SearchSort.Order.DESC);
        query.addSearchSort(searchSort);

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
        listTaskByUser(context, username, null, listener);
    }

    /**
     * List tasks of a user
     *
     * @param context  Context
     * @param username Username of the user to list
     * @param listener Listener to call on response
     */
    public static void listTaskByUser(Context context, String username, Status status, final ListTaskListener listener) {
        BooleanSearchQuery query = new BooleanSearchQuery();
        query.getBoolCondition().addMust(new TermCondition("user.username", username.toLowerCase().trim()));
        if (status != null) {
            query.getBoolCondition().addMust(new TermCondition("status", status.toString()));
        }
        SearchSort searchSort = new SearchSort();
        searchSort.addField("dateTime", SearchSort.Order.DESC);
        query.addSearchSort(searchSort);

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

    public static void listTaskByChosenUser(Context context, String username, final ListTaskListener listener) {
        listTaskByChosenUser(context, username, null, listener);
    }

    public static void listTaskByChosenUser(Context context, String username, Status status, final ListTaskListener listener) {
        BooleanSearchQuery query = new BooleanSearchQuery();
        query.getBoolCondition().addMust(new TermCondition("chosenBid.user.username", username.toLowerCase().trim()));
        if (status != null) {
            query.getBoolCondition().addMust(new TermCondition("status", status.toString()));
        }
        SearchSort searchSort = new SearchSort();
        searchSort.addField("dateTime", SearchSort.Order.DESC);
        query.addSearchSort(searchSort);

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

    public static void listTaskByBiddedUser(Context context, String username, final ListTaskListener listener) {
        listTaskByBiddedUser(context, username, null, listener);
    }

    public static void listTaskByBiddedUser(Context context, String username, Status status, final ListTaskListener listener) {
        BooleanSearchQuery query = new BooleanSearchQuery();

        BooleanSearchQuery nestedQuery = new BooleanSearchQuery();

        nestedQuery.getBoolCondition().addMust(new TermCondition("bids.user.username", username.toLowerCase().trim()));
        query.getBoolCondition().addMust(new NestedCondition("bids", nestedQuery));

        if (status != null) {
            query.getBoolCondition().addMust(new TermCondition("status", status.toString()));
        }

        SearchSort searchSort = new SearchSort();
        searchSort.addField("dateTime", SearchSort.Order.DESC);
        query.addSearchSort(searchSort);

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

    /**
     * Parse SearchResult to an ArrayList of ElasticSearchTask objects
     *
     * @param searchResult SearchResult to parse
     * @return List of ElasticSearchTask objects
     */
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

    public abstract static class GetTaskListener extends GetResponseListener {
        @Override
        public void onFound(JSONObject object) {
            onFound(new Gson().fromJson(object.toString(), ElasticSearchTask.class));
        }

        public abstract void onFound(ElasticSearchTask task);
    }

    /**
     * Listener for listing task requests
     */
    public interface ListTaskListener {
        void onResult(ArrayList<ElasticSearchTask> tasks);

        void onError(VolleyError error);
    }
}
