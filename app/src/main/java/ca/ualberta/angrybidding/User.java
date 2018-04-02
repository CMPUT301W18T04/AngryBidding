package ca.ualberta.angrybidding;

/**
 * User model class
 * Contains username and email address
 */
public class User {
    private String username;
    private String emailAddress;

    /**
     * @param username Username of the user
     */
    public User(String username) {
        this.username = username.toLowerCase().trim();
    }

    /**
     * @param username     Username of the user
     * @param emailAddress Email address of the user
     */
    public User(String username, String emailAddress) {
        this(username);
        this.emailAddress = emailAddress.toLowerCase().trim();
    }

    /**
     * @return Username of the user
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * @return Email address of the user
     */
    public String getEmailAddress() {
        return this.emailAddress;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof User && ((User) other).getUsername().equals(getUsername());
    }
}
