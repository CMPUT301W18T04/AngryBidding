package ca.ualberta.angrybidding;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class UserInstrumentedTest {
    public static final String username = "Carl";
    public static final String emailAddress = "Carl@ualberta.ca";
    public static final String password = "123456asdfgh";

    @Test
    public void userConstructor(){
        User newUser = new User(username);
        assertThat(newUser.getUsername(), is(username));
        assertEquals(newUser.getEmailAddress(), null);
    }

    @Test
    public void userConstructorEmail(){
        User newUser = new User(username, emailAddress);
        assertThat(newUser.getUsername(), is(username));
        assertThat(newUser.getEmailAddress(), is(emailAddress));
    }

    @Test
    public void userLogin(){
        assertEquals(User.login(username,emailAddress), null);
    }

    @Test
    public void userSignUp(){
        assertEquals(User.signUp(username, password, emailAddress), null);
    }
}
