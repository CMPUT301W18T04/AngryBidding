package ca.ualberta.angrybidding.ui.activity.main;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.slouple.android.AdvancedFragment;

import java.util.ArrayList;

import ca.ualberta.angrybidding.ElasticSearchNotification;
import ca.ualberta.angrybidding.R;
import ca.ualberta.angrybidding.notification.BidAddedNotification;
import ca.ualberta.angrybidding.notification.Notification;
import ca.ualberta.angrybidding.notification.NotificationCallback;
import ca.ualberta.angrybidding.notification.NotificationConnection;
import ca.ualberta.angrybidding.notification.NotificationFactory;
import ca.ualberta.angrybidding.notification.NotificationService;
import ca.ualberta.angrybidding.notification.NotificationWrapper;
import ca.ualberta.angrybidding.ui.activity.main.IMainFragment;

/**
 * Created by SarahS on 2018/03/29.
 */

public class NotificationFragment extends AdvancedFragment implements IMainFragment {
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private ArrayList<Notification> notifications = new ArrayList<>();
    private ElasticSearchNotification.ListNotificationListener listener;

    private static final int COMMENT_VIEW_TYPE = 1;
    private static final int RATE_VIEW_TYPE = 2;

    private AppBarLayout appBarLayout;
    private Toolbar toolbar;

    @Override
    public AppBarLayout getAppBarLayout(ViewGroup rootView, LayoutInflater inflater) {
        if (appBarLayout == null) {
            appBarLayout = (AppBarLayout) inflater.inflate(R.layout.notification_fragment_layout, rootView, false);
        }
        return appBarLayout;

    }

    @Override
    public Toolbar getToolbar(ViewGroup rootView, LayoutInflater inflater) {
        if (toolbar == null) {
            toolbar = (Toolbar) getAppBarLayout(rootView, inflater).findViewById(R.id.notification_fragment_toolbar);
        }
        return toolbar;
    }

    @Override
    public void onActionBarAdded(ActionBar actionBar) {

    }

    @Override
    public boolean shouldOffsetForToolbar() {
        return true;
    }

    @Override
    public void onVisible() {

    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    //Need to add
    private NotificationConnection connection = new NotificationConnection() {
        @Override
        public NotificationWrapper onDeserializeNotification(String className, String json) {
            Class<?> classType;
            if(className.equals("com.postphere.notification.CommentNotification")){
                classType = BidAddedNotification.class;
            }else{
                return null;
            }
            Gson gson = new Gson();
            return (NotificationWrapper) gson.fromJson(json, classType);
        }

        @Override
        public void onReceivedNotification(NotificationWrapper notificationWrapper) {
            if(notificationWrapper != null){
                //ElasticsearchNotification.addNotification?
                //addNotification(notification);
                final MainActivity mainActivity = (MainActivity) getContext();
                if(mainActivity.getCurrentFragmentID() != R.id.nav_notification) {
                    Snackbar.make(getView(), "New Notification", Snackbar.LENGTH_LONG)
                            .setAction("View", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    mainActivity.setCurrentFragment(R.id.nav_notification);
                                }
                            }).show();
                }
            }
        }
    };//

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //Notification view? New Class needed?
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FrameLayout layout = (FrameLayout) inflater.inflate(R.layout.fragment_notification, container, false);
        //ViewPager viewPager = layout.findViewById(R.id.notificationViewPager);

        return layout;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

}
