package ca.ualberta.angrybidding.elasticsearch;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * RangeCondition with greater/less than or equal
 */
public class RangeCondition extends Condition {
    private JSONObject range;
    private JSONObject conditions;

    /**
     * @param fieldName Field name to search
     * @param from greater or equal to from
     * @param to less or equal to to
     */
    public RangeCondition(String fieldName, String from, String to) {
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
