package ca.ualberta.angrybidding;

import java.util.ArrayList;

public class Task {
    private User user;
    private String title;
    private String description;
    private ArrayList<Bid> bids;

    public Task(User user, String title){
        this.user = user;
        this.title = title;
    }

    public Task(User user, String title, String description){
        this(user, title);
        this.description = description;
    }

    public Task(User user, String title, String description, ArrayList<Bid> bids){
        this(user, title, description);
        this.bids = bids;
    }

    public User getUser(){
        return this.user;
    }

    public String getTitle(){
        return this.title;
    }

    public String getDescription(){
        return this.description;
    }

    public ArrayList<Bid> getBids(){
        return this.bids;
    }
}