package ca.ualberta.angrybidding.ui.activity.main;

import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.LinkedHashMap;

import ca.ualberta.angrybidding.BuildConfig;
import ca.ualberta.angrybidding.ElasticSearchUser;
import ca.ualberta.angrybidding.R;
import ca.ualberta.angrybidding.ui.activity.AngryBiddingActivity;
import ca.ualberta.angrybidding.ui.activity.EditUserProfileActivity;
import ca.ualberta.angrybidding.ui.activity.LoginActivity;

/**
 * Activity that incorporates all basic app fragments
 * Fragments can be added and removed
 * Extends AdvancedActivity
 */
public class MainActivity extends AngryBiddingActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FragmentManager fm;
    private NavigationView navigationView;
    private CoordinatorLayout appBarMain;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private AppBarLayout currentAppBarLayout;

    private ImageView avatarView;
    private TextView displayNameView;


    private LinkedHashMap<Integer, MainFragmentEntry> fragmentList = new LinkedHashMap<>();
    private int currentFragmentID = -1;
    private int savedFragmentID = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        fm = getSupportFragmentManager();
        //Set navigationView
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setItemTextColor(null);
        navigationView.setNavigationItemSelectedListener(this);

        //AppBar
        appBarMain = (CoordinatorLayout) findViewById(R.id.app_bar_main);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        //Account image and text click
        View.OnClickListener accountClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getElasticSearchUser() == null) {
                    startActivity(LoginActivity.class);
                } else {
                    Intent intent = new Intent(MainActivity.this, EditUserProfileActivity.class);
                    intent.putExtra("username", getElasticSearchUser().getUsername());
                    startActivityForResult(intent, EditUserProfileActivity.REQUEST_CODE);
                }
            }
        };
        View.OnLongClickListener accountLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (getElasticSearchUser() == null) {
                    startActivity(LoginActivity.class);
                } else {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle(R.string.signOut)
                            .setMessage(R.string.confirmSignOut)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ElasticSearchUser.removeMainUser(MainActivity.this);
                                    startActivity(LoginActivity.class);
                                    finish();
                                }

                            })
                            .setNegativeButton(R.string.no, null)
                            .show();
                }
                return true;
            }
        };

        //Header, including user image and user displayName
        View header = navigationView.getHeaderView(0);
        avatarView = header.findViewById(R.id.userAvatarImageView);
        avatarView.setOnClickListener(accountClickListener);
        avatarView.setOnLongClickListener(accountLongClickListener);
        displayNameView = header.findViewById(R.id.userDisplayName);
        displayNameView.setOnClickListener(accountClickListener);
        displayNameView.setOnLongClickListener(accountLongClickListener);


        //If user is not logged in, open LoginActivity
        if (getElasticSearchUser() == null) {
            startActivity(LoginActivity.class);
            finish();
        } else {
            displayNameView.setText(getElasticSearchUser().getUsername());
            //Added all fragments
            addFragment(R.id.nav_history, new HistoryFragment(), true);
            addFragment(R.id.nav_search, new SearchFragment(), true);
            addFragment(R.id.nav_nearby, new NearbyFragment(), true);
            addFragment(R.id.nav_notification, new NotificationFragment(), true);
            addFragment(R.id.nav_credits, new CreditsFragment(), true);
            //addFragment(R.id.nav_settings, new SettingsFragment(), true);

            if(BuildConfig.DEBUG) {
                addFragment(R.id.nav_all_task, new AllTaskFragment(), true);
            }

            setCurrentFragment(R.id.nav_history);
        }
        Log.d("MainActivity", "Finished onCreate");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("MainActivity", "Finished onStart");
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            Log.d("MainActivity", "Restoring Saved Instance State");
            //Load savedFragmentsID
            savedFragmentID = savedInstanceState.getInt("currentFragmentID");
        }
    }

    //Calls between onRestore onStart, overrides savedFragmentID
    @Override
    public void onPostCreate(Bundle bundle) {
        super.onPostCreate(bundle);
        handleIntent(getIntent());
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    //Handles intent which specifies which fragment to display on start
    public void handleIntent(Intent intent) {
        if (intent == null || intent.getExtras() == null) {
            return;
        }
        int fragmentID = intent.getIntExtra("fragmentID", -1);
        if (fragmentID > 0) {
            savedFragmentID = fragmentID;
        }
        setIntent(null);
    }

    @Override
    public void onResume() {
        super.onResume();

        //All fragments should be added before the code below
        if (savedFragmentID > 0) {
            setCurrentFragment(savedFragmentID);
            savedFragmentID = -1;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.d("MainActivity", "Saving Instance State");
        if (savedInstanceState == null) {
            savedInstanceState = new Bundle();
        }
        //Save currentFragmentID
        savedInstanceState.putInt("currentFragmentID", currentFragmentID);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        Log.d("MainActivity", "Finished onStart");
    }

    /**
     * Color of the status bar
     * Override from AdvancedActivity
     *
     * @return color of the status bar
     */
    @Override
    protected int getColorID() {
        return R.color.colorPrimaryDark;
    }

    /**
     * Add fragment to the activity
     *
     * @param drawerID The drawer ID links to the fragment. This is also used to distinguish between different fragments.
     * @param fragment The fragment to add
     * @param preserve Should the fragment be preserved when switched to other fragments
     */
    public void addFragment(int drawerID, Fragment fragment, boolean preserve) {
        Fragment savedFragment = fm.findFragmentByTag(String.valueOf(drawerID));
        if (savedFragment == null) {
            this.fragmentList.put(drawerID, new MainFragmentEntry(fragment, preserve));
            FragmentTransaction ft = fm.beginTransaction();
            if (preserve) {
                ft.add(R.id.content_main, fragment, String.valueOf(drawerID)).hide(fragment).commit();
            }
            updateDrawerMenu();
        } else {
            this.fragmentList.put(drawerID, new MainFragmentEntry(savedFragment, preserve));
        }
    }

    /**
     * Remove a fragment
     *
     * @param drawerID DrawerID of the fragment
     */
    public void removeFragment(int drawerID) {
        MainFragmentEntry fragmentEntry = fragmentList.get(drawerID);
        if (fragmentEntry != null) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.remove(fragmentEntry.getFragment()).commit();
            this.fragmentList.remove(drawerID);
            updateDrawerMenu();
            if (currentFragmentID == drawerID) {
                setCurrentFragment(fragmentList.keySet().iterator().next());
            }
        }
    }

    /**
     * Use setCurrentFragment instead
     * Does not handle drawer
     *
     * @param drawerID
     */
    private void switchFragment(int drawerID) {
        MainFragmentEntry fragmentEntry = fragmentList.get(drawerID);
        if (drawerID == currentFragmentID || fragmentEntry == null) {
            return;
        }

        FragmentTransaction ft = fm.beginTransaction();
        //Hide all other fragments
        for (MainFragmentEntry other : fragmentList.values()) {
            if (other.equals(fragmentEntry)) {
                continue;
            }
            if (other.shouldPreserve()) {
                ft.hide(other.getFragment());
            } else {
                ft.remove(other.getFragment());
            }
        }
        //Display fragment
        if (fragmentEntry.shouldPreserve()) {
            ft.show(fragmentEntry.getFragment());
        } else {
            ft.add(R.id.content_main, fragmentEntry.getFragment(), String.valueOf(drawerID));
        }
        ft.commit();
        fm.executePendingTransactions();
        //Set toolbar
        if (fragmentEntry.getFragment() instanceof IMainFragment) {
            setFragmentAppBarLayout((IMainFragment) fragmentEntry.getFragment());
            ((IMainFragment) fragmentEntry.getFragment()).onVisible();
        }
    }

    /**
     * Set AppBarLayout to the one from fragment
     *
     * @param fragment
     */
    private void setFragmentAppBarLayout(final IMainFragment fragment) {
        if (currentAppBarLayout != null) {
            appBarMain.removeView(currentAppBarLayout);
        }
        if (toggle != null) {
            drawer.removeDrawerListener(toggle);
        }
        currentAppBarLayout = fragment.getAppBarLayout(appBarMain, getLayoutInflater());
        appBarMain.addView(currentAppBarLayout);
        final Toolbar toolbar = fragment.getToolbar(appBarMain, getLayoutInflater());
        setSupportActionBar(toolbar);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        fragment.onActionBarAdded(getSupportActionBar());

        //Set main container margin depending on fragment shouldOffsetForToolbar
        final CoordinatorLayout.LayoutParams params = new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.MATCH_PARENT);
        if (fragment.shouldOffsetForToolbar()) {
            ViewTreeObserver observer = toolbar.getViewTreeObserver();
            observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    int height = toolbar.getHeight();
                    CoordinatorLayout.LayoutParams appBarParams = (CoordinatorLayout.LayoutParams) currentAppBarLayout.getLayoutParams();
                    height += appBarParams.topMargin + appBarParams.bottomMargin;
                    params.setMargins(0, height, 0, 0);
                    findViewById(R.id.content_main).setLayoutParams(params);
                    toolbar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
        } else {
            params.setMargins(0, 0, 0, 0);
            findViewById(R.id.content_main).setLayoutParams(params);
        }
    }

    /**
     * @param drawerID DrawerID of the fragment
     * @return The fragment linked with the drawerID
     */
    public Fragment getFragment(int drawerID) {
        return fragmentList.get(drawerID).getFragment();
    }

    /**
     * Updates drawer menu depending on which fragment is added
     */
    public void updateDrawerMenu() {
        for (int i = 0; i < navigationView.getMenu().size(); i++) {
            MenuItem item = navigationView.getMenu().getItem(i);
            if (fragmentList.get(item.getItemId()) == null) {
                item.setVisible(false);
            } else {
                item.setVisible(true);
            }
        }
    }

    /**
     * Switch current fragment by triggering drawer
     *
     * @param id drawerID of the fragment
     */
    public void setCurrentFragment(int id) {
        navigationView.setCheckedItem(id);
        onNavigationItemSelected(id);
    }

    /**
     * @return The current fragment being displayed
     */
    public Fragment getCurrentFragment() {
        return getFragment(getCurrentFragmentID());
    }

    /**
     * @return DrawerID of the current fragment being displayed
     */
    public int getCurrentFragmentID() {
        return currentFragmentID;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (!(getCurrentFragment() instanceof IMainFragment) ||
                !((IMainFragment) getCurrentFragment()).onBackPressed()) {
            //Returns to history fragment instead of closing
            if (currentFragmentID != R.id.nav_history) {
                setCurrentFragment(R.id.nav_history);
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return onNavigationItemSelected(item.getItemId());
    }

    private boolean onNavigationItemSelected(int drawerID) {
        switchFragment(drawerID);
        drawer.closeDrawer(GravityCompat.START);
        currentFragmentID = drawerID;
        return true;
    }

    /**
     * Data type for storing a main fragment and its values
     */
    private class MainFragmentEntry {
        private Fragment fragment;
        private boolean preserve;

        public MainFragmentEntry(Fragment fragment, boolean preserve) {
            this.fragment = fragment;
            this.preserve = preserve;
        }

        public Fragment getFragment() {
            return fragment;
        }

        public boolean shouldPreserve() {
            return preserve;
        }
    }
}
