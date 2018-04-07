package ca.ualberta.angrybidding.elasticsearch;

import android.content.Context;

import com.android.volley.toolbox.JsonObjectRequest;
import com.slouple.android.VolleySingleton;

import org.json.JSONObject;

/**
 * An ElasticSearch Request
 * Sends an json body to the server with index
 */
public class ElasticSearchRequest extends JsonObjectRequest {
    //Default ElasticSearch URL
    public static final String DEFAULT_URL = "http://cosh.em.slouple.com:19200";

    protected String url;
    protected String index;
    protected String type;
    protected String id;

    /**
     * @param method   HTTP Method. Can be POST, GET, PUT, DELETE etc.
     * @param index    Index of which the request will be sent to
     * @param id       ID of the object, can be null
     * @param body     Request body, can be null
     * @param listener Response listener
     */
    public ElasticSearchRequest(int method, String index, String id, JSONObject body, ElasticSearchResponseListener listener) {
        this(method, null, index, id, body, listener);
    }

    /**
     * @param url      ElasticSearch Server URL
     * @param method   HTTP Method. Can be POST, GET, PUT, DELETE etc.
     * @param index    Index of which the request will be sent to
     * @param id       ID of the object, can be null
     * @param body     Request body, can be null
     * @param listener Response listener
     */
    public ElasticSearchRequest(int method, String url, String index, String id, JSONObject body, ElasticSearchResponseListener listener) {
        super(method, null, body, listener, listener);
        this.url = url == null ? DEFAULT_URL : url;
        this.index = index;
        this.type = "_doc"; //New versions of ElasticSearch removed type
        this.id = id;
    }

    /**
     * @return Index
     */
    public String getIndex() {
        return this.index;
    }

    /**
     * @return Type. Will always default to _doc. Should not be changed unless support older ElasticSearch versions
     */
    public String getType() {
        return this.type;
    }

    /**
     * @return ID of the object
     */
    public String getID() {
        return this.getID();
    }

    /**
     * Overrides Volley method so it can be updated after request is constructed
     *
     * @return Full URL of the request
     */
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

    /**
     * Submit this request using VolleySingleton
     *
     * @param context Activity or ApplicationContext
     */
    public void submit(Context context) {
        VolleySingleton.getInstance(context).addToRequestQueue(this);
    }
}
