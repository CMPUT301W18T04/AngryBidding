package ca.ualberta.angrybidding.elasticsearch;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class GetResponseListener extends ElasticSearchResponseListener {

    @Override
    public void onResponse(JSONObject response) {
        try {
            boolean created = response.getBoolean("found");
            if(created){
                onFound(response.getJSONObject("_source"));
            }else{
                onNotFound();
            }
        } catch (JSONException e) {
            onErrorResponse(new VolleyError(e));
        }
    }

    public abstract void onFound(JSONObject object);

    public abstract void onNotFound();
}
