package ca.ualberta.angrybidding;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class BidInstrumentedTest {
    private User newUser = new User("Carl", "Carl@ualberta.ca");
    public static final BigDecimal newPrice = new BigDecimal(10.0);
    public static final double newDoublePrice = 10.0;
    private Bid newBid;

    @Test
    public void bidConstructorBigDeciaml(){
        newBid = new Bid(newUser, newPrice);
        assertThat(newBid.getUser(), is(newUser));
        assertThat(newBid.getPrice(), is(newPrice));
    }

    @Test
    public void bidConstructorDouble(){
        newBid = new Bid(newUser, newDoublePrice);
        assertThat(newBid.getUser(), is(newUser));
        assertThat(newBid.getPrice(), is(newPrice));
    }
}
