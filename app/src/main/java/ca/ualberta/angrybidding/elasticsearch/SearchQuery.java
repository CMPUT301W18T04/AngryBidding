package ca.ualberta.angrybidding.elasticsearch;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Basic ElasticSearch Search Query
 */
public class SearchQuery {
    private JSONObject root;
    private JSONObject query;

    public SearchQuery() {
        root = new JSONObject();
        query = new JSONObject();
        try {
            root.put("query", query);
        } catch (JSONException e) {
            Log.e("SearchQuery", e.getMessage(), e);
        }
        setSize(1000); //Defaults size to 1000
    }

    /**
     * @return root JSONObject for the body
     */
    public JSONObject getRoot() {
        return this.root;
    }

    /**
     * @return query JSONObject inside root
     */
    public JSONObject getQuery() {
        return this.query;
    }

    public void setFrom(int from) {
        try {
            root.put("from", from);
        } catch (JSONException e) {
            Log.e("SearchQuery", e.getMessage(), e);
        }
    }

    /**
     * Set max result size
     * @param size Maximum result size
     */
    public void setSize(int size) {
        try {
            root.put("size", size);
        } catch (JSONException e) {
            Log.e("SearchQuery", e.getMessage(), e);
        }
    }

    public void addSearchSort(SearchSort searchSort){
        try {
            root.put("sort", searchSort.getSort());
        } catch (JSONException e) {
            Log.e("SearchQuery", e.getMessage(), e);
        }
    }


}
