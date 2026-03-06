package product.orders.uiservice.product.service;

import org.springframework.stereotype.Service;
import product.orders.uiservice.product.client.ProductAPIClient;
import product.orders.uiservice.product.dto.ProductDto;

import java.util.List;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductAPIClient apiClient;

    public ProductServiceImpl(ProductAPIClient apiClient) {
        this.apiClient = apiClient;
    }

    @Override
    public ProductDto getProduct(UUID productId){
        return apiClient.getProduct(productId);
    }

    @Override
    public List<ProductDto> getActiveProducts(String category) {
        return apiClient.getActiveProducts(category);
    }
}
