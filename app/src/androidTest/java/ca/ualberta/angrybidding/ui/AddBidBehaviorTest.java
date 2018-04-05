package ca.ualberta.angrybidding.ui;

import android.content.Context;
import android.content.Intent;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.android.volley.VolleyError;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.util.ArrayList;

import ca.ualberta.angrybidding.ElasticSearchTask;
import ca.ualberta.angrybidding.ElasticSearchUser;
import ca.ualberta.angrybidding.R;
import ca.ualberta.angrybidding.Task;
import ca.ualberta.angrybidding.User;
import ca.ualberta.angrybidding.elasticsearch.AddResponseListener;
import ca.ualberta.angrybidding.elasticsearch.DeleteResponseListener;
import ca.ualberta.angrybidding.ui.activity.main.MainActivity;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AddBidBehaviorTest {
    private String username = "ui_tester";
    private String password = "ui_tester_password";
    private String emailAddress = "ui_tester@a.com";

    private String title = "Test Task";
    private String description = "Test Task Description";

    private String bidderUsername = "test_bidder";
    private BigDecimal price = new BigDecimal(20.56);

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class, true, false);

    @Test
    public void validBidTask() throws Exception {
        onView(withId(R.id.addTaskActionButton)).perform(click());
        onView(withId(R.id.addTaskTitle))
                .perform(typeText(title), closeSoftKeyboard());
        onView(withId(R.id.addTaskDescription))
                .perform(typeText(description), closeSoftKeyboard());
        onView(withId(R.id.addTaskSubmitButton)).perform(click());

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(allOf(withId(R.id.taskTitle), withText(title)))
                .check(matches(isDisplayed()));
        onView(allOf(withId(R.id.taskDescription), withText(description)))
                .check(matches(isDisplayed()));
    }

    @Before
    public void setUp() {
        final Context context = getInstrumentation().getTargetContext();
        ElasticSearchUser.setMainUser(context, new ElasticSearchUser("abcde", bidderUsername, "", ""));
        ElasticSearchTask.listTaskByUser(context, username, new ElasticSearchTask.ListTaskListener() {
            @Override
            public void onResult(ArrayList<ElasticSearchTask> tasks) {
                for (ElasticSearchTask task : tasks) {
                    ElasticSearchTask.deleteTask(context, task.getID(), new DeleteResponseListener() {
                        @Override
                        public void onDeleted(String id) {

                        }

                        @Override
                        public void onNotFound() {

                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });
                }
                ElasticSearchTask.addTask(context, new Task(new User(username), title, description), new AddResponseListener() {
                    @Override
                    public void onCreated(String id) {

                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
            }

            @Override
            public void onError(VolleyError error) {

            }
        });
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        activityTestRule.launchActivity(new Intent());
    }

    @After
    public void cleanUp() {
        final Context context = activityTestRule.getActivity();
        ElasticSearchTask.listTaskByUser(context, username, new ElasticSearchTask.ListTaskListener() {
            @Override
            public void onResult(ArrayList<ElasticSearchTask> tasks) {
                for (ElasticSearchTask task : tasks) {
                    ElasticSearchTask.deleteTask(context, task.getID(), new DeleteResponseListener() {
                        @Override
                        public void onDeleted(String id) {

                        }

                        @Override
                        public void onNotFound() {

                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });
                }
            }

            @Override
            public void onError(VolleyError error) {

            }
        });
        ElasticSearchUser.removeMainUser(context);
    }
}