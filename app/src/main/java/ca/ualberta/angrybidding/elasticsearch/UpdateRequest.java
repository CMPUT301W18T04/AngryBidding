package ca.ualberta.angrybidding.elasticsearch;

import org.json.JSONObject;

/**
 * Update Request For Elastic Search
 */
public class UpdateRequest extends ElasticSearchRequest {

    /**
     * @param index    ElasticSearch Index
     * @param id       ID of the object to update
     * @param body     Object to add
     * @param listener Listener to call on response
     */
    public UpdateRequest(String index, String id, JSONObject body, UpdateResponseListener listener) {
        this(null, index, id, body, listener);
    }

    /**
     * @param url      ElasticSearch Server URL
     * @param index    ElasticSearch Index
     * @param id       ID of the object to update
     * @param body     Object to add
     * @param listener Listener to call on response
     */
    public UpdateRequest(String url, String index, String id, JSONObject body, UpdateResponseListener listener) {
        super(Method.POST, url, index, id, body, listener);
    }

}
