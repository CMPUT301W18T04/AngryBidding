package ca.ualberta.angrybidding.ui.activity.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import ca.ualberta.angrybidding.ElasticSearchTask;
import ca.ualberta.angrybidding.ElasticSearchUser;
import ca.ualberta.angrybidding.R;
import ca.ualberta.angrybidding.ui.activity.AngryBiddingActivity;
import ca.ualberta.angrybidding.ui.activity.LoginActivity;

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
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setItemTextColor(null);
        navigationView.setNavigationItemSelectedListener(this);

        appBarMain = (CoordinatorLayout) findViewById(R.id.app_bar_main);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        View.OnClickListener accountClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        };

        View header = navigationView.getHeaderView(0);
        avatarView = header.findViewById(R.id.userAvatarImageView);
        avatarView.setOnClickListener(accountClickListener);
        displayNameView = header.findViewById(R.id.userDisplayName);
        displayNameView.setOnClickListener(accountClickListener);


        if (getElasticSearchUser() == null) {
            startActivity(LoginActivity.class);
            finish();
        } else {
            displayNameView.setText(getElasticSearchUser().getUsername());
            addFragment(R.id.nav_history, new HistoryFragment(), true);
            addFragment(R.id.nav_search, new SearchFragment(), true);
            addFragment(R.id.nav_nearby, new NearbyFragment(), true);
            addFragment(R.id.nav_all_task, new AllTaskFragment(), true);
            addFragment(R.id.nav_settings, new SettingsFragment(), true);
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

    @Override
    protected int getColorID() {
        return R.color.colorPrimaryDark;
    }

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

    //Use setCurrentFragment instead
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

    public Fragment getFragment(int drawerID) {
        return fragmentList.get(drawerID).getFragment();
    }

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

    public void setCurrentFragment(int id) {
        navigationView.setCheckedItem(id);
        onNavigationItemSelected(id);
    }

    public Fragment getCurrentFragment() {
        return getFragment(getCurrentFragmentID());
    }

    public int getCurrentFragmentID() {
        return currentFragmentID;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (!(getCurrentFragment() instanceof IMainFragment) ||
                !((IMainFragment) getCurrentFragment()).onBackPressed()) {

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
