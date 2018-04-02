package ca.ualberta.angrybidding.elasticsearch;

import org.json.JSONObject;

/**
 * Add Request For Elastic Search
 */
public class AddRequest extends ElasticSearchRequest {

    /**
     * @param index    ElasticSearch Index
     * @param body     Object to add
     * @param listener Listener to call on response
     */
    public AddRequest(String index, JSONObject body, AddResponseListener listener) {
        this(null, index, body, listener);
    }

    /**
     * @param url      ElasticSearch Server URL
     * @param index    ElasticSearch Index
     * @param body     Object to add
     * @param listener Listener to call on response
     */
    public AddRequest(String url, String index, JSONObject body, AddResponseListener listener) {
        super(Method.POST, url, index, null, body, listener);
    }
}
