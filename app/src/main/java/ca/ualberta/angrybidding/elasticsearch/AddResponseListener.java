package ca.ualberta.angrybidding.elasticsearch;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class AddResponseListener extends ElasticSearchResponseListener {

    @Override
    public void onResponse(JSONObject response) {
        try {
            onCreated(response.getString("_id"));
        } catch (JSONException e) {
            onErrorResponse(new VolleyError(e));
        }
    }

    public abstract void onCreated(String id);
}
