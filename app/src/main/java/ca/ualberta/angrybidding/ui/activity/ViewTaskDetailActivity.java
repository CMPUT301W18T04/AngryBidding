package ca.ualberta.angrybidding.ui.activity;

/*import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;*/

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.slouple.android.notification.Notification;
import com.slouple.android.widget.adapter.DummyAdapter;
import com.slouple.android.widget.image.ImageSlider;

import java.util.Collections;
import java.util.Comparator;

import ca.ualberta.angrybidding.Bid;
import ca.ualberta.angrybidding.ElasticSearchTask;
import ca.ualberta.angrybidding.ElasticSearchUser;
import ca.ualberta.angrybidding.R;
import ca.ualberta.angrybidding.User;
import ca.ualberta.angrybidding.elasticsearch.UpdateResponseListener;
import ca.ualberta.angrybidding.map.LocationMarker;
import ca.ualberta.angrybidding.map.LocationPoint;
import ca.ualberta.angrybidding.map.MapObjectContainer;
import ca.ualberta.angrybidding.map.ScalableMapView;
import ca.ualberta.angrybidding.ui.view.BidView;
import me.relex.circleindicator.CircleIndicator;

//noti

public class ViewTaskDetailActivity extends AngryBiddingActivity {
    private ElasticSearchTask task;
    private User user;
    private String id;
    private Notification notification;

    private TextView titleTextView;
    private TextView ownerTextView;
    private TextView descriptionTextView;

    private FrameLayout mapCotainer;
    private MapObjectContainer mapObjectContainer;
    private ScalableMapView mapView;

    private ImageSlider imageSlider;
    private CircleIndicator circleIndicator;

    private TextView bidsLable;
    private RecyclerView bidRecyclerView;

    /**
     * Creates ViewTaskDetailActivity
     * Gets task object from Intent using Gson
     * Assigns members to according view objects
     *
     * @param savedInstanceState State of saved instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_task_detail);

        Intent intent = getIntent();
        String taskJson = intent.getStringExtra("task");
        id = intent.getStringExtra("id");
        task = new Gson().fromJson(taskJson, ElasticSearchTask.class);
        user = ElasticSearchUser.getMainUser(this);

        titleTextView = findViewById(R.id.taskDetailTitle);
        ownerTextView = findViewById(R.id.taskDetailOwner);
        descriptionTextView = findViewById(R.id.taskDetailDescription);

        imageSlider = findViewById(R.id.taskDetailImageSlider);
        circleIndicator = findViewById(R.id.taskDetailCircleIndicator);
        circleIndicator.setViewPager(imageSlider);
        imageSlider.getAdapter().registerDataSetObserver(circleIndicator.getDataSetObserver());

        mapCotainer = findViewById(R.id.taskDetailMapContainer);
        mapObjectContainer = findViewById(R.id.taskDetailMapObjectContainer);
        mapView = findViewById(R.id.taskDetailMapView);

        bidsLable = findViewById(R.id.taskDetailBidsLabel);
        bidRecyclerView = findViewById(R.id.taskDetailBids);

        titleTextView.setText(task.getTitle());
        ownerTextView.setText(task.getUser().getUsername());
        descriptionTextView.setText(task.getDescription());

        if (task.getPhotos().size() == 0) {
            imageSlider.setVisibility(View.GONE);
            circleIndicator.setVisibility(View.GONE);
        } else {
            for (String string : task.getPhotos()) {
                imageSlider.addSlide(string);
            }
        }

        if (task.getLocationPoint() == null) {
            mapCotainer.setVisibility(View.GONE);
        } else {
            LocationPoint locationPoint = new LocationPoint(task.getLocationPoint().getLatitude(), task.getLocationPoint().getLongitude());
            locationPoint.setZ(13);
            mapView.setLocation(locationPoint);
            mapObjectContainer.addView(new LocationMarker(this, locationPoint, false));
        }

        if (task.getBids().size() < 1) {
            bidsLable.setVisibility(View.GONE);
            bidRecyclerView.setVisibility(View.GONE);
        } else {
            Collections.sort(task.getBids(), new Comparator<Bid>() {
                @Override
                public int compare(Bid bid1, Bid bid2) {
                    return bid1.getPrice().compareTo(bid2.getPrice());
                }
            });
            bidRecyclerView.setAdapter(new DummyAdapter<Bid, BidView>(task.getBids()) {
                @Override
                public BidView createView(int i) {
                    return new BidView(ViewTaskDetailActivity.this);
                }

                /**
                 * Sets the list view of Bids
                 * Checks if Task detail view user is owner or not and displays popup menu accordingly
                 *
                 * @param bidView view of bid list
                 * @param bid     The bid in bid list
                 */
                @Override
                public void onBindView(final BidView bidView, final Bid bid) {
                    bidView.setBid(bid);
                    if (task.getUser().equals(user)) {
                        bidView.useBidPopupMenu(bid, new BidView.OnBidActionListener() {
                            @Override
                            public void onDecline() {
                                ViewTaskDetailActivity.this.onDecline(bid);
                            }

                            @Override
                            public void onAccept() {
                                ViewTaskDetailActivity.this.onAccept(bid);
                            }
                        });
                    }
                }

                @Override
                public void onReachingLastItem(int i) {

                }
            });
            bidRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    /**
     * Removes or Declines selected bid
     *
     * @param bid The selected bid
     */
    public void onDecline(Bid bid) {
        task.getBids().remove(bid);
        task.updateStatus();
        updateFinish();

    }

    /**
     * Removes bids from bid list except Accepted(chosen) bid
     *
     * @param bid The selected bid
     */
    public void onAccept(Bid bid) {
        task.setChosenBid(bid);
        updateFinish();
    }

    /**
     * Updates the change
     */
    public void updateFinish() {
        ElasticSearchTask.updateTask(this, id, task, new UpdateResponseListener() {
            @Override
            public void onCreated(String id) {

            }

            @Override
            public void onUpdated(int version) {
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

}
