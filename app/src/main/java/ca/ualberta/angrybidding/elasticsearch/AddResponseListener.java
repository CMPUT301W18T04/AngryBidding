package ca.ualberta.angrybidding.elasticsearch;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class AddResponseListener extends ElasticSearchResponseListener {

    @Override
    public void onResponse(JSONObject response) {
        try {
            boolean created = response.getBoolean("created");
            if(created){
                onCreated(response.getString("_id"));
            }else{
                onNotCreated();
            }
        } catch (JSONException e) {
            onErrorResponse(new VolleyError(e));
        }
    }

    public abstract void onCreated(String id);

    public abstract void onNotCreated();
}
