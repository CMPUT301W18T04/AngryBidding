package ca.ualberta.angrybidding;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Bid {
    private User user;
    private BigDecimal price;
    private boolean declined;

    /**
     * @param user  User who placed the bid
     * @param price Price of the bid
     */
    public Bid(User user, BigDecimal price) {
        this.user = user;
        this.price = price;
        this.declined = false;
    }

    /**
     * @param user  User who placed the bid
     * @param price Price of the bid
     */
    public Bid(User user, double price) {
        this(user, new BigDecimal(price));
    }

    /**
     * @return User who placed the bid
     */
    public User getUser() {
        return this.user;
    }

    /**
     * @return Price of the bid
     */
    public BigDecimal getPrice() {
        return this.price;
    }

    public void setDeclined(boolean declined) {
        this.declined = declined;
    }

    public boolean isDeclined() {
        return this.declined;
    }

    /**
     * @return Price of the bid as String in the format of $[xxx].xx
     */
    public String getPriceString() {
        return "$" + this.price.setScale(2, RoundingMode.HALF_UP).toString(); // round to 2 decimal places
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Bid)) {
            return false;
        } else {
            return ((Bid) other).getUser().equals(getUser()) && ((Bid) other).getPrice().equals(getPrice());
        }
    }
}
