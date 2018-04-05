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
public class SignUpBehaviorTest {

    private String username = "ui_tester";
    private String password = "ui_tester_password";
    private String badEmailAddress = "ui_tester.com";
    private String emailAddress = "ui_tester@a.com";

    @Rule
    public ActivityTestRule<LoginActivity> activityTestRule = new ActivityTestRule<>(LoginActivity.class, true, false);

    @Test
    public void badSignUp() {
        onView(withId(R.id.createAccountTextView)).perform(click());
        onView(withId(R.id.signUpUsername))
                .perform(typeText(username), closeSoftKeyboard());
        onView(withId(R.id.signUpPassword))
                .perform(typeText(password), closeSoftKeyboard());
        onView(withId(R.id.signUpEmailAddress))
                .perform(typeText(badEmailAddress), closeSoftKeyboard());
        onView(withId(R.id.signUpSubmitButton)).perform(click());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(ElasticSearchUser.getMainUser(activityTestRule.getActivity()) == null);
    }

    @Test
    public void validSignUp() {
        onView(withId(R.id.createAccountTextView)).perform(click());
        onView(withId(R.id.signUpUsername))
                .perform(typeText(username), closeSoftKeyboard());
        onView(withId(R.id.signUpPassword))
                .perform(typeText(password), closeSoftKeyboard());
        onView(withId(R.id.signUpEmailAddress))
                .perform(typeText(emailAddress), closeSoftKeyboard());
        onView(withId(R.id.signUpSubmitButton)).perform(click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(ElasticSearchUser.getMainUser(activityTestRule.getActivity()) != null);
    }

    @Before
    public void setUp() {
        final Context context = getInstrumentation().getTargetContext();
        ElasticSearchUser.removeMainUser(context);
        activityTestRule.launchActivity(new Intent());
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

    @After
    public void cleanUp() {
        final Context context = getInstrumentation().getTargetContext();
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