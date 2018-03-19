package ca.ualberta.angrybidding.elasticsearch;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Listener for SearchRequest
 */
public abstract class SearchResponseListener extends ElasticSearchResponseListener {

    /**
     * Unpacks response into SearchResult
     * @param response Returned json response from server
     */
    @Override
    public void onResponse(JSONObject response) {
        try {
            onResult(new SearchResult(response));
        } catch (JSONException e) {
            onErrorResponse(new VolleyError(e));
        }
    }

    /**
     * Calls when search result is returned from the server
     * @param searchResult SearchResult object that contains all the info from the server
     */
    public abstract void onResult(SearchResult searchResult);
}
