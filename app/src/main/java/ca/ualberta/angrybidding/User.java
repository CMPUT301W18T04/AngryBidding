package ca.ualberta.angrybidding;

public class User {
    private String username;
    private String emailAddress;

    public User(String username) {
        this.username = username.toLowerCase().trim();
    }

    public User(String username, String emailAddress) {
        this(username);
        this.emailAddress = emailAddress.toLowerCase().trim();
    }

    public String getUsername() {
        return this.username;
    }

    public String getEmailAddress() {
        return this.emailAddress;
    }
}
