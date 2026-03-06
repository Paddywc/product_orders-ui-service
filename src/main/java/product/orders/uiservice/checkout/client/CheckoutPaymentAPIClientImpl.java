package product.orders.uiservice.checkout.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import product.orders.uiservice.cart.model.Cart;
import product.orders.uiservice.cart.model.CartItem;
import product.orders.uiservice.checkout.dto.CheckoutRequestItem;
import product.orders.uiservice.checkout.dto.CreateCheckoutRequest;
import product.orders.uiservice.exception.BackendServiceException;
import product.orders.uiservice.security.UserPrincipal;

import java.util.List;
import java.util.UUID;

@Component
public class CheckoutPaymentAPIClientImpl implements CheckoutPaymentAPIClient {

    private final WebClient webClient;

    @Value("${store.currency}")
    private String currency;

    public CheckoutPaymentAPIClientImpl(WebClient paymentWebClient) {
        this.webClient = paymentWebClient;
    }


    /**
     * Have the payment service create a checkout session for the given order and return its url
     *
     * @param orderId       the order id
     * @param cart          the cart containing the order items
     * @param customerEmail the customer email
     * @return the stripe checkout session url
     */
    @Override
    public String createCheckoutSession(UUID orderId, Cart cart, String customerEmail) {

        CreateCheckoutRequest request = mapToRequest(orderId, cart, customerEmail);

        return webClient.post()
                .uri("/api/payments/stripe/checkout")
                .bodyValue(request)
                .headers(headers -> headers.setBearerAuth(getCurrentToken()))
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class)
                                .map(body ->
                                        new BackendServiceException(
                                                "Failed to create checkout session"
                                        )
                                )
                )
                .bodyToMono(String.class)
                .onErrorMap(WebClientRequestException.class,
                        ex -> new BackendServiceException("Payment service unavailable", ex))
                .block();
    }

    private CreateCheckoutRequest mapToRequest(UUID orderId, Cart cart, String customerEmail) {
        List<CheckoutRequestItem> items = cart.items()
                .stream()
                .map((CartItem item) -> new CheckoutRequestItem(
                        item.productId(),
                        (long) item.quantity(),
                        item.productName(),
                        item.priceUsdCentsSnapshot()))
                .toList();

        String successURl = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/orders/" + orderId.toString())
                .queryParam("paymentSuccess", "true")
                .toUriString();

        String cancelUrl = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/orders/" + orderId.toString())
                .queryParam("paymentCancelled", "true")
                .toUriString();

        return new CreateCheckoutRequest(orderId, items, customerEmail, currency, successURl, cancelUrl);
    }

    private String getCurrentToken() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new IllegalStateException("User not authenticated");
        }

        return principal.getToken();
    }
}
