package ca.ualberta.angrybidding.elasticsearch;

public class GetRequest extends ElasticSearchRequest {

    public GetRequest(String index, String id, GetResponseListener listener) {
        this(null, index, id, listener);
    }

    public GetRequest(String url, String index, String id, GetResponseListener listener) {
        super(Method.GET, url, index, id, null, listener);
    }

}
