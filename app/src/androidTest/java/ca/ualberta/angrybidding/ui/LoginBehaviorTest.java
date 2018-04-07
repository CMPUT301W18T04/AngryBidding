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

import ca.ualberta.angrybidding.ElasticSearchUser;
import ca.ualberta.angrybidding.R;
import ca.ualberta.angrybidding.elasticsearch.DeleteRequest;
import ca.ualberta.angrybidding.elasticsearch.DeleteResponseListener;
import ca.ualberta.angrybidding.ui.activity.LoginActivity;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginBehaviorTest {

    private String username = "ui_tester";
    private String incorrectPassword = "ui_tester_incorrect_password";
    private String password = "ui_tester_password";
    private String emailAddress = "ui_tester@a.com";

    @Rule
    public ActivityTestRule<LoginActivity> activityTestRule = new ActivityTestRule<>(LoginActivity.class, true, false);

    @Test
    public void badLogin() {
        onView(withId(R.id.loginUsernameTextView))
                .perform(typeText(username), closeSoftKeyboard());
        onView(withId(R.id.loginPasswordTextView))
                .perform(typeText(incorrectPassword), closeSoftKeyboard());
        onView(withId(R.id.loginButton)).perform(click());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(ElasticSearchUser.getMainUser(activityTestRule.getActivity()) == null);
    }

    @Test
    public void validLogin() {
        onView(withId(R.id.loginUsernameTextView))
                .perform(typeText(username), closeSoftKeyboard());
        onView(withId(R.id.loginPasswordTextView))
                .perform(typeText(password), closeSoftKeyboard());
        onView(withId(R.id.loginButton)).perform(click());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(ElasticSearchUser.getMainUser(activityTestRule.getActivity()) != null);
    }

    @Before
    public void setUp() {
        final Context context = getInstrumentation().getTargetContext();
        ElasticSearchUser.signUp(context, username, password, emailAddress, new ElasticSearchUser.UserSignUpListener() {
            @Override
            public void onSuccess(ElasticSearchUser user) {

            }

            @Override
            public void onDuplicate() {

            }

            @Override
            public void onError(VolleyError error) {

            }
        });
        ElasticSearchUser.removeMainUser(context);
        activityTestRule.launchActivity(new Intent());
    }


    @After
    public void cleanUp() {
        final Context context = activityTestRule.getActivity();
        ElasticSearchUser.removeMainUser(context);
        ElasticSearchUser.getUserByUsername(context, username, new ElasticSearchUser.GetUserListener() {
            @Override
            public void onFound(ElasticSearchUser user) {
                DeleteRequest deleteRequest = new DeleteRequest("user", user.getID(), new DeleteResponseListener() {
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
                deleteRequest.submit(context);
            }

            @Override
            public void onNotFound() {

            }

            @Override
            public void onError(VolleyError error) {

            }
        });
    }
}