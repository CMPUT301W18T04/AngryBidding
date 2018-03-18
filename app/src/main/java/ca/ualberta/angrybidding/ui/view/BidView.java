package ca.ualberta.angrybidding.ui.view;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.slouple.android.AdvancedActivity;
import com.slouple.android.ResultRequest;
import com.slouple.android.widget.button.PopupMenuButton;

import ca.ualberta.angrybidding.Bid;
import ca.ualberta.angrybidding.ElasticSearchTask;
import ca.ualberta.angrybidding.ElasticSearchUser;
import ca.ualberta.angrybidding.R;
import ca.ualberta.angrybidding.elasticsearch.DeleteResponseListener;
import ca.ualberta.angrybidding.ui.activity.AddBidActivity;
import ca.ualberta.angrybidding.ui.activity.ViewTaskDetailActivity;

/**
 * Represents a view of the task object
 * Part of a list
 */
public class BidView extends FrameLayout {
    protected Bid bid;

    protected FrameLayout container;
    protected TextView usernameTextView;
    protected TextView priceTextView;

    public BidView(Context context) {
        this(context, null);
    }

    public BidView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BidView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     * Assigns members to corresponding views
     */
    protected void loadViews() {
        container = (FrameLayout) LayoutInflater.from(getContext()).inflate(R.layout.view_bid, this, true);

        usernameTextView = findViewById(R.id.bidUsername);
        priceTextView = findViewById(R.id.bidPrice);
    }

    /**
     * Initializes the view
     */
    protected void init() {
        loadViews();
    }

    /**
     * Extracts the bid object into the view
     *
     * @param bid The task object
     */
    public void setBid(Bid bid) {
        this.bid = bid;
        usernameTextView.setText(bid.getUser().getUsername());
        priceTextView.setText(bid.getPriceString());
    }

    public FrameLayout getContainer() {
        return container;
    }

    public Bid getBid() {
        return bid;
    }

    public TextView getUsernameTextView() {
        return usernameTextView;
    }

    public TextView getPriceTextView() {
        return priceTextView;
    }
}
