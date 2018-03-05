package ca.ualberta.angrybidding.ui.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.slouple.android.AdvancedActivity;
import com.slouple.android.VolleySingleton;
import com.slouple.android.widget.button.SubmitButton;
import com.slouple.android.widget.button.SubmitButtonListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ca.ualberta.angrybidding.Bid;
import ca.ualberta.angrybidding.ElasticSearchTask;
import ca.ualberta.angrybidding.ElasticSearchUser;
import ca.ualberta.angrybidding.LocationPoint;
import ca.ualberta.angrybidding.R;
import ca.ualberta.angrybidding.Task;
import ca.ualberta.angrybidding.User;
import ca.ualberta.angrybidding.elasticsearch.AddResponseListener;

public class MainActivity extends AdvancedActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        final SubmitButton testButton = findViewById(R.id.testSubmitButton);
        testButton.setButtonListener(new SubmitButtonListener() {
            @Override
            public void onSubmit() {

            }

            @Override
            public void onDisabledClick() {

                ElasticSearchUser.getUserByUsername(MainActivity.this, "Cosh_", new ElasticSearchUser.GetUserListener() {
                    @Override
                    public void onFound(ElasticSearchUser user) {
                        Log.d("MainActivity", "getUserByUsername: " + user.getID() + " " + user.getUsername() + " " + user.getPasswordHash() + " " + user.getEmailAddress());
                    }

                    @Override
                    public void onNotFound() {
                        Log.d("MainActivity", "No User Found");
                    }

                    @Override
                    public void onError(VolleyError error) {
                        Log.e("MainActivity", error.getMessage(), error);
                    }
                });

                ElasticSearchUser.login(MainActivity.this, "Cosh_", "testpassword", new ElasticSearchUser.UserLoginListener() {
                    @Override
                    public void onSuccess(ElasticSearchUser user) {
                        Log.d("MainActivity", "login: " + user.getID() + " " + user.getUsername() + " " + user.getPasswordHash() + " " + user.getEmailAddress());
                    }

                    @Override
                    public void onFailure() {
                        Log.d("MainActivity", "Login Failed");
                    }

                    @Override
                    public void onError(VolleyError error) {
                        Log.e("MainActivity", error.getMessage(), error);
                    }
                });

                ElasticSearchUser.signUp(MainActivity.this, "Cosh_", "testpassword", "cosh@slouple.com", new ElasticSearchUser.UserSignUpListener() {
                    @Override
                    public void onSuccess(ElasticSearchUser user) {
                        Log.d("MainActivity", "signUp: " + user.getID() + " " + user.getUsername() + " " + user.getPasswordHash() + " " + user.getEmailAddress());
                    }

                    @Override
                    public void onDuplicate() {
                        Log.d("MainActivity", "Sign Up Duplicate");
                    }

                    @Override
                    public void onError(VolleyError error) {
                        Log.e("MainActivity", error.getMessage(), error);
                    }
                });

                ArrayList<Bid> bids = new ArrayList<>();
                bids.add(new Bid(new User("Cosh_"), 100.5d));
                bids.add(new Bid(new User("QWER"), 2334d));
                Task task = new Task(new User("Cosh_"), "Test Task", "test description",
                        new LocationPoint(25d, 50d),  bids);
                Log.d("MainActivity", new Gson().toJson(task));

//                ElasticSearchTask.addTask(MainActivity.this, task, new AddResponseListener() {
//                    @Override
//                    public void onCreated(String id) {
//                        Log.d("MainActivity", "addTask: " + id);
//                    }
//
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.e("MainActivity", error.getMessage(), error);
//                    }
//                });

                ElasticSearchTask.listTaskByUser(MainActivity.this, "Cosh_", new ElasticSearchTask.ListTaskListener() {
                    @Override
                    public void onResult(ArrayList<ElasticSearchTask> tasks) {
                        Log.d("MainActivity", "listTask: onResult");
                        for(ElasticSearchTask task : tasks){
                            Log.d("MainActivity", "listTask: " + task.getID());
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {
                        Log.e("MainActivity", error.getMessage(), error);
                    }
                });
            }

            @Override
            public void onSubmitClick() {

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
