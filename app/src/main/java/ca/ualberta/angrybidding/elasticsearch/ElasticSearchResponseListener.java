package ca.ualberta.angrybidding.elasticsearch;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

public abstract class ElasticSearchResponseListener implements Response.Listener<JSONObject>, Response.ErrorListener {
    @Override
    public abstract void onResponse(JSONObject response);

    @Override
    public abstract void onErrorResponse(VolleyError error);
}
