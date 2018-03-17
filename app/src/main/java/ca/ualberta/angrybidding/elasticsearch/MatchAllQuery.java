package ca.ualberta.angrybidding.elasticsearch;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MatchAllQuery extends SearchQuery {
    private JSONObject matchAll;

    public MatchAllQuery() {
        super();
        try {
            matchAll = new JSONObject();
            getQuery().put("match_all", matchAll);
        } catch (JSONException e) {
            Log.e("MatchAllQuery", e.getMessage(), e);
        }
    }

    public JSONObject getMatchAll() {
        return this.matchAll;
    }

}
