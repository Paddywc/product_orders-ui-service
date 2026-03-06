package product.orders.uiservice.cart.repository;

import product.orders.uiservice.cart.model.Cart;

/**
 * Repository for managing the user's shopping cart.
 */
public interface CartRepository {
    /**
     * Get the current user's cart
     * @return the current user's cart
     */
    Cart get();

    /**
     * Save the current user's cart
     * @param cart the cart to save
     */
    void save(Cart cart);

    /**
     * Clear the current user's cart (remove all items)
     */
    void clear();
}
