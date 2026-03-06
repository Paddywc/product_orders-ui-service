package product.orders.uiservice.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import product.orders.uiservice.cart.model.Cart;
import product.orders.uiservice.cart.service.CartService;
import product.orders.uiservice.checkout.view.mapper.CheckoutViewMapper;
import product.orders.uiservice.service.UserSessionService;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TestExceptionThrowingController.class)
@Import(UIControllerAdvice.class)
@ActiveProfiles("test")
class UIControllerAdviceTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    CartService cartService;

    @MockitoBean
    UserSessionService userSessionService;

    @MockitoBean
    CheckoutViewMapper checkoutViewMapper;

    @BeforeEach
    void setUp() {
        // Needed for global model attributes when redirecting
        Cart cart = mock(Cart.class);
        when(cart.totalItemCount()).thenReturn(0);
        when(cartService.getCart()).thenReturn(cart);
        when(userSessionService.isAuthenticated()).thenReturn(false);
    }

    @Test
    void testHandleOrderNotFound_ExceptionThrown_RedirectsToOrders() throws Exception {
        mockMvc.perform(get("/test/order-not-found"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders"))
                .andExpect(flash().attribute("orderNotFound", true));
    }

    @Test
    void testHandleProductNotFound_ExceptionThrown_RedirectsToProducts() throws Exception {
        mockMvc.perform(get("/test/product-not-found"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products"))
                .andExpect(flash().attribute("productNotFound", true));
    }

    @Test
    void testHandleBackendServiceException_ExceptionThrown_ShowsServiceUnavailable() throws Exception {
        mockMvc.perform(get("/test/backend-error"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(view().name("error/service-unavailable"))
                .andExpect(model().attribute("errorMessage", "Service down"));
    }

    @Test
    void testHandleIllegalArgumentException_ExceptionThrown_ShowsBusinessError() throws Exception {
        mockMvc.perform(get("/test/illegal-arg"))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("error/business-error"))
                .andExpect(model().attribute("errorMessage", "Bad input"));
    }

    @Test
    void testHandleUnhandled_ExceptionThrown_ShowsGeneralError() throws Exception {
        mockMvc.perform(get("/test/generic"))
                .andExpect(status().isInternalServerError())
                .andExpect(view().name("error/general-error"))
                .andExpect(model().attribute("errorMessage", "Boom"));
    }
}
