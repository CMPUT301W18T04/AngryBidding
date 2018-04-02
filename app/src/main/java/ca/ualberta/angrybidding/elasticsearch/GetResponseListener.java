package ca.ualberta.angrybidding.elasticsearch;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Listener for AddRequest
 */
public abstract class GetResponseListener extends ElasticSearchResponseListener {

    /**
     * Unpack response and call onFound or onNotFound depending on the response
     *
     * @param response
     */
    @Override
    public void onResponse(JSONObject response) {
        try {
            boolean created = response.getBoolean("found");
            if (created) {
                onFound(response.getJSONObject("_source"));
            } else {
                onNotFound();
            }
        } catch (JSONException e) {
            onErrorResponse(new VolleyError(e));
        }
    }

    /**
     * Calls when the object corresponding to the ID is found
     *
     * @param object Object corresponding to the ID returned from the server
     */
    public abstract void onFound(JSONObject object);

    /**
     * Calls when the object corresponding to the ID is not found
     */
    public abstract void onNotFound();
}
