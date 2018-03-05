package ca.ualberta.angrybidding.elasticsearch;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TermOrQuery extends SearchQuery{
    private JSONObject disMax;
    private JSONArray queries;

    public TermOrQuery(){
        super();
        try {
            disMax = new JSONObject();
            getQuery().put("dis_max", disMax);
            queries = new JSONArray();
            disMax.put("queries", queries);
        } catch (JSONException e) {
            Log.e("TermOrQuery", e.getMessage(), e);
        }
    }

    public void addTerm(String field, String value){
        try {
            JSONObject term = new JSONObject();
            JSONObject fieldPair = new JSONObject();
            fieldPair.put(field, value);
            term.put("term", fieldPair);
            queries.put(term);
        } catch (JSONException e) {
            Log.e("TermOrQuery", e.getMessage(), e);
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
            queries.put(range);
        } catch (JSONException e) {
            Log.e("TermAndQuery", e.getMessage(), e);
        }
    }

    public JSONObject getDisMax() {
        return this.disMax;
    }
    public JSONArray getQueries() {
        return this.queries;
    }
}
