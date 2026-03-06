package product.orders.uiservice.product.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import product.orders.uiservice.product.client.ProductAPIClient;
import product.orders.uiservice.product.dto.ProductDto;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    ProductAPIClient apiClient;

    @InjectMocks
    ProductServiceImpl productService;

    @Test
    void testGetProduct_ValidProductId_DelegatesToApiClient() {
        // Arrange
        UUID productId = UUID.randomUUID();
        ProductDto product = mock(ProductDto.class);

        when(apiClient.getProduct(productId)).thenReturn(product);

        // Act
        ProductDto result = productService.getProduct(productId);

        // Assert
        verify(apiClient).getProduct(productId);
        verifyNoMoreInteractions(apiClient);
        // Optional sanity check
        assert result == product;
    }

    @Test
    void testGetActiveProducts_WithCategory_DelegatesToApiClient() {
        // Arrange
        String category = "ELECTRONICS";
        List<ProductDto> products = List.of(mock(ProductDto.class));

        when(apiClient.getActiveProducts(category)).thenReturn(products);

        // Act
        List<ProductDto> result = productService.getActiveProducts(category);

        // Assert
        verify(apiClient).getActiveProducts(category);
        verifyNoMoreInteractions(apiClient);
        assert result == products;
    }

    @Test
    void testGetActiveProducts_NullCategory_DelegatesToApiClient() {
        // Arrange
        List<ProductDto> products = List.of();

        when(apiClient.getActiveProducts(null)).thenReturn(products);

        // Act
        List<ProductDto> result = productService.getActiveProducts(null);

        // Assert
        verify(apiClient).getActiveProducts(null);
        verifyNoMoreInteractions(apiClient);
        assert result == products;
    }
}