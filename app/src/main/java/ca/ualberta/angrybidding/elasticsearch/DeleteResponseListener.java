package ca.ualberta.angrybidding.elasticsearch;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Listener for DeleteRequest
 */
public abstract class DeleteResponseListener extends ElasticSearchResponseListener {

    /**
     * Unpacks response and calls onDelete if found and onNotFound when object with the corresponding ID not found
     *
     * @param response
     */
    @Override
    public void onResponse(JSONObject response) {
        try {
            String result = response.getString("result");
            if (result.equals("not_found")) {
                onNotFound();
            } else if (result.equals("deleted")) {
                onDeleted(response.getString("_id"));
            }
        } catch (JSONException e) {
            onErrorResponse(new VolleyError(e));
        }
    }

    /**
     * Called when object is deleted in ElasticSearch
     *
     * @param id
     */
    public abstract void onDeleted(String id);

    /**
     * Called when object with ID not found
     */
    public abstract void onNotFound();
}
