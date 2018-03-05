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

    public SearchResult(JSONObject jsonObject) throws JSONException {
        this.took = jsonObject.getInt("took");
        this.timedOut = jsonObject.getBoolean("timed_out");
        jsonObject = jsonObject.getJSONObject("hits");
        this.hitCount = jsonObject.getInt("total");
        if(this.hitCount > 0){
            this.maxScore = jsonObject.getDouble("max_score");
        }else{
            this.maxScore = -1;
        }
        JSONArray hits = jsonObject.getJSONArray("hits");
        for(int i = 0; i < hits.length(); i++){
            JSONObject hit = hits.getJSONObject(i);
            searchResultObjects.add(new SearchResultObject(hit));
        }
    }

    public int getTook() {
        return took;
    }

    public boolean isTimedOut() {
        return timedOut;
    }

    public int getHitCount() {
        return hitCount;
    }

    public double getMaxScore() {
        return maxScore;
    }

    public ArrayList<SearchResultObject> getSearchResultObjects() {
        return searchResultObjects;
    }

    public class SearchResultObject{
        private String index;
        private String type;
        private String id;
        private double score;
        private JSONObject source;

        public SearchResultObject(JSONObject jsonObject) throws JSONException {
            this.index = jsonObject.getString("_index");
            this.type = jsonObject.getString("_type");
            this.id = jsonObject.getString("_id");
            this.score = jsonObject.getDouble("_score");
            this.source = jsonObject.getJSONObject("_source");
        }

        public String getIndex(){
            return this.index;
        }

        public String getType() {
            return type;
        }

        public String getId() {
            return id;
        }

        public double getScore() {
            return score;
        }

        public JSONObject getSource() {
            return source;
        }
    }
}
