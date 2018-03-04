package ca.ualberta.angrybidding.elasticsearch;

import org.json.JSONObject;

public class GetRequest extends ElasticSearchRequest {

    public GetRequest(String index, String type, String id, GetResponseListener listener) {
        this(null, index, type, id, listener);
    }

    public GetRequest(String url, String index, String type, String id, GetResponseListener listener) {
        super(Method.GET, url, index, type, id, null, listener);
    }

}
