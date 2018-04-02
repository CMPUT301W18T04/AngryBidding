package ca.ualberta.angrybidding.elasticsearch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchResult {

    private int took;
    private boolean timedOut;
    private int hitCount;
    private double maxScore;
    private ArrayList<SearchResultObject> searchResultObjects = new ArrayList<>();

    /**
     * @param jsonObject JsonObject to unpack
     * @throws JSONException
     */
    public SearchResult(JSONObject jsonObject) throws JSONException {
        this.took = jsonObject.getInt("took");
        this.timedOut = jsonObject.getBoolean("timed_out");
        jsonObject = jsonObject.getJSONObject("hits");
        this.hitCount = jsonObject.getInt("total");
        if (this.hitCount > 0 && !jsonObject.isNull("max_score")) {
            this.maxScore = jsonObject.getDouble("max_score");
        } else {
            this.maxScore = -1;
        }
        JSONArray hits = jsonObject.getJSONArray("hits");
        for (int i = 0; i < hits.length(); i++) {
            JSONObject hit = hits.getJSONObject(i);
            searchResultObjects.add(new SearchResultObject(hit));
        }
    }

    /**
     * @return The amount of time search took
     */
    public int getTook() {
        return took;
    }

    /**
     * @return Has search timed out
     */
    public boolean isTimedOut() {
        return timedOut;
    }

    /**
     * @return Number of objects matches the query
     */
    public int getHitCount() {
        return hitCount;
    }

    /**
     * @return Max score of objects from the result
     */
    public double getMaxScore() {
        return maxScore;
    }

    /**
     * @return List of objects returned from the query. List size can be smaller than HitCount because of Request Size Limit
     */
    public ArrayList<SearchResultObject> getSearchResultObjects() {
        return searchResultObjects;
    }

    public class SearchResultObject {
        private String index;
        private String type;
        private String id;
        private double score;
        private JSONObject source;

        /**
         * Unpacks SearchResult hit
         *
         * @param jsonObject Object to unpack
         * @throws JSONException
         */
        public SearchResultObject(JSONObject jsonObject) throws JSONException {
            this.index = jsonObject.getString("_index");
            this.type = jsonObject.getString("_type");
            this.id = jsonObject.getString("_id");
            this.score = jsonObject.isNull("_score") ? -1 : jsonObject.getDouble("_score");
            this.source = jsonObject.getJSONObject("_source");
        }

        /**
         * @return Index of the server which this object belongs to
         */
        public String getIndex() {
            return this.index;
        }

        /**
         * @return The type which this object belongs to
         */
        public String getType() {
            return type;
        }

        /**
         * @return ID of the object
         */
        public String getId() {
            return id;
        }

        /**
         * @return Search score of this object
         */
        public double getScore() {
            return score;
        }

        /**
         * @return The object found
         */
        public JSONObject getSource() {
            return source;
        }
    }
}
