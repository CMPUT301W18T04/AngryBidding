package ca.ualberta.angrybidding.elasticsearch;

import org.json.JSONObject;

/**
 * Base class for all types of conditions for bool
 */
public abstract class Condition {
    private JSONObject root = new JSONObject();

    public JSONObject getRoot() {
        return this.root;
    }
}
