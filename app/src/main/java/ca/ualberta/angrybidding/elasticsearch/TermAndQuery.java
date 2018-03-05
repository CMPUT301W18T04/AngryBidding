package ca.ualberta.angrybidding.elasticsearch;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TermAndQuery extends SearchQuery{
    private JSONObject bool;
    private JSONArray must;

    public TermAndQuery(){
        super();
        try {
            bool = new JSONObject();
            getQuery().put("bool", bool);
            must = new JSONArray();
            bool.put("must", must);
        } catch (JSONException e) {
            Log.e("TermAndQuery", e.getMessage(), e);
        }
    }

    public void addTerm(String field, String value){
        try {
            JSONObject term = new JSONObject();
            JSONObject fieldPair = new JSONObject();
            fieldPair.put(field, value);
            term.put("term", fieldPair);
            must.put(term);
        } catch (JSONException e) {
            Log.e("TermAndQuery", e.getMessage(), e);
        }
    }

    public void addRange(String field, String value, String from, String to) {
        try {
            JSONObject range = new JSONObject();
            JSONObject fieldJson = new JSONObject();
            JSONObject conditions = new JSONObject();
            conditions.put("gte", from);
            conditions.put("lte", to);
            fieldJson.put(field, conditions);
            range.put("range", fieldJson);
            must.put(range);
        } catch (JSONException e) {
            Log.e("TermAndQuery", e.getMessage(), e);
        }
    }

    public JSONObject getBool() {
        return this.bool;
    }
    public JSONArray getMust() {
        return this.must;
    }
}
