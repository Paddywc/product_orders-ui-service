package product.orders.uiservice.order.client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import product.orders.uiservice.order.enums.OrderProgress;
import product.orders.uiservice.exception.BackendServiceException;
import product.orders.uiservice.exception.OrderNotFoundException;
import product.orders.uiservice.order.dto.OrderDto;
import product.orders.uiservice.order.enums.PaymentStatus;
import product.orders.uiservice.support.WebClientTestSupport;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class OrderQueryAPIClientImplTest {

    @Test
    void testGetOrder_200Response_ReturnsOrder() {
        UUID orderId = UUID.randomUUID();

        OrderDto dto = orderDto(orderId);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(dto);

        ClientResponse response = ClientResponse
                .create(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(json)
                .build();

        OrderQueryAPIClientImpl client = WebClientTestSupport.webClientWithResponse(
                response,
                OrderQueryAPIClientImpl::new
        );

        OrderDto result = client.getOrder(orderId);

        assertThat(result).isEqualTo(dto);
    }

    @Test
    void testGetOrder_404Response_ThrowsOrderNotFound() {
        UUID orderId = UUID.randomUUID();

        ClientResponse response = ClientResponse
                .create(HttpStatus.NOT_FOUND)
                .build();

        OrderQueryAPIClientImpl client = WebClientTestSupport.webClientWithResponse(
                response,
                OrderQueryAPIClientImpl::new
        );

        assertThatThrownBy(() -> client.getOrder(orderId))
                .isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    void testGetOrder_5xxResponse_ThrowsBackendServiceException() {
        UUID orderId = UUID.randomUUID();

        ClientResponse response = ClientResponse
                .create(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Boom")
                .build();

        OrderQueryAPIClientImpl client = WebClientTestSupport.webClientWithResponse(
                response,
                OrderQueryAPIClientImpl::new
        );

        assertThatThrownBy(() -> client.getOrder(orderId))
                .isInstanceOf(BackendServiceException.class)
                .hasMessageContaining("Order service unavailable");
    }

    @Test
    void testGetOrder_RequestFailure_ThrowsBackendServiceException() {
        UUID orderId = UUID.randomUUID();

        IOException ioException = new IOException("Connection refused");

        OrderQueryAPIClientImpl client = WebClientTestSupport.webClientWithFailure(
                new WebClientRequestException(
                        ioException,
                        HttpMethod.GET,
                        URI.create("http://orders/orders/" + orderId),
                        HttpHeaders.EMPTY
                ),
                OrderQueryAPIClientImpl::new
        );

        assertThatThrownBy(() -> client.getOrder(orderId))
                .isInstanceOf(BackendServiceException.class)
                .hasMessageContaining("Order service unavailable");
    }

    @Test
    void testGetCustomerOrders_200Response_ReturnsOrders() {
        UUID customerId = UUID.randomUUID();

        OrderDto dto1 = orderDto(UUID.randomUUID());
        OrderDto dto2 = orderDto(UUID.randomUUID());
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(List.of(dto1, dto2));

        ClientResponse response = ClientResponse
                .create(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(json)
                .build();

        OrderQueryAPIClientImpl client = WebClientTestSupport.webClientWithResponse(
                response,
                OrderQueryAPIClientImpl::new
        );

        List<OrderDto> result = client.getCustomerOrders(customerId);

        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(dto1, dto2);
    }

    @Test
    void testGetCustomerOrders_5xxResponse_ThrowsBackendServiceException() {
        UUID customerId = UUID.randomUUID();

        ClientResponse response = ClientResponse
                .create(HttpStatus.SERVICE_UNAVAILABLE)
                .build();

        OrderQueryAPIClientImpl client = WebClientTestSupport.webClientWithResponse(
                response,
                OrderQueryAPIClientImpl::new
        );

        assertThatThrownBy(() -> client.getCustomerOrders(customerId))
                .isInstanceOf(BackendServiceException.class);
    }

    @Test
    void testGetCustomerOrders_RequestFailure_ThrowsBackendServiceException() {
        UUID customerId = UUID.randomUUID();

        IOException ioException = new IOException("Connection refused");

        OrderQueryAPIClientImpl client = WebClientTestSupport.webClientWithFailure(
                new WebClientRequestException(
                        ioException,
                        HttpMethod.GET,
                        URI.create("http://orders/orders/customer/" + customerId),
                        HttpHeaders.EMPTY
                ),
                OrderQueryAPIClientImpl::new
        );


        assertThatThrownBy(() -> client.getCustomerOrders(customerId))
                .isInstanceOf(BackendServiceException.class);
    }

    // ----------------------------------------------------
    // Helpers
    // ----------------------------------------------------

    private OrderDto orderDto(UUID orderId) {
        return new OrderDto(
                orderId,
                List.of(),
                UUID.randomUUID(),
                "email@email.com",
                "123 Fake St",
                1000L,
                "USD",
                "CREATED",
                OrderProgress.AWAITING_PAYMENT,
                PaymentStatus.PENDING,
                Instant.now());
    }

}
