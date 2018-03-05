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
    }

    public void addMatch(String field, String value) {
        try {
            JSONObject match;
            if (query.has("match")) {
                match = new JSONObject();
                query.put("match", match);
            } else {
                match = query.getJSONObject("match");
            }
            match.put(field, value);

        } catch (JSONException e) {
            Log.e("SearchQuery", e.getMessage(), e);
        }
    }

    public void addTerm(String field, String value) {
        try {
            JSONObject term;
            if (query.has("term")) {
                term = new JSONObject();
                query.put("term", term);
            } else {
                term = query.getJSONObject("term");
            }
            term.put(field, value);

        } catch (JSONException e) {
            Log.e("SearchQuery", e.getMessage(), e);
        }
    }

    public void addRange(String field, String value, double from, double to) {
        addRange(field, value, String.valueOf(from), String.valueOf(to));
    }

    public void addRange(String field, String value, String from, String to) {
        try {
            JSONObject range;
            if (query.has("range")) {
                range = new JSONObject();
                query.put("range", range);
            } else {
                range = query.getJSONObject("range");
            }
            JSONObject condition = new JSONObject();
            condition.put("gte", from);
            condition.put("lte", to);
            range.put(field, condition);
        } catch (JSONException e) {
            Log.e("SearchQuery", e.getMessage(), e);
        }
    }

    public JSONObject getRoot() {
        return this.root;
    }

    public JSONObject getQuery() {
        return this.query;
    }


}
