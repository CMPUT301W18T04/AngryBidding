package ca.ualberta.angrybidding.elasticsearch;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Match Condition for Search Query
 */
public class MatchCondition extends Condition {
    private JSONObject match;

    /**
     * Match condition
     * @param fieldName Field name to match
     * @param fieldValue Field value to match
     */
    public MatchCondition(String fieldName, String fieldValue) {
        match = new JSONObject();

        try {
            match.put(fieldName, fieldValue);
            getRoot().put("match", match);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
