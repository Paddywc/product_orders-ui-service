package product.orders.uiservice.checkout.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import product.orders.uiservice.cart.model.Cart;
import product.orders.uiservice.checkout.client.CheckoutOrderAPIClient;
import product.orders.uiservice.checkout.client.CheckoutPaymentAPIClient;
import product.orders.uiservice.checkout.model.CheckoutForm;

import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckoutServiceImplTest {

    @Mock
    CheckoutOrderAPIClient orderAPIClient;

    @Mock
    CheckoutPaymentAPIClient paymentAPIClient;

    @InjectMocks
    CheckoutServiceImpl checkoutService;


    @DisplayName("createOrder delegates to the API client with the provided inputs")
    @Test
    void testCreateOrder_ValidCartAndForm_DelegatesToApiClient() {
        // Arrange
        Cart cart = new Cart();
        CheckoutForm form = new CheckoutForm("buyer@example.com", "456 Market Avenue");
        UUID expectedOrderId = UUID.randomUUID();
        when(orderAPIClient.createOrder(cart, form)).thenReturn(expectedOrderId);

        // Act
        checkoutService.createOrder(cart, form);

        // Assert
        verify(orderAPIClient).createOrder(cart, form);
        verifyNoMoreInteractions(orderAPIClient);
    }

    @DisplayName("createCheckoutSession delegates to the API client with the provided inputs")
    @Test
    void testCreateCheckoutSession_ValidCartAndAddress_DelegatesToApiClient() {
        // Arrange
        Cart cart = new Cart();
        UUID orderId = UUID.randomUUID();
        String customerEmail = "test@example.com";

        // Act
        checkoutService.createCheckoutSession(orderId, cart, customerEmail);

        // Assert
        verify(paymentAPIClient).createCheckoutSession(orderId, cart, customerEmail);
        verifyNoMoreInteractions(paymentAPIClient);
    }


}
