package ca.ualberta.angrybidding.elasticsearch;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class UpdateResponseListener extends ElasticSearchResponseListener {

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

    public abstract void onCreated(String id);

    public abstract void onUpdated(int version);
}
