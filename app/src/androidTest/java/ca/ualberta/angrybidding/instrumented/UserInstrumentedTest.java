package ca.ualberta.angrybidding.instrumented;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import ca.ualberta.angrybidding.User;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class UserInstrumentedTest {
    public static final String USERNAME = "Carl";
    public static final String EMAIL_ADDRESS = "Carl@ualberta.ca";

    @Test
    public void userConstructor() {
        User newUser = new User(USERNAME);
        assertThat(newUser.getUsername(), is(USERNAME.toLowerCase()));
        assertEquals(newUser.getEmailAddress(), null);
    }

    @Test
    public void userConstructorEmail() {
        User newUser = new User(USERNAME, EMAIL_ADDRESS);
        assertThat(newUser.getUsername(), is(USERNAME.toLowerCase()));
        assertThat(newUser.getEmailAddress(), is(EMAIL_ADDRESS.toLowerCase()));
    }
}
