package ca.ualberta.angrybidding;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Bid {
    private User user;
    private BigDecimal price;

    public Bid(User user, BigDecimal price) {
        this.user = user;
        this.price = price;
    }

    public Bid(User user, double price) {
        this(user, new BigDecimal(price));
    }

    public User getUser() {
        return this.user;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public String getPriceString() {
        return "$" + this.price.setScale(2, RoundingMode.HALF_UP).toString(); // round to 2 decimal places
    }
}
