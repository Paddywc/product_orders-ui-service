package product.orders.uiservice.product.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import product.orders.uiservice.product.dto.ProductDto;
import product.orders.uiservice.exception.BackendServiceException;
import product.orders.uiservice.exception.ProductNotFoundException;

import java.util.List;
import java.util.UUID;

@Component
public class ProductAPIClientImpl implements ProductAPIClient {
    private final WebClient webClient;

    public ProductAPIClientImpl(WebClient productWebClient) {
        this.webClient = productWebClient;
    }

    @Override
    public ProductDto getProduct(UUID productId) {
        return webClient.get()
                .uri("/products/{id}", productId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        response.bodyToMono(String.class)
                                .map(body ->
                                        new ProductNotFoundException(productId)
                                )
                )
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        response.bodyToMono(String.class)
                                .map(body ->
                                        new BackendServiceException(
                                                "Product service unavailable"
                                        )
                                )
                )
                .bodyToMono(ProductDto.class)
                .onErrorMap(WebClientRequestException.class,
                        ex -> new BackendServiceException("Product service unavailable", ex))
                .block();
    }


    @Override
    public List<ProductDto> getActiveProducts(String category) {
        return webClient.get()
                .uri(uriBuilder -> {
                    var builder = uriBuilder.path("/products");
                    if (category != null) {
                        builder.queryParam("category", category);
                    }
                    return builder.build();
                })
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class)
                                .map(body ->
                                        new BackendServiceException(
                                                "Failed to fetch products"
                                        )
                                )
                )
                .bodyToMono(new ParameterizedTypeReference<List<ProductDto>>() {
                })
                .onErrorMap(WebClientRequestException.class,
                        ex -> new BackendServiceException("Product service unavailable", ex))
                .block();
    }

}
