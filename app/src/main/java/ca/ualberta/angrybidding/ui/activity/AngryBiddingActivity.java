package ca.ualberta.angrybidding.ui.activity;

import com.slouple.android.AdvancedActivity;

import ca.ualberta.angrybidding.ElasticSearchUser;

public class AngryBiddingActivity extends AdvancedActivity {

    public ElasticSearchUser getElasticSearchUser() {
        return ElasticSearchUser.getMainUser(this);
    }

    @Deprecated
    public com.slouple.lib.User getUser() {
        return null;
    }
}
