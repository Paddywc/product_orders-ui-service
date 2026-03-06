package product.orders.uiservice.cart.service;

import product.orders.uiservice.cart.model.Cart;

import java.util.UUID;

/**
 * Service for managing the shopping cart
 */
public interface CartService {
    /**
     * Get the current user's cart
     * @return the current user's cart
     */
    Cart getCart();

    /**
     * Add an item to the cart, merging with existing item (sum the quantities) if it already exist in the cart
     * @param productId the unique identifier of the product
     * @param quantity the quantity to add
     */
    void addItem(UUID productId, int quantity);

    /**
     * Completely remove an item from the cart
     * @param productId the unique identifier of the product to remove
     */
    void removeItem(UUID productId);

    /**
     * Clear the current user's cart (remove all items)
     */
    void clear();
}
