package ca.ualberta.angrybidding.elasticsearch;

import android.content.Context;

import com.android.volley.toolbox.JsonObjectRequest;
import com.slouple.android.VolleySingleton;

import org.json.JSONObject;


public class ElasticSearchRequest extends JsonObjectRequest {
    public static final String DEFAULT_URL = "http://cosh.em.slouple.com:19200";

    protected String url;
    protected String index;
    protected String type;
    protected String id;

    public ElasticSearchRequest(int method, String index, String id, JSONObject body, ElasticSearchResponseListener listener) {
        this(method, null, index, id, body, listener);
    }

    public ElasticSearchRequest(int method, String url, String index, String id, JSONObject body, ElasticSearchResponseListener listener) {
        super(method, null, body, listener, listener);
        this.url = url == null ? DEFAULT_URL : url;
        this.index = index;
        this.type = "_doc";
        this.id = id;
    }

    public String getIndex() {
        return this.index;
    }

    public String getType() {
        return this.type;
    }

    public String getID() {
        return this.getID();
    }

    @Override
    public String getUrl() {
        StringBuilder builder = new StringBuilder();
        builder.append(url);
        if (url.endsWith("/")) {
            builder.deleteCharAt(url.length() - 1);
        }
        if (index != null) {
            builder.append("/");
            builder.append(index);
        }

        if (type != null) {
            builder.append("/");
            builder.append(type);
        }

        if (id != null) {
            builder.append("/");
            builder.append(id);
        }

        return builder.toString();
    }

    public void submit(Context context) {
        VolleySingleton.getInstance(context).addToRequestQueue(this);
    }
}
