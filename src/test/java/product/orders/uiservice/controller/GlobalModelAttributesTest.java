package product.orders.uiservice.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import product.orders.uiservice.cart.model.Cart;
import product.orders.uiservice.cart.service.CartService;
import product.orders.uiservice.service.UserSessionService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for GlobalModelAttributes controller advice and its cartItemCount model attribute method.
 */
class GlobalModelAttributesTest {

    GlobalModelAttributes globalModelAttributes;

    @Mock
    CartService cartService;

    @Mock
    UserSessionService userSessionService;

    @BeforeEach
    void setUp() {
        cartService = mock(CartService.class);
        userSessionService = mock(UserSessionService.class);
        globalModelAttributes = new GlobalModelAttributes(cartService, userSessionService);
    }

    @Test
    void testCartItemCount_EmptyCart_ReturnsZero() {
        // Arrange
        Cart cart = mock(Cart.class);
        when(cart.totalItemCount()).thenReturn(0);
        when(cartService.getCart()).thenReturn(cart);

        // Act
        int result = globalModelAttributes.cartItemCount();

        // Assert
        assertThat(result).isZero();
    }

    @Test
    void testCartItemCount_MultipleItems_ReturnsTotalCount() {
        // Arrange
        Cart cart = mock(Cart.class);
        when(cart.totalItemCount()).thenReturn(5);
        when(cartService.getCart()).thenReturn(cart);

        // Act
        int result = globalModelAttributes.cartItemCount();

        // Assert
        assertThat(result).isEqualTo(5);
    }

    @Test
    void testIsAuthenticated_UserAuthenticated_ReturnsTrue() {
        // Arrange
        when(userSessionService.isAuthenticated()).thenReturn(true);

        // Act
        boolean result = globalModelAttributes.isAuthenticated();

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void testIsAuthenticated_UserNotAuthenticated_ReturnsFalse() {
        // Arrange
        when(userSessionService.isAuthenticated()).thenReturn(false);

        // Act
        boolean result = globalModelAttributes.isAuthenticated();

        // Assert
        assertThat(result).isFalse();
    }
}
