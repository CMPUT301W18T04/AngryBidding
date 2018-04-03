package ca.ualberta.angrybidding.instrumented;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class LocationPointInstrumentedTest {
    @Test
    public void latitudeLongitudeConstructor() throws Exception {
        double latitude = 80.01d;
        double longitude = 25.25d;
        LocationPoint locationPoint = new LocationPoint(latitude, longitude);
        assertThat(locationPoint.getLatitude(), is(latitude));
        assertThat(locationPoint.getLongitude(), is(longitude));
    }

    @Test
    public void latitudeLongitudeSetters() throws Exception {
        double latitude = 80.01d;
        double longitude = 25.25d;
        double newLatitude = 30.30d;
        double newLongitude = 50.50d;
        LocationPoint locationPoint = new LocationPoint(latitude, longitude);
        locationPoint.setLatitude(newLatitude);
        locationPoint.setLongitude(newLongitude);
        assertThat(locationPoint.getLatitude(), is(newLatitude));
        assertThat(locationPoint.getLongitude(), is(newLongitude));
    }

    @Test
    public void xyzConstructor() throws Exception {
        double x = 50.2d;
        double y = 20.3d;
        int z = 12;
        LocationPoint locationPoint = new LocationPoint(x, y, z);
        assertThat(locationPoint.getX(), is(x));
        assertThat(locationPoint.getY(), is(y));
        assertThat(locationPoint.getZ(), is(z));
    }

    @Test
    public void xyzSetters() throws Exception {
        double x = 50.2d;
        double y = 20.3d;
        int z = 12;
        double newX = 50.2d;
        double newY = 20.3d;
        LocationPoint locationPoint = new LocationPoint(x, y, z);
        locationPoint.setX(newX);
        locationPoint.setY(newY);
        assertThat(locationPoint.getX(), is(newX));
        assertThat(locationPoint.getY(), is(newY));

        double newAdjustedX = 25.1d;
        double newAdjustedY = 10.15d;
        int newZ = 11;
        locationPoint.setZ(newZ);
        assertThat(locationPoint.getZ(), is(newZ));
        assertThat(locationPoint.getX(), is(newAdjustedX));
        assertThat(locationPoint.getY(), is(newAdjustedY));
    }

    @Test
    public void copyLocationPoint() throws Exception {
        double x = 50.2d;
        double y = 20.3d;
        int z = 12;
        LocationPoint locationPoint = new LocationPoint(x, y, z);
        LocationPoint newLocationPoint = locationPoint.copy();

        assertThat(newLocationPoint.getX(), is(x));
        assertThat(newLocationPoint.getY(), is(y));
        assertThat(newLocationPoint.getZ(), is(z));
    }
}
