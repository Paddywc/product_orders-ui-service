package product.orders.uiservice.product.service;

import product.orders.uiservice.product.dto.ProductDto;

import java.util.List;
import java.util.UUID;

/**
 * Service for querying the Product microservice
 */
public interface ProductService {
    ProductDto getProduct(UUID productId);

    List<ProductDto> getActiveProducts(String category);
}
