package ca.ualberta.angrybidding.elasticsearch;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Listener for AddRequest
 */
public abstract class UpdateResponseListener extends ElasticSearchResponseListener {

    /**
     * Unpacks response
     * Calls onCreate if object not found and onUpdate if found
     * @param response
     */
    @Override
    public void onResponse(JSONObject response) {
        try {
            String result = response.getString("result");
            if (result.equals("created")) {
                onCreated(response.getString("_id"));
            } else if (result.equals("updated")) {
                onUpdated(response.getInt("_version"));
            } else {
                onErrorResponse(new VolleyError("Unknown ElasticSearch Update Result"));
            }
        } catch (JSONException e) {
            onErrorResponse(new VolleyError(e));
        }
    }

    /**
     * Calls when object with ID is not found
     * @param id ID of the object
     */
    public abstract void onCreated(String id);

    /**
     * Calls when object with ID is found and updated
     * @param version version of the object
     */
    public abstract void onUpdated(int version);
}
