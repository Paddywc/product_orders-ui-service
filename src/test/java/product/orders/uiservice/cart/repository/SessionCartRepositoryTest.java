package product.orders.uiservice.cart.repository;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import product.orders.uiservice.cart.model.Cart;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

class SessionCartRepositoryTest {

    private HttpSession session;
    private SessionCartRepository repository;

    @BeforeEach
    void setUp() {
        session = mock(HttpSession.class);
        repository = new SessionCartRepository(session);
    }

    @Test
    void testGet_ExistingCartInSession_ReturnsCartFromSession() {
        // Arrange
        Cart existingCart = new Cart();
        when(session.getAttribute("CART")).thenReturn(existingCart);

        // Act
        Cart result = repository.get();

        // Assert
        assertThat(result).isSameAs(existingCart);
        // Verify that the card wasn't written to the session
        verify(session, never()).setAttribute(anyString(), any());
    }

    @Test
    void testGet_NoCartInSession_StoresAndReturnsNewCart() {
        // Arrange
        when(session.getAttribute("CART")).thenReturn(null);

        // Act
        Cart result = repository.get();

        // Assert
        assertThat(result).isNotNull();

        ArgumentCaptor<Cart> cartCaptor = ArgumentCaptor.forClass(Cart.class);
        verify(session).setAttribute(eq("CART"), cartCaptor.capture());

        assertThat(cartCaptor.getValue()).isSameAs(result);
    }

    @Test
    void testSave_SavesCartInSession() {
        // Arrange
        Cart cart = new Cart();

        // Act
        repository.save(cart);

        // Assert
        verify(session).setAttribute("CART", cart);
    }

    @Test
    void testClear_RemovesCartFromSession() {
        // Act
        repository.clear();

        // Assert
        verify(session).removeAttribute("CART");
    }
}