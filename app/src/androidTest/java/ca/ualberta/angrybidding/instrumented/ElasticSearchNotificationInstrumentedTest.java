package ca.ualberta.angrybidding.instrumented;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;

import ca.ualberta.angrybidding.ElasticSearchNotification;
import ca.ualberta.angrybidding.User;
import ca.ualberta.angrybidding.notification.Notification;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ElasticSearchNotificationInstrumentedTest {
    public static final String USERNAME = "Cosh_";
    public static final String NOTIFICATION_TYPE = "Message";
    public static final String PAR_NAME = "Par1";
    public static final String PAR_VALUE = "1234";
    public static final String ID = "qwer1wqwrqrwqrq";


    @Test
    public void notificationConstructor() {
        User user = new User(USERNAME);
        HashMap<String, String> pars = new HashMap<>();
        pars.put(PAR_NAME, PAR_VALUE);
        ElasticSearchNotification notification = new ElasticSearchNotification(ID, new User(USERNAME), NOTIFICATION_TYPE, pars, false);
        assertThat(notification.getUser(), is(user));
        assertThat(notification.getParameterMap().get(PAR_NAME), is(PAR_VALUE));
        assertThat(notification.getNotificationType(), is(NOTIFICATION_TYPE));
        assertThat(notification.getID(), is(ID));
    }
}
