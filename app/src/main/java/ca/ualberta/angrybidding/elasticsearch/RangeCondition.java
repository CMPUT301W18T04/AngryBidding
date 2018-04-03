package ca.ualberta.angrybidding.elasticsearch;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class RangeCondition {
    private JSONObject root;
    private JSONObject range;
    private JSONObject conditions;

    public RangeCondition(String fieldName, String from, String to){
        root = new JSONObject();
        range = new JSONObject();
        conditions = new JSONObject();
        try {
            conditions.put("gte", from);
            conditions.put("lte", to);
            range.put(fieldName, conditions);
            root.put("range", range);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getRoot() {
        return this.root;
    }
}
