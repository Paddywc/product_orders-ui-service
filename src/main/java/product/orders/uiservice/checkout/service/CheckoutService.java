package product.orders.uiservice.checkout.service;

import product.orders.uiservice.cart.model.Cart;
import product.orders.uiservice.checkout.model.CheckoutForm;

import java.util.UUID;

public interface CheckoutService {
    /**
     * Send an order to the order service for saving and returns its id
     *
     * @param cart         the card that contains the order items
     * @param checkoutForm form containing user details
     * @return the id of the created order
     */
    UUID createOrder(Cart cart, CheckoutForm checkoutForm);

    /**
     * Have the payment service create a checkout session for the given order and return its url
     *
     * @param orderId       the order id
     * @param cart          the cart containing the order items
     * @param customerEmail the customer email
     * @return the stripe checkout session url
     */
    String createCheckoutSession(UUID orderId, Cart cart, String customerEmail);
}
