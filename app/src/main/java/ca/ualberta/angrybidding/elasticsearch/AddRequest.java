package ca.ualberta.angrybidding.elasticsearch;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

public class AddRequest extends ElasticSearchRequest {

    public AddRequest(String index, String type, JSONObject body, AddResponseListener listener) {
        this(null, index, type, body, listener);
    }

    public AddRequest(String url, String index, String type, JSONObject body, AddResponseListener listener) {
        super(Method.POST, url, index, type, null, body, listener);
    }
}
