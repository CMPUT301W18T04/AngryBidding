package ca.ualberta.angrybidding.elasticsearch;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Matches all data inside an index
 */
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

    /**
     * @return match_all JSONObject inside of the query
     */
    public JSONObject getMatchAll() {
        return this.matchAll;
    }

}
