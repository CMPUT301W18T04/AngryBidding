package ca.ualberta.angrybidding.elasticsearch;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

/**
 * Listener for ElasticSearchRequest
 */
public abstract class ElasticSearchResponseListener implements Response.Listener<JSONObject>, Response.ErrorListener {

    /**
     * Called when a response is returned from the server.
     *
     * @param response Returned json response from server
     */
    @Override
    public abstract void onResponse(JSONObject response);

    /**
     * Called when there is an error regarding to sending the request
     *
     * @param error VolleyError
     */
    @Override
    public abstract void onErrorResponse(VolleyError error);
}
