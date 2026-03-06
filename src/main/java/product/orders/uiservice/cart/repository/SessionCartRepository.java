package product.orders.uiservice.cart.repository;

import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import product.orders.uiservice.cart.model.Cart;

/**
 * Implementation of {@link CartRepository} that stores the cart in the HTTP session.
 */
@Repository
@Profile({"web", "prod", "local"})
public class SessionCartRepository implements CartRepository {

    /**
     * Unique for storing the cart in the HTTP session
     */
    private static final String CART_SESSION_KEY = "CART";

    private final HttpSession session;

    public SessionCartRepository(HttpSession session) {
        this.session = session;
    }

    @Override
    public Cart get() {
        Object value = session.getAttribute(CART_SESSION_KEY);

        if (value instanceof Cart cart) {
            return cart;
        }

        Cart cart = new Cart();
        session.setAttribute(CART_SESSION_KEY, cart);
        return cart;
    }

    @Override
    public void save(Cart cart) {
        session.setAttribute(CART_SESSION_KEY, cart);
    }

    @Override
    public void clear() {
        session.removeAttribute(CART_SESSION_KEY);
    }
}

