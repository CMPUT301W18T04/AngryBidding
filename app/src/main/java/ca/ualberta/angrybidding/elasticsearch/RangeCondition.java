package ca.ualberta.angrybidding.elasticsearch;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class RangeCondition extends Condition{
    private JSONObject range;
    private JSONObject conditions;

    public RangeCondition(String fieldName, String from, String to){
        range = new JSONObject();
        conditions = new JSONObject();
        try {
            conditions.put("gte", from);
            conditions.put("lte", to);
            range.put(fieldName, conditions);
            getRoot().put("range", range);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
