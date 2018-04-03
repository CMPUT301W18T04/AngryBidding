package ca.ualberta.angrybidding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import ca.ualberta.angrybidding.map.LocationPoint;

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
    private Status status;
    private Date dateTime;

    /**
     * @param user User who created the task
     * @param title Title of the task
     */
    public Task(User user, String title) {
        this.user = user;
        this.title = title;
        this.status = Status.REQUESTED;
        this.dateTime = new Date(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis());
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
        this.getBids().clear();
        updateStatus();
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

    public void updateStatus(){
        if(this.status == Status.COMPLETED){
            return;
        }else if(getChosenBid() != null){
            this.status = Status.ASSIGNED;
        }else if(getBids().size() == 0){
            this.status = Status.REQUESTED;
        }else{
            this.status = Status.BIDDED;
        }
    }

    public Status getStatus(){
        updateStatus();
        return this.status;
    }

    public void setCompleted(){
        if(getStatus() == Status.ASSIGNED){
            this.status = Status.COMPLETED;
        }
    }

    public Date getDateTime(){
        return this.dateTime;
    }

    public enum Status{
        REQUESTED,
        BIDDED,
        ASSIGNED,
        COMPLETED;

        public static Status getStatus(String statusString){
            statusString = statusString.trim().toLowerCase();
            if(statusString.equalsIgnoreCase("requested")){
                return REQUESTED;
            }else if(statusString.equalsIgnoreCase("bidded")){
                return BIDDED;
            }else if(statusString.equalsIgnoreCase("assigned")){
                return ASSIGNED;
            }else if(statusString.equalsIgnoreCase("completed")){
                return COMPLETED;
            }else{
                return null;
            }
        }
    }
}