package ca.ualberta.angrybidding.elasticsearch;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    public JSONObject getBool() {
        return this.bool;
    }

    public JSONArray getMust() {
        return this.must;
    }
}
