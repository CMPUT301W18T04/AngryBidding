package ca.ualberta.angrybidding.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.PopupMenu;

import com.slouple.android.widget.button.PopupMenuButton;

import ca.ualberta.angrybidding.Bid;
import ca.ualberta.angrybidding.ElasticSearchTask;
import ca.ualberta.angrybidding.R;

/**
 * Represents a view of the task object
 * Part of a list
 */
public class BidView extends FrameLayout {
    protected Bid bid;
    protected ElasticSearchTask elasticSearchTask;

    protected FrameLayout container;
    protected TextView usernameTextView;
    protected TextView priceTextView;
    protected PopupMenuButton popupMenuButton;

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
        popupMenuButton = findViewById(R.id.BidPopupMenuButton);
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
        popupMenuButton.setVisibility(View.GONE);
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

    public PopupMenuButton getPopupMenuButton() {
        return popupMenuButton;
    }

    public void useBidPopupMenu(final Bid bid, final OnBidActionListener listener) {
        this.bid = bid;
        popupMenuButton.setVisibility(View.VISIBLE);
        getPopupMenuButton().getPopupMenu().getMenu().clear();
        getPopupMenuButton().setMenuRes(R.menu.bid_popup);
        getPopupMenuButton().getPopupMenu().setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.BidPopupAccept:
                        listener.onAccept();
                        break;

                    case R.id.BidPopupDecline:
                        listener.onDecline();
                        break;
                }

                return true;
            }
        });
    }

    public interface OnBidActionListener {
        void onDecline();
        void onAccept();
    }
}
