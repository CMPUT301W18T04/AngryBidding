package ca.ualberta.angrybidding.elasticsearch;

public class DeleteRequest extends ElasticSearchRequest {

    public DeleteRequest(String index, String id, DeleteResponseListener listener) {
        this(null, index, id, listener);
    }

    public DeleteRequest(String url, String index, String id, DeleteResponseListener listener) {
        super(Method.DELETE, url, index, id, null, listener);
    }
}
