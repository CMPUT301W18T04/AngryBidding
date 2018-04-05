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
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.swipeDown;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class EditTaskBehaviorTest {
    private String username = "ui_tester";
    private String password = "ui_tester_password";
    private String emailAddress = "ui_tester@a.com";

    private String title = "Test Task";
    private String description = "Test Task Description";

    private String newTitle = "New Test Task";
    private String newDescription = "New Test Task Description";

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class, true, false);

    @Test
    public void validEditTask() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.taskPopupMenuButton)).perform(click());
        onView(withText("Edit")).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.editTaskTitle))
                .perform(clearText(), typeText(newTitle), closeSoftKeyboard());
        onView(withId(R.id.editTaskDescription))
                .perform(clearText(), typeText(newDescription), closeSoftKeyboard());
        onView(withId(R.id.editTaskSubmitButton)).perform(click());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(allOf(withId(R.id.taskListSwipeRefreshLayout), isDisplayed()))
                .perform(swipeDown());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(allOf(withId(R.id.taskTitle), withText(newTitle)))
                .check(matches(isDisplayed()));
        onView(allOf(withId(R.id.taskDescription), withText(newDescription)))
                .check(matches(isDisplayed()));
    }

    @Before
    public void setUp() {
        final Context context = getInstrumentation().getTargetContext();
        ElasticSearchUser user = new ElasticSearchUser("abc", username, "", "");
        ElasticSearchUser.setMainUser(context, user);
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
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ElasticSearchTask.addTask(context, new Task(new User(username), title, description), new AddResponseListener() {
            @Override
            public void onCreated(String id) {
            }

            @Override
            public void onErrorResponse(VolleyError error) {

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