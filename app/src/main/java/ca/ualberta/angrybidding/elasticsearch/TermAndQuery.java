package ca.ualberta.angrybidding.elasticsearch;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Get all data that passes all terms
 */
public class TermAndQuery extends SearchQuery {
    private JSONObject bool;
    private JSONArray must;

    public TermAndQuery() {
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

    /**
     * Add a term for the query
     *
     * @param field Field name
     * @param value Field value to match
     */
    public void addTerm(String field, String value) {
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

    /**
     * Add a range for the query
     *
     * @param field Field name
     * @param from  Min inclusive value
     * @param to    Max inclusive value
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
            must.put(range);
        } catch (JSONException e) {
            Log.e("TermAndQuery", e.getMessage(), e);
        }
    }

    @Override
    public void addNestedQuery(String path, SearchQuery searchQuery) {
        try {
            JSONObject wrapper = new JSONObject();
            JSONObject nested = new JSONObject();
            nested.put("path", path);
            nested.put("query", searchQuery.getQuery());
            wrapper.put("nested", nested);
            must.put(wrapper);
        } catch (JSONException e) {
            Log.e("SearchQuery", e.getMessage(), e);
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
