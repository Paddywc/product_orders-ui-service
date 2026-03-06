package product.orders.uiservice.controller;

import jakarta.servlet.RequestDispatcher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import product.orders.uiservice.cart.model.Cart;
import product.orders.uiservice.cart.service.CartService;
import product.orders.uiservice.checkout.view.mapper.CheckoutViewMapper;
import product.orders.uiservice.service.UserSessionService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.mockito.Mockito.*;


@WebMvcTest(controllers = ErrorPageController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = CheckoutControllerAdvice.class
        ))
class ErrorPageControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    CartService cartService;

    @MockitoBean
    UserSessionService userSessionService;

    @BeforeEach
    void setUp() {
        // Needed for global model attributes
        Cart cart = mock(Cart.class);
        when(cart.totalItemCount()).thenReturn(0);
        when(cartService.getCart()).thenReturn(cart);
        when(userSessionService.isAuthenticated()).thenReturn(false);
    }

    @Test
    @DisplayName("404 status renders not-found page")
    void testHandleError_404_NotFoundViewReturned() throws Exception {
        mockMvc.perform(get("/error")
                        .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 404)
                        .requestAttr(RequestDispatcher.ERROR_MESSAGE, "Page not found"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("error/not-found"))
                .andExpect(model().attribute("errorMessage", "Page not found"));
    }

    @Test
    @DisplayName("405 status renders method-not-allowed page")
    void testHandleError_405_MethodNotAllowedViewReturned() throws Exception {
        mockMvc.perform(get("/error")
                        .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 405))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(view().name("error/method-not-allowed"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    @DisplayName("503 status renders service-unavailable page")
    void testHandleError_503_ServiceUnavailableViewReturned() throws Exception {
        mockMvc.perform(get("/error")
                        .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 503))
                .andExpect(status().isServiceUnavailable())
                .andExpect(view().name("error/service-unavailable"));
    }

    @Test
    @DisplayName("400 status renders business-error page")
    void testHandleError_400_BusinessErrorViewReturned() throws Exception {
        mockMvc.perform(get("/error")
                        .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 400)
                        .requestAttr(RequestDispatcher.ERROR_MESSAGE, "Invalid input"))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("error/business-error"))
                .andExpect(model().attribute("errorMessage", "Invalid input"));
    }

    @Test
    @DisplayName("Exception message is used when no explicit error message is present")
    void testHandleError_ExceptionMessageUsed() throws Exception {
        RuntimeException exception = new RuntimeException("Something exploded");

        mockMvc.perform(get("/error")
                        .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 500)
                        .requestAttr(RequestDispatcher.ERROR_EXCEPTION, exception))
                .andExpect(status().isInternalServerError())
                .andExpect(view().name("error/general-error"))
                .andExpect(model().attribute("errorMessage", "Something exploded"));
    }

    @Test
    @DisplayName("Fallback message is used when no message or exception is present")
    void testHandleError_FallbackMessageUsed() throws Exception {
        mockMvc.perform(get("/error")
                        .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 500))
                .andExpect(status().isInternalServerError())
                .andExpect(view().name("error/general-error"))
                .andExpect(model().attribute("errorMessage", "Unexpected error"));
    }
}
