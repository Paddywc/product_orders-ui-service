package product.orders.uiservice.checkout.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import product.orders.uiservice.cart.model.Cart;
import product.orders.uiservice.cart.model.CartItem;
import product.orders.uiservice.checkout.dto.CreateOrderItemRequest;
import product.orders.uiservice.checkout.dto.CreateOrderRequest;
import product.orders.uiservice.checkout.dto.CreateOrderResponse;
import product.orders.uiservice.checkout.model.CheckoutForm;
import product.orders.uiservice.exception.BackendServiceException;
import product.orders.uiservice.service.UserSessionService;

import java.util.List;
import java.util.UUID;

@Component
public class CheckoutOrderAPIClientImpl implements CheckoutOrderAPIClient {

    private final WebClient webClient;

    private UserSessionService userSessionService;

    @Value("${store.currency}")
    private String currency;


    public CheckoutOrderAPIClientImpl(WebClient orderWebClient, UserSessionService userSessionService) {
        webClient = orderWebClient;
        this.userSessionService = userSessionService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UUID createOrder(Cart cart, CheckoutForm checkoutForm) {
        if (cart.isEmpty()) {
            throw new IllegalStateException("Cannot create order from empty cart");
        }

        CreateOrderRequest request = mapToRequest(cart, checkoutForm);

        CreateOrderResponse createOrderResponse = webClient.post()
                .uri("/orders")
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        response.bodyToMono(String.class)
                                .map(body ->
                                        new BackendServiceException(
                                                "Order request was rejected"
                                        )
                                )
                )
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        response.bodyToMono(String.class)
                                .map(body ->
                                        new BackendServiceException(
                                                "Order service unavailable"
                                        )
                                )
                )
                .bodyToMono(CreateOrderResponse.class)
                .onErrorMap(WebClientRequestException.class,
                        ex -> new BackendServiceException("Order service unavailable", ex))
                .block();

        if (createOrderResponse == null) {
            throw new BackendServiceException("Order service unavailable");
        }
        return createOrderResponse.orderId();
    }


    private CreateOrderRequest mapToRequest(Cart cart, CheckoutForm checkoutForm) {
        List<CreateOrderItemRequest> items =
                cart.items()
                        .stream()
                        .map(this::toItem)
                        .toList();

        return new CreateOrderRequest(
                items,
                userSessionService.getCurrentUserId(),
                checkoutForm.getCustomerEmail(),
                checkoutForm.getCustomerAddress(),
                cart.totalAmountUsdCents(),
                currency
        );
    }

    private CreateOrderItemRequest toItem(CartItem item) {
        return new CreateOrderItemRequest(
                item.productId(),
                item.productName(),
                item.quantity(),
                item.priceUsdCentsSnapshot()
        );
    }

}
