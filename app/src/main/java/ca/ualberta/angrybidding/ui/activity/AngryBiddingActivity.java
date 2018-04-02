package ca.ualberta.angrybidding.ui.activity;

import com.slouple.android.AdvancedActivity;

import ca.ualberta.angrybidding.ElasticSearchUser;

/**
 * Base activity for all activities used
 * Allows common methods
 */
public class AngryBiddingActivity extends AdvancedActivity {

    /**
     * @return The current logged in ElasticSearchUser
     */
    public ElasticSearchUser getElasticSearchUser() {
        return ElasticSearchUser.getMainUser(this);
    }

    /**
     * Hides getUser from AdvancedActivity
     * Returns null. Do not use.
     *
     * @return null
     */
    @Deprecated
    public com.slouple.lib.User getUser() {
        return null;
    }
}
