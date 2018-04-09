package ca.ualberta.angrybidding.elasticsearch;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Nested Condition for searching nested field type
 */
public class NestedCondition extends Condition {
    private JSONObject nested;

    /**
     * @param path Path for the nested query
     * @param query The nested query
     */
    public NestedCondition(String path, SearchQuery query) {
        nested = new JSONObject();
        try {
            nested.put("path", path);
            nested.put("query", query.getQuery());
            getRoot().put("nested", nested);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
