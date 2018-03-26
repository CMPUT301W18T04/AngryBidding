package ca.ualberta.angrybidding.elasticsearch;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Get all data that passes one of the terms
 */
public class TermOrQuery extends SearchQuery {
    private JSONObject disMax;
    private JSONArray queries;

    public TermOrQuery() {
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

    /**
     * Add a term for the query
     * @param field Field name
     * @param value Field value to match
     */
    public void addTerm(String field, String value) {
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

    /**
     * Add a range for the query
     * @param field Field name
     * @param from Min inclusive value
     * @param to Max inclusive value
     */
    public void addRange(String field, String from, String to) {
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

    @Override
    public void addNestedQuery(String path, SearchQuery searchQuery){
        try {
            JSONObject wrapper = new JSONObject();
            JSONObject nested = new JSONObject();
            nested.put("path", path);
            nested.put("query", searchQuery.getQuery());
            wrapper.put("nested", nested);
            queries.put(wrapper);
        } catch (JSONException e) {
            Log.e("SearchQuery", e.getMessage(), e);
        }
    }

    /**
     * @return dis_max JSONObject inside query
     */
    public JSONObject getDisMax() {
        return this.disMax;
    }

    /**
     * @return queries JSONArray inside dis_max
     */
    public JSONArray getQueries() {
        return this.queries;
    }
}
