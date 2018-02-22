package ca.ualberta.angrybidding;

public class User {
    private String username;
    private String emailAddress;

    public User(String username){
        this.username = username;
    }

    public User(String username, String emailAddress){
        this(username);
        this.emailAddress = emailAddress;
    }

    public String getUsername(){
        return this.username;
    }

    public String getEmailAddress(){
        return this.emailAddress;
    }

    public static User login(String username, String password){
        //TODO Implement user login

        return null;
        //return new User(username);
    }

    public static User signUp(String username, String password, String emailAddress){
        //TODO Implement user sign up

        return null;
        //return new User(username, emailAddress);
    }
}
