package ca.ualberta.angrybidding.elasticsearch;

import org.json.JSONObject;

/**
 * Search Request For Elastic Search
 */
public class SearchRequest extends ElasticSearchRequest {

    /**
     * @param index ElasticSearch Index
     * @param searchQuery Query submits to the server
     * @param listener Listener to call on response
     */
    public SearchRequest(String index, SearchQuery searchQuery, SearchResponseListener listener) {
        this(index, searchQuery.getRoot(), listener);
    }

    /**
     * @param index ElasticSearch Index
     * @param body Json query body
     * @param listener Listener to call on response
     */
    public SearchRequest(String index, JSONObject body, SearchResponseListener listener) {
        this(null, index, body, listener);
    }

    /**
     * @param url ElasticSearch Server URL
     * @param index ElasticSearch Index
     * @param searchQuery Query submits to the server
     * @param listener Listener to call on response
     */
    public SearchRequest(String url, String index, SearchQuery searchQuery, SearchResponseListener listener) {
        this(url, index, searchQuery.getRoot(), listener);
    }

    /**
     * @param url ElasticSearch Server URL
     * @param index ElasticSearch Index
     * @param body Json query body
     * @param listener Listener to call on response
     */
    public SearchRequest(String url, String index, JSONObject body, SearchResponseListener listener) {
        super(Method.POST, url, index, null, body, listener);
    }

    /**
     * Return URL with appended /_search to the end of url
     * @return URL with appended /_search
     */
    @Override
    public String getUrl() {
        return super.getUrl() + "/_search";
    }
}
