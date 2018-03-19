package ca.ualberta.angrybidding;

import java.util.ArrayList;

/**
 * Task model class
 * Contains all information for a task
 */
public class Task {
    private User user;
    private String title;
    private String description;
    private LocationPoint locationPoint;
    private Bid chosenBid;
    private ArrayList<Bid> bids;
    private boolean completed = false;

    /**
     * @param user User who created the task
     * @param title Title of the task
     */
    public Task(User user, String title) {
        this.user = user;
        this.title = title;
    }

    /**
     * @param user User who created the task
     * @param title Title of the task
     * @param description Description of the task
     */
    public Task(User user, String title, String description) {
        this(user, title);
        this.description = description;
    }

    /**
     * @param user User who created the task
     * @param title Title of the task
     * @param description Description of the task
     * @param locationPoint LocationPoint of the task
     */
    public Task(User user, String title, String description, LocationPoint locationPoint) {
        this(user, title, description);
        this.locationPoint = locationPoint;
    }

    /**
     * @param user User who created the task
     * @param title Title of the task
     * @param description Description of the task
     * @param locationPoint LocationPoint of the task
     * @param bids List of bid of the task
     */
    public Task(User user, String title, String description, LocationPoint locationPoint, ArrayList<Bid> bids) {
        this(user, title, description, locationPoint);
        this.bids = bids;
    }

    /**
     * @param user User who created the task
     * @param title Title of the task
     * @param description Description of the task
     * @param locationPoint LocationPoint of the task
     * @param chosenBid The accepted bid of the task
     */
    public Task(User user, String title, String description, LocationPoint locationPoint, Bid chosenBid) {
        this(user, title, description, locationPoint);
        this.chosenBid = chosenBid;
    }

    /**
     * @return User who created the task
     */
    public User getUser() {
        return this.user;
    }

    /**
     * Set title
     * @param title Title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return Title of the task
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Set description of the task
     * @param description Description of the task
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return Description of the task
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Set the accepted bid of the task and removes all other bids
     * @param chosenBid The bid that is accepted
     */
    public void setChosenBid(Bid chosenBid) {
        this.chosenBid = chosenBid;
    }

    /**
     * @return The accepted bid
     */
    public Bid getChosenBid() {
        return this.chosenBid;
    }

    /**
     * @return ArrayList of bids
     */
    public ArrayList<Bid> getBids() {
        if (this.bids == null) {
            this.bids = new ArrayList<>();
        }
        return this.bids;
    }

    /**
     * Set LocationPoint
     * @param locationPoint LocationPoint of the Task
     */
    public void setLocationPoint(LocationPoint locationPoint) {
        this.locationPoint = locationPoint;
    }

    /**
     * @return LocationPoint of the task
     */
    public LocationPoint getLocationPoint() {
        return this.locationPoint;
    }

    /**
     * Set Task to completed or not
     * @param completed Is task completed
     */
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    /**
     * @return Is task completed
     */
    public boolean isCompleted() {
        return this.completed;
    }
}