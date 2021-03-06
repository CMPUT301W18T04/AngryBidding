package ca.ualberta.angrybidding.elasticsearch;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Sorting order for search result
 */
public class SearchSort {
    private JSONArray sort;

    public SearchSort() {
        sort = new JSONArray();
    }

    /**
     * @param fieldName Field to sort by
     * @param order ASC or DESC
     */
    public void addField(String fieldName, Order order) {
        try {
            JSONObject field = new JSONObject();
            JSONObject orderJson = new JSONObject();
            orderJson.put("order", order.toString().toLowerCase());
            field.put(fieldName, orderJson);
            sort.put(field);
        } catch (JSONException e) {
            Log.e("SearchSort", e.getMessage(), e);
        }
    }

    /**
     * @return sort JSONArray inside root
     */
    public JSONArray getSort() {
        return this.sort;
    }

    public enum Order {
        ASC,
        DESC
    }
}
