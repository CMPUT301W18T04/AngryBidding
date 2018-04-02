package ca.ualberta.angrybidding.elasticsearch;

/**
 * Delete Request for ElasticSearch
 */
public class DeleteRequest extends ElasticSearchRequest {

    /**
     * @param index    ElasticSearch Index
     * @param id       ID of the object to delete
     * @param listener Listener to call on response
     */
    public DeleteRequest(String index, String id, DeleteResponseListener listener) {
        this(null, index, id, listener);
    }

    /**
     * @param url      ElasticSearch Server URL
     * @param index    ElasticSearch Index
     * @param id       ID of the object to delete
     * @param listener Listener to call on response
     */
    public DeleteRequest(String url, String index, String id, DeleteResponseListener listener) {
        super(Method.DELETE, url, index, id, null, listener);
    }
}
