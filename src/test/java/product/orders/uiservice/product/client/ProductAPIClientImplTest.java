package product.orders.uiservice.product.client;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import product.orders.uiservice.exception.BackendServiceException;
import product.orders.uiservice.exception.ProductNotFoundException;
import product.orders.uiservice.product.dto.ProductDto;
import product.orders.uiservice.support.WebClientTestSupport;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductAPIClientImplTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testGetProduct_200Response_ReturnsProduct() {
        UUID productId = UUID.randomUUID();
        ProductDto dto = productDto(productId);

        String body = objectMapper.writeValueAsString(dto);

        ProductAPIClientImpl client = WebClientTestSupport.webClientWithResponse(
                WebClientTestSupport.jsonResponse(HttpStatus.OK, body),
                ProductAPIClientImpl::new
        );

        ProductDto result = client.getProduct(productId);

        assertThat(result.productId()).isEqualTo(productId);
    }

    @Test
    void testGetProduct_404Response_ThrowsProductNotFoundException() {
        UUID productId = UUID.randomUUID();

        ProductAPIClientImpl client = WebClientTestSupport.webClientWithResponse(
                WebClientTestSupport.jsonResponse(HttpStatus.NOT_FOUND, "not found"),
                ProductAPIClientImpl::new
        );

        assertThatThrownBy(() -> client.getProduct(productId))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void testGetProduct_5xxResponse_ThrowsBackendServiceException() {
        UUID productId = UUID.randomUUID();

        ProductAPIClientImpl client = WebClientTestSupport.webClientWithResponse(
                WebClientTestSupport.jsonResponse(HttpStatus.SERVICE_UNAVAILABLE, "down"),
                ProductAPIClientImpl::new
        );

        assertThatThrownBy(() -> client.getProduct(productId))
                .isInstanceOf(BackendServiceException.class)
                .hasMessageContaining("Product service unavailable");
    }

    @Test
    void testGetActiveProducts_200Response_ReturnsProducts() throws Exception {
        ProductDto dto1 = productDto(UUID.randomUUID());
        ProductDto dto2 = productDto(UUID.randomUUID());

        String body = objectMapper.writeValueAsString(List.of(dto1, dto2));

        ProductAPIClientImpl client = WebClientTestSupport.webClientWithResponse(
                WebClientTestSupport.jsonResponse(HttpStatus.OK, body),
                ProductAPIClientImpl::new
        );

        List<ProductDto> result = client.getActiveProducts(null);

        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    void testGetActiveProducts_ErrorResponse_ThrowsBackendServiceException() {
        IOException ioException = new IOException("Connection refused");

        ProductAPIClientImpl client = WebClientTestSupport.webClientWithFailure(
                new WebClientRequestException(
                        ioException,
                        HttpMethod.GET,
                        URI.create(""),
                        HttpHeaders.EMPTY
                ),
                ProductAPIClientImpl::new
        );

        assertThatThrownBy(() -> client.getActiveProducts(null))
                .isInstanceOf(BackendServiceException.class);
    }

    private ProductDto productDto(UUID id) {
        return new ProductDto(
                id,
                "Widget",
                "A tool",
                250L,
                "ELECTRONICS",
                "CREATED");
    }
}
