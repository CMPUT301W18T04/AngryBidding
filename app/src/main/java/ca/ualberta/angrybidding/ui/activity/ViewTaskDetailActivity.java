package ca.ualberta.angrybidding.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.slouple.android.ResultRequest;
import com.slouple.android.widget.adapter.DummyAdapter;
import com.slouple.android.widget.image.ImageSlider;

import java.util.ArrayList;
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
import ca.ualberta.angrybidding.ui.view.TaskPopupMenuButton;
import me.relex.circleindicator.CircleIndicator;

public class ViewTaskDetailActivity extends AngryBiddingActivity {
    public static final int REQUEST_CODE = 1006;

    private ElasticSearchTask task;
    private User user;
    private String id;

    private TaskPopupMenuButton popupMenuButton;

    private TextView titleTextView;
    private TextView ownerTextView;
    private TextView descriptionTextView;

    private FrameLayout mapCotainer;
    private MapObjectContainer mapObjectContainer;
    private ScalableMapView mapView;

    private ImageSlider imageSlider;
    private CircleIndicator circleIndicator;

    private LinearLayout chosenBidContainer;
    private TextView chosenBidUsernameTextView;
    private TextView chosenBidPriceTextView;

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
        task.setID(id);
        user = ElasticSearchUser.getMainUser(this);

        popupMenuButton = findViewById(R.id.taskDetailPopupMenuButton);
        popupMenuButton.setTask(task, getElasticSearchUser(), new TaskPopupMenuButton.OnTaskChangeListener() {
            @Override
            public void onDelete() {
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onEdit() {
                setResult(RESULT_OK);
                finish();
            }
        });

        popupMenuButton.getPopupMenu().getMenu().removeItem(R.id.taskPopupViewDetail);

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

        ownerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewTaskDetailActivity.this, UserProfileActivity.class);
                intent.putExtra("username", task.getUser().getUsername());
                startActivity(intent);
            }
        });


        chosenBidContainer = findViewById(R.id.taskDetailChosenBidContainer);
        chosenBidUsernameTextView = findViewById(R.id.taskDetailChosenBidUsername);
        chosenBidPriceTextView = findViewById(R.id.taskDetailChosenBidPrice);

        if(task.getChosenBid() == null){
            chosenBidContainer.setVisibility(View.GONE);
        }else{
            chosenBidUsernameTextView.setText(task.getChosenBid().getUser().getUsername());
            chosenBidPriceTextView.setText(task.getChosenBid().getPriceString());
            chosenBidUsernameTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ViewTaskDetailActivity.this, UserProfileActivity.class);
                    intent.putExtra("username", task.getUser().getUsername());
                    startActivity(intent);
                }
            });
        }


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

        ArrayList<Bid> nonDeclinedBids = new ArrayList<>();
        for (Bid bid : task.getBids()) {
            if (!bid.isDeclined()) {
                nonDeclinedBids.add(bid);
            }
        }
        if (nonDeclinedBids.size() == 0) {
            bidsLable.setVisibility(View.GONE);
            bidRecyclerView.setVisibility(View.GONE);
        } else {
            Collections.sort(nonDeclinedBids, new Comparator<Bid>() {
                @Override
                public int compare(Bid bid1, Bid bid2) {
                    return bid1.getPrice().compareTo(bid2.getPrice());
                }
            });
            bidRecyclerView.setAdapter(new DummyAdapter<Bid, BidView>(nonDeclinedBids) {
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
                    bidView.setVisibility(View.VISIBLE);
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

        addResultRequest(new ResultRequest(AddTaskActivity.REQUEST_CODE) {
            @Override
            public void onResult(Intent intent) {
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onCancel(Intent intent) {
            }
        });

        addResultRequest(new ResultRequest(EditTaskActivity.REQUEST_CODE) {
            @Override
            public void onResult(Intent intent) {
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onCancel(Intent intent) {
            }
        });

        addResultRequest(new ResultRequest(AddBidActivity.REQUEST_CODE) {
            @Override
            public void onResult(Intent intent) {
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onCancel(Intent intent) {
            }
        });
    }

    /**
     * Removes or Declines selected bid
     *
     * @param bid The selected bid
     */
    public void onDecline(Bid bid) {
        bid.setDeclined(true);
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
