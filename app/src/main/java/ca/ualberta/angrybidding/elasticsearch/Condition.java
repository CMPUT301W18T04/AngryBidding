package ca.ualberta.angrybidding.elasticsearch;

import org.json.JSONObject;

public abstract class Condition {
    private JSONObject root = new JSONObject();
    public JSONObject getRoot(){
        return this.root;
    }
}
