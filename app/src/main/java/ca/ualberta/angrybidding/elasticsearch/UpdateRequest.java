package ca.ualberta.angrybidding.elasticsearch;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

public class UpdateRequest extends ElasticSearchRequest {

    public UpdateRequest(String index, String id, JSONObject body, UpdateResponseListener listener) {
        this(null, index, id, body, listener);
    }

    public UpdateRequest(String url, String index, String id, JSONObject body, UpdateResponseListener listener) {
        super(Method.POST, url, index, id, body, listener);
    }

}