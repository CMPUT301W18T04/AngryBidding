package ca.ualberta.angrybidding;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.web.proto.webdriver.WebWebdriverAtoms;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class TaskInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("ca.ualberta.angrybidding", appContext.getPackageName());

    }
    public static final User User_Name = new User("Lewis");
    public static final String Title = "This is the title for the task";
    public static final String Description = "Description of the task";
    public static final LocationPoint location_point = new LocationPoint(0,0);
    @Test
    public void newTask(){
        Task newTask = new Task(User_Name,Title);
        assertThat(newTask.getUser(),is(User_Name));
        assertThat(newTask.getTitle(),is(Title));
    }

    @Test
    public void newTaskDescription(){
        Task newTask = new Task(User_Name,Title,Description);
        assertThat(newTask.getUser(), is(User_Name));
        assertThat(newTask.getTitle(), is(Title));
        assertThat(newTask.getDescription(),is(Description));
    }

    @Test
    public void newTaskLocationPoint(){
        Task newTask = new Task(User_Name,Title,Description,location_point);
        assertThat(newTask.getUser(), is(User_Name));
        assertThat(newTask.getTitle(), is(Title));
        assertThat(newTask.getDescription(),is(Description));
        assertThat(newTask.getLocationPoint(), is(location_point));
    }
    @Test
    public void newTaskBids(){
        ArrayList<Bid> bids = new ArrayList<Bid>();
        bids.add(new Bid(new User("John"),80.32));
        Task newTask = new Task(User_Name,Title,Description,location_point,bids);
        assertThat(newTask.getUser(), is(User_Name));
        assertThat(newTask.getTitle(), is(Title));
        assertThat(newTask.getDescription(),is(Description));
        assertThat(newTask.getLocationPoint(), is(location_point));
        assertThat(newTask.getBids(), is(bids));
    }
}
