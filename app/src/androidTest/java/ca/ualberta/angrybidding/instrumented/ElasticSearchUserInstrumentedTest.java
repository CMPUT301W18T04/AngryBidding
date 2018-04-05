package ca.ualberta.angrybidding.instrumented;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import ca.ualberta.angrybidding.ElasticSearchUser;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ElasticSearchUserInstrumentedTest {
    public static final String USERNAME = "Carl";
    public static final String EMAIL_ADDRESS = "Carl@ualberta.ca";
    public static final String PASSWORD_HASH = "123456asdfgh";
    public static final String ID = "abcde";


    @Test
    public void elasticSearchUserConstructor() {
        ElasticSearchUser newUser = new ElasticSearchUser(ID, USERNAME, PASSWORD_HASH, EMAIL_ADDRESS);
        assertThat(newUser.getID(), is(ID));
        assertThat(newUser.getUsername(), is(USERNAME.toLowerCase()));
        assertThat(newUser.getPasswordHash(), is(PASSWORD_HASH));
        assertThat(newUser.getEmailAddress(), is(EMAIL_ADDRESS.toLowerCase()));

    }
}
