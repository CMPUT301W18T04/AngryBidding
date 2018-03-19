package ca.ualberta.angrybidding.elasticsearch;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Get all data that passes all matches
 */
public class MatchAndQuery extends SearchQuery {
    private JSONObject bool;
    private JSONArray must;

    public MatchAndQuery() {
        super();
        try {
            bool = new JSONObject();
            getQuery().put("bool", bool);
            must = new JSONArray();
            bool.put("must", must);
        } catch (JSONException e) {
            Log.e("MatchAndQuery", e.getMessage(), e);
        }
    }

    /**
     * Add a match for the query
     * @param field Field name
     * @param value Field value to match
     */
    public void addMatch(String field, String value) {
        try {
            JSONObject match = new JSONObject();
            JSONObject fieldPair = new JSONObject();
            fieldPair.put(field, value);
            match.put("match", fieldPair);
            must.put(match);
        } catch (JSONException e) {
            Log.e("MatchAndQuery", e.getMessage(), e);
        }
    }

    /**
     * @return bool JSONObject inside query
     */
    public JSONObject getBool() {
        return this.bool;
    }

    /**
     * @return must JSONObject inside bool
     */
    public JSONArray getMust() {
        return this.must;
    }
}
