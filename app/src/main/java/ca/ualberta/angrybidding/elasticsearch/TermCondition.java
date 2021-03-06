package ca.ualberta.angrybidding.elasticsearch;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Term Condition for search query
 */
public class TermCondition extends Condition {
    private JSONObject term;


    /**
     * @param fieldName Field name for term
     * @param fieldValue Field value for term
     */
    public TermCondition(String fieldName, String fieldValue) {
        term = new JSONObject();

        try {
            term.put(fieldName, fieldValue);
            getRoot().put("term", term);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
