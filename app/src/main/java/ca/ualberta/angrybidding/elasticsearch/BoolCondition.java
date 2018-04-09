package ca.ualberta.angrybidding.elasticsearch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Boolean condition for search query with must, must_not and should
 */
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

    /**
     * Add a must condition
     * @param condition Condition to add
     */
    public void addMust(Condition condition) {
        must.put(condition.getRoot());
    }

    /**
     * Add a must_not condition
     * @param condition Condition to add
     */
    public void addMustNot(Condition condition) {
        mustNot.put(condition.getRoot());
    }

    /**
     * Add a should condition
     * @param condition Condition to add
     */
    public void addShould(Condition condition) {
        should.put(condition.getRoot());
    }

    /**
     * @return root bool JSONObject
     */
    public JSONObject getBool() {
        return this.bool;
    }
}
