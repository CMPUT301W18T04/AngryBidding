package ca.ualberta.angrybidding.elasticsearch;

import org.json.JSONObject;

public class AddRequest extends ElasticSearchRequest {

    public AddRequest(String index, JSONObject body, AddResponseListener listener) {
        this(null, index, body, listener);
    }

    public AddRequest(String url, String index, JSONObject body, AddResponseListener listener) {
        super(Method.POST, url, index, null, body, listener);
    }
}
