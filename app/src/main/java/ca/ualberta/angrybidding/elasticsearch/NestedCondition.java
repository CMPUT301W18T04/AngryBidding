package ca.ualberta.angrybidding.elasticsearch;

import org.json.JSONException;
import org.json.JSONObject;

public class NestedCondition extends Condition{
    private JSONObject nested;

    public NestedCondition(String path, SearchQuery query){
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
