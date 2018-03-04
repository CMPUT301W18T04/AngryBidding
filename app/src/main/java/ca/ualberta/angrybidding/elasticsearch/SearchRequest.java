package ca.ualberta.angrybidding.elasticsearch;

import org.json.JSONObject;

public class SearchRequest extends ElasticSearchRequest {

    public SearchRequest(String index, String type, JSONObject body, SearchResponseListener listener) {
        this(null, index, type, body, listener);
    }

    public SearchRequest(String url, String index, String type, JSONObject body, SearchResponseListener listener) {
        super(Method.GET, url, index, type, null, body, listener);
    }
}
