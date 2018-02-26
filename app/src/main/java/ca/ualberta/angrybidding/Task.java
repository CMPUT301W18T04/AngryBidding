package ca.ualberta.angrybidding;

import java.util.ArrayList;

public class Task {
    private User user;
    private String title;
    private String description;
    private LocationPoint locationPoint;
    private ArrayList<Bid> bids;

    public Task(User user, String title) {
        this.user = user;
        this.title = title;
    }

    public Task(User user, String title, String description) {
        this(user, title);
        this.description = description;
    }

    public Task(User user, String title, String description, LocationPoint locationPoint) {
        this(user, title, description);
        this.locationPoint = locationPoint;
    }

    public Task(User user, String title, String description, LocationPoint locationPoint, ArrayList<Bid> bids) {
        this(user, title, description, locationPoint);
        this.bids = bids;
    }

    public User getUser() {
        return this.user;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public ArrayList<Bid> getBids() {
        return this.bids;
    }

    public LocationPoint getLocationPoint() {
        return this.locationPoint;
    }
}