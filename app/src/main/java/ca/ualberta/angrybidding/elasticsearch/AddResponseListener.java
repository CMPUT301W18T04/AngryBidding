package ca.ualberta.angrybidding.elasticsearch;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Listener for AddRequest
 */
public abstract class AddResponseListener extends ElasticSearchResponseListener {

    /**
     * Unpack response and call onCreated with ID
     * @param response
     */
    @Override
    public void onResponse(JSONObject response) {
        try {
            onCreated(response.getString("_id"));
        } catch (JSONException e) {
            onErrorResponse(new VolleyError(e));
        }
    }

    /**
     * Called when object is add into ElasticSearch
     * @param id ID for the newly add object
     */
    public abstract void onCreated(String id);
}
