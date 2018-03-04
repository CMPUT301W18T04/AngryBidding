package ca.ualberta.angrybidding.elasticsearch;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

public class UpdateRequest extends ElasticSearchRequest {

    public UpdateRequest(String index, String type, String id, JSONObject body, ElasticSearchResponseListener listener) {
        this(null, index, type, id, body, listener);
    }

    public UpdateRequest(String url, String index, String type, String id, JSONObject body, ElasticSearchResponseListener listener) {
        super(Method.POST, url, index, type, id, body, listener);
    }

}
