package product.orders.uiservice.checkout.client;

import product.orders.uiservice.cart.model.Cart;
import product.orders.uiservice.checkout.model.CheckoutForm;

import java.util.UUID;

/**
 * Client for the order service for checkout operations
 */
public interface CheckoutOrderAPIClient {
    /**
     * Send an order to the order service for saving and returns its id
     * @param cart the card that contains the order items
     * @param checkoutForm form containing user details
     * @return the id of the created order
     */
    UUID createOrder(Cart cart, CheckoutForm checkoutForm);
}
