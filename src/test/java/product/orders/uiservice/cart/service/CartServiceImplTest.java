package product.orders.uiservice.cart.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import product.orders.uiservice.cart.model.Cart;
import product.orders.uiservice.cart.repository.CartRepository;
import product.orders.uiservice.product.dto.ProductDto;
import product.orders.uiservice.product.service.ProductService;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    CartRepository cartRepository;

    @Mock
    ProductService productService;

    @InjectMocks
    CartServiceImpl cartService;

    Cart cart;

    @BeforeEach
    void setUp() {
        cart = new Cart();
        when(cartRepository.get()).thenReturn(cart);
    }

    @Test
    void testGetCart_returnsCartFromRepository() {
        // Assert
        Cart result = cartService.getCart();

        assertThat(result).isSameAs(cart);
        verify(cartRepository).get();
        verifyNoMoreInteractions(cartRepository);
    }

    @Test
    void testAddItem_ValidProduct_AddsProductToCartAndSaves() {
        // Arrange
        UUID productId = UUID.randomUUID();

        ProductDto product = new ProductDto(
                productId,
                "Test Product",
                "Description",
                500L,
                "CATEGORY",
                "ACTIVE"
        );

        when(productService.getProduct(productId)).thenReturn(product);

        // Act
        cartService.addItem(productId, 2);

        // Assert
        assertThat(cart.items()).hasSize(1);
        assertThat(cart.totalItemCount()).isEqualTo(2);
        assertThat(cart.totalAmountUsdCents()).isEqualTo(1000L);

        verify(cartRepository).save(cart);
    }

    @Test
    void testRemoveItem_ValidId_RemovesItemAndSaves() {
        // Arrange
        UUID productId = UUID.randomUUID();

        ProductDto product = new ProductDto(
                productId,
                "Test Product",
                "Description",
                300L,
                "CATEGORY",
                "ACTIVE"
        );

        when(productService.getProduct(productId)).thenReturn(product);

        // Act
        cartService.addItem(productId, 1);
        cartService.removeItem(productId);

        // Assert
        assertThat(cart.items()).isEmpty();

        verify(cartRepository, times(2)).save(cart);
    }

    @MockitoSettings(strictness = Strictness.LENIENT) // only test where cartRepository.get is not called
    @Test
    void testClear_CallsRepositoryClear() {
        cartService.clear();

        verify(cartRepository).clear();
        verifyNoMoreInteractions(cartRepository);
    }
}

