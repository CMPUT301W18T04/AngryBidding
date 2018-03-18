package ca.ualberta.angrybidding.elasticsearch;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

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
        setSize(1000);
    }

    public JSONObject getRoot() {
        return this.root;
    }

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

    public void setSize(int size) {
        try {
            root.put("size", size);
        } catch (JSONException e) {
            Log.e("SearchQuery", e.getMessage(), e);
        }
    }


}
