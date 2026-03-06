package product.orders.uiservice.product.client;

import product.orders.uiservice.product.dto.ProductDto;

import java.util.List;
import java.util.UUID;

/**
 * Client for querying the Product service
 */
public interface ProductAPIClient {
    ProductDto getProduct(UUID productId);

    List<ProductDto> getActiveProducts(String category);
}
