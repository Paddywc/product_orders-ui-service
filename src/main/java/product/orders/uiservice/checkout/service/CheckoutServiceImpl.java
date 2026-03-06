package product.orders.uiservice.checkout.service;

import org.springframework.stereotype.Service;
import product.orders.uiservice.cart.model.Cart;
import product.orders.uiservice.checkout.client.CheckoutOrderAPIClient;
import product.orders.uiservice.checkout.client.CheckoutPaymentAPIClient;
import product.orders.uiservice.checkout.model.CheckoutForm;

import java.util.UUID;

@Service
public class CheckoutServiceImpl implements CheckoutService {

    private final CheckoutOrderAPIClient orderAPiClient;

    private final CheckoutPaymentAPIClient paymentAPiClient;

    public CheckoutServiceImpl(CheckoutOrderAPIClient orderAPiClient, CheckoutPaymentAPIClient paymentAPiClient) {
        this.orderAPiClient = orderAPiClient;
        this.paymentAPiClient = paymentAPiClient;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UUID createOrder(Cart cart, CheckoutForm checkoutForm){
        return orderAPiClient.createOrder(cart, checkoutForm);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String createCheckoutSession(UUID orderId, Cart cart, String customerEmail){
        return paymentAPiClient.createCheckoutSession(orderId, cart, customerEmail);
    }

}
