package ca.ualberta.angrybidding;

import java.util.ArrayList;

public class Task {
    private User user;
    private String title;
    private String description;
    private LocationPoint locationPoint;
    private Bid chosenBid;
    private ArrayList<Bid> bids;
    private boolean completed = false;

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

    public Task(User user, String title, String description, LocationPoint locationPoint, Bid chosenBid) {
        this(user, title, description, locationPoint);
        this.chosenBid = chosenBid;
    }

    public User getUser() {
        return this.user;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public void setChosenBid(Bid chosenBid) {
        this.chosenBid = chosenBid;
    }

    public Bid getChosenBid() {
        return this.chosenBid;
    }

    public ArrayList<Bid> getBids() {
        return this.bids;
    }

    public void setLocationPoint(LocationPoint locationPoint) {
        this.locationPoint = locationPoint;
    }

    public LocationPoint getLocationPoint() {
        return this.locationPoint;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean isCompleted() {
        return this.completed;
    }
}