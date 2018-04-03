package ca.ualberta.angrybidding.elasticsearch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TermCondition extends Condition{
    private JSONObject term;

    public TermCondition(String fieldName, String fieldValue){
        term = new JSONObject();

        try {
            term.put(fieldName, fieldValue);
            getRoot().put("term", term);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
