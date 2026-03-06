package product.orders.uiservice.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import product.orders.uiservice.cart.model.Cart;
import product.orders.uiservice.cart.model.CartItem;
import product.orders.uiservice.cart.service.CartService;
import product.orders.uiservice.checkout.model.CheckoutForm;
import product.orders.uiservice.checkout.service.CheckoutService;
import product.orders.uiservice.checkout.view.CheckoutItemViewModel;
import product.orders.uiservice.checkout.view.CheckoutViewModel;
import product.orders.uiservice.checkout.view.mapper.CheckoutViewMapper;
import product.orders.uiservice.exception.BackendServiceException;
import product.orders.uiservice.service.UserSessionService;

import java.util.UUID;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("CheckoutController submitCheckout handler tests")
@WebMvcTest(controllers = CheckoutController.class,
        excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class)
class CheckoutControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    CartService cartService;

    @MockitoBean
    CheckoutService checkoutService;

    @MockitoBean
    CheckoutViewMapper checkoutViewMapper;

    @MockitoBean
    UserSessionService userSessionService;


    @Test
    void testReviewCheckout_WhenCartIsEmpty_RedirectsToProducts() throws Exception {
        Cart emptyCart = mock(Cart.class);
        when(emptyCart.isEmpty()).thenReturn(true);
        when(cartService.getCart()).thenReturn(emptyCart);

        mockMvc.perform(get("/checkout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products"));
    }

    @Test
    void testReviewCheckout_WhenCartHasItems_ReturnsReviewView() throws Exception {
        Cart cart = mock(Cart.class);
        CheckoutViewModel viewModel = mock(CheckoutViewModel.class);

        when(cart.isEmpty()).thenReturn(false);
        when(cartService.getCart()).thenReturn(cart);
        when(checkoutViewMapper.toCheckoutView(cart)).thenReturn(viewModel);

        mockMvc.perform(get("/checkout"))
                .andExpect(status().isOk())
                .andExpect(view().name("checkout/review"))
                .andExpect(model().attribute("cart", viewModel))
                .andExpect(model().attributeExists("checkoutForm"));
    }

    @Test
    void testSubmitCheckout_CartIsEmpty_RedirectsToProducts() throws Exception {
        // Arrange
        Cart cart = mock(Cart.class);
        when(cart.isEmpty()).thenReturn(true);
        when(cartService.getCart()).thenReturn(cart);

        // Act
        mockMvc.perform(post("/checkout")
                        .param("customerEmail", "buyer@example.com")
                        .param("customerAddress", "123 Main Street"))
                // Assert
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products"));
    }

    @Test
    void testSubmitCheckout_FormHasErrors_ReturnsCheckoutReview() throws Exception {
        // Arrange
        Cart cart = mock(Cart.class);
        CartItem item = mock(CartItem.class);
        when(cart.isEmpty()).thenReturn(false);
        when(cart.items()).thenReturn(List.of(item));
        when(cartService.getCart()).thenReturn(cart);


        CheckoutItemViewModel checkoutItemViewModel = mock(CheckoutItemViewModel.class);
        CheckoutViewModel checkoutViewModel = mock(CheckoutViewModel.class);

        when(checkoutViewModel.items()).thenReturn(List.of(checkoutItemViewModel));
        when(checkoutItemViewModel.productName()).thenReturn("Test Product");
        when(checkoutItemViewModel.quantity()).thenReturn(2);

        when(checkoutViewMapper.toCheckoutView(cart)).thenReturn(checkoutViewModel);


        // Act
        mockMvc.perform(post("/checkout")
                        .param("customerEmail", "")
                        .param("customerAddress", ""))
                // Assert
                .andExpect(status().isOk())
                .andExpect(view().name("checkout/review"))
                .andExpect(model().attributeHasFieldErrors("checkoutForm", "customerEmail", "customerAddress"));

        verify(cartService, never()).clear();
    }

    @Test
    void testSubmitCheckout_ValidForm_CreatesOrderAndClearsCartAndRedirects() throws Exception {
        // Arrange
        Cart cart = mock(Cart.class);
        CartItem item = mock(CartItem.class);
        when(cart.isEmpty()).thenReturn(false);
        when(cart.items()).thenReturn(List.of(item));
        UUID orderId = UUID.randomUUID();
        when(cartService.getCart()).thenReturn(cart);
        when(checkoutService.createOrder(same(cart), any(CheckoutForm.class))).thenReturn(orderId);
        String checkoutSessionUrl = "https://example.com/checkout-session/";
        when(checkoutService.createCheckoutSession(any(UUID.class), any(Cart.class), anyString()))
                .thenReturn(checkoutSessionUrl);

        // Act
        mockMvc.perform(post("/checkout")
                        .param("customerEmail", "buyer@example.com")
                        .param("customerAddress", "123 Main Street"))
                // Assert
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(checkoutSessionUrl));

        verify(checkoutService).createOrder(same(cart), any(CheckoutForm.class));
        verify(cartService).clear();
    }

    @Test
    void testSubmitCheckout_BackendServiceFails_RedirectsToCheckoutWithFlashError() throws Exception {
        // Arrange
        Cart cart = mock(Cart.class);
        CartItem item = mock(CartItem.class);
        when(cart.isEmpty()).thenReturn(false);
        when(cart.items()).thenReturn(List.of(item));
        when(cartService.getCart()).thenReturn(cart);
        when(checkoutService.createOrder(same(cart), any(CheckoutForm.class)))
                .thenThrow(new BackendServiceException("Order service unavailable"));

        // Act
        mockMvc.perform(post("/checkout")
                        .param("customerEmail", "buyer@example.com")
                        .param("customerAddress", "123 Main Street"))
                // Assert
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/checkout"))
                .andExpect(flash().attribute("backendServiceError", true));

        verify(cartService, never()).clear();
    }
}
