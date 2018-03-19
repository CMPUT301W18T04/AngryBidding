package ca.ualberta.angrybidding.elasticsearch;

/**
 * Add Request For Elastic Search
 */
public class GetRequest extends ElasticSearchRequest {

    /**
     * @param index ElasticSearch Index
     * @param id ID of Object to get
     * @param listener Listener to call on response
     */
    public GetRequest(String index, String id, GetResponseListener listener) {
        this(null, index, id, listener);
    }

    /**
     * @param url ElasticSearch Server URL
     * @param index ElasticSearch Index
     * @param id ID of Object to get
     * @param listener Listener to call on response
     */
    public GetRequest(String url, String index, String id, GetResponseListener listener) {
        super(Method.GET, url, index, id, null, listener);
    }

}
