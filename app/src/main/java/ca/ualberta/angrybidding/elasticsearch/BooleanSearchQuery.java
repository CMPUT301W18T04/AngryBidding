package ca.ualberta.angrybidding.elasticsearch;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Basic ElasticSearch Search Query
 */
public class BooleanSearchQuery extends SearchQuery {
    private JSONObject constantScore;
    private JSONObject filter;
    private BoolCondition boolCondition;

    public BooleanSearchQuery() {
        super();
        try {
            constantScore = new JSONObject();
            filter = new JSONObject();
            constantScore.put("filter", filter);
            getQuery().put("constant_score", constantScore);

            boolCondition = new BoolCondition();
            filter.put("bool", boolCondition.getBool());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public BoolCondition getBoolCondition() {
        return this.boolCondition;
    }
}
