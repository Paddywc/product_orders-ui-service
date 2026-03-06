package product.orders.uiservice.checkout.client;

import product.orders.uiservice.cart.model.Cart;

import java.util.UUID;

public interface CheckoutPaymentAPIClient {
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
