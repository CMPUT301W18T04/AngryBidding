package ca.ualberta.angrybidding.instrumented;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import ca.ualberta.angrybidding.Bid;
import ca.ualberta.angrybidding.ElasticSearchTask;
import ca.ualberta.angrybidding.LocationPoint;
import ca.ualberta.angrybidding.User;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class ElasticSearchTaskInstrumentedTest {
    public static final String ID = "abcde";
    public static final User USERNAME = new User("Lewis");
    public static final String TITLE = "This is the title for the task";
    public static final String DESCRIPTION = "Description of the task";
    public static final LocationPoint LOCATION_POINT = new LocationPoint(0, 0);

    @Test
    public void newTaskID() {
        ArrayList<Bid> bids = new ArrayList<Bid>();
        bids.add(new Bid(new User("John"), 80.32));
        ElasticSearchTask newTask = new ElasticSearchTask(ID, USERNAME, TITLE, DESCRIPTION, LOCATION_POINT, bids);
        assertThat(newTask.getUser(), is(USERNAME));
        assertThat(newTask.getTitle(), is(TITLE));
        assertThat(newTask.getDescription(), is(DESCRIPTION));
        assertThat(newTask.getLocationPoint(), is(LOCATION_POINT));
        assertThat(newTask.getBids(), is(bids));
        assertThat(newTask.getID(), is(ID));
    }
}
