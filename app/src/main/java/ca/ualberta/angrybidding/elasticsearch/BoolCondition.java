package ca.ualberta.angrybidding.elasticsearch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BoolCondition extends Condition {
    private JSONObject bool;
    private JSONArray must;
    private JSONArray mustNot;
    private JSONArray should;

    public BoolCondition() {
        bool = new JSONObject();
        must = new JSONArray();
        mustNot = new JSONArray();
        should = new JSONArray();

        try {
            getRoot().put("bool", bool);
            bool.put("must", must);
            bool.put("must_not", mustNot);
            bool.put("should", should);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addMust(Condition condition) {
        must.put(condition.getRoot());
    }

    public void addMustNot(Condition condition) {
        mustNot.put(condition.getRoot());
    }

    public void addShould(Condition condition) {
        should.put(condition.getRoot());
    }

    public JSONObject getBool() {
        return this.bool;
    }
}
