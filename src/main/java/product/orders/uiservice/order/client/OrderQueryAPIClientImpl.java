package product.orders.uiservice.order.client;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import product.orders.uiservice.exception.BackendServiceException;
import product.orders.uiservice.exception.OrderNotFoundException;
import product.orders.uiservice.order.dto.OrderDto;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Component
public class OrderQueryAPIClientImpl implements OrderQueryAPIClient {
    private final WebClient webClient;

    public OrderQueryAPIClientImpl(WebClient orderWebClient) {
        this.webClient = orderWebClient;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OrderDto getOrder(UUID orderId) {
        return webClient.get()
                .uri("/orders/{id}", orderId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(String.class)
                        .defaultIfEmpty("")
                        .flatMap(body -> {
                            if (response.statusCode().equals(HttpStatusCode.valueOf(404))) {
                                return Mono.error(new OrderNotFoundException(orderId));
                            }
                            return Mono.error(new BackendServiceException("Order service unavailable"));
                        }))
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        response.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .flatMap(body -> Mono.error(new BackendServiceException(
                                        "Order service unavailable"
                                )))
                )
                .bodyToMono(OrderDto.class)
                .onErrorMap(WebClientRequestException.class,
                        ex -> new BackendServiceException("Order service unavailable", ex))
                .block();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<OrderDto> getCustomerOrders(UUID customerId) {
        return webClient
                .get()
                .uri("/orders/customer/{customerId}", customerId)
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        response.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .flatMap(body -> Mono.error(new BackendServiceException(
                                        "Order service unavailable"
                                )))
                )
                .bodyToFlux(OrderDto.class)
                .collectList()
                .onErrorMap(WebClientRequestException.class,
                        ex -> new BackendServiceException("Order service unavailable", ex))
                .block();
    }
}
