package ca.ualberta.angrybidding.elasticsearch;

import org.json.JSONException;
import org.json.JSONObject;

public class MatchCondition extends Condition{
    private JSONObject match;

    public MatchCondition(String fieldName, String fieldValue){
        match = new JSONObject();

        try {
            match.put(fieldName, fieldValue);
            getRoot().put("match", match);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
