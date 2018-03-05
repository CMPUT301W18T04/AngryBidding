package ca.ualberta.angrybidding.elasticsearch;

import org.json.JSONObject;

public class SearchRequest extends ElasticSearchRequest {

    public SearchRequest(String index, SearchQuery searchQuery, SearchResponseListener listener) {
        this(index, searchQuery.getRoot(), listener);
    }

    public SearchRequest(String index, JSONObject body, SearchResponseListener listener) {
        this(null, index, body, listener);
    }

    public SearchRequest(String url, String index, SearchQuery searchQuery, SearchResponseListener listener) {
        this(url, index, searchQuery.getRoot(), listener);
    }

    public SearchRequest(String url, String index, JSONObject body, SearchResponseListener listener) {
        super(Method.POST, url, index, null, body, listener);
    }

    @Override
    public String getUrl() {
        return super.getUrl() + "/_search";
    }
}
