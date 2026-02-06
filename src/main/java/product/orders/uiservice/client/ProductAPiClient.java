package product.orders.uiservice.client;

import product.orders.uiservice.client.dto.ProductDto;

import java.util.List;
import java.util.UUID;

public interface ProductAPiClient {
    ProductDto getProduct(UUID productId);

    List<ProductDto> getActiveProducts(String category);
}
