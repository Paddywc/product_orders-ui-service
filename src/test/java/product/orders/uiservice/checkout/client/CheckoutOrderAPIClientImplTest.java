package product.orders.uiservice.checkout.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import product.orders.uiservice.cart.model.Cart;
import product.orders.uiservice.cart.model.CartItem;
import product.orders.uiservice.checkout.dto.CreateOrderRequest;
import product.orders.uiservice.checkout.dto.CreateOrderResponse;
import product.orders.uiservice.checkout.model.CheckoutForm;
import product.orders.uiservice.exception.BackendServiceException;
import product.orders.uiservice.service.UserSessionService;
import product.orders.uiservice.support.WebClientTestSupport;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CheckoutOrderAPIClientImplTest {

    private WebClient webClient;
    private UserSessionService userSessionService;

    private CheckoutOrderAPIClientImpl client;

    // WebClient mocks
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;
    private WebClient.RequestBodySpec requestBodySpec;
    private WebClient.RequestHeadersSpec<?> requestHeadersSpec;
    private WebClient.ResponseSpec responseSpec;

    @BeforeEach
    void setUp() {
        webClient = mock(WebClient.class);
        userSessionService = mock(UserSessionService.class);

        requestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
        requestBodySpec = mock(WebClient.RequestBodySpec.class);
        requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        responseSpec = mock(WebClient.ResponseSpec.class);

        client = new CheckoutOrderAPIClientImpl(webClient, userSessionService);
        ReflectionTestUtils.setField(client, "currency", "USD");

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/orders")).thenReturn(requestBodySpec);
        doReturn(requestHeadersSpec)
                .when(requestBodySpec)
                .bodyValue(any());
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
    }


    @Test
    void testCreateOrder_EmptyCart_ThrowsIllegalStateException() {
        Cart emptyCart = mock(Cart.class);
        when(emptyCart.isEmpty()).thenReturn(true);

        assertThatThrownBy(() ->
                client.createOrder(emptyCart, mock(CheckoutForm.class))
        ).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("empty cart");
    }

    @Test
    void testCreateOrder_ValidCart_ReturnsOrderId() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Cart cart = mockCart();
        CheckoutForm form = mockCheckoutForm();

        when(userSessionService.getCurrentUserId()).thenReturn(userId);
        when(responseSpec.bodyToMono(CreateOrderResponse.class))
                .thenReturn(Mono.just(new CreateOrderResponse(orderId, null, null, null, 0L, null)));

        // Act
        UUID result = client.createOrder(cart, form);

        // Assert
        assertThat(result).isEqualTo(orderId);
    }


    @Test
    void testCreateOrder_4xxResponse_ThrowsBackendServiceException() {
        // Arrange
        CheckoutOrderAPIClientImpl realClient = clientWithResponse(HttpStatus.BAD_REQUEST);

        Cart cart = mockCart();

        // Act + Assert
        assertThatThrownBy(() -> realClient.createOrder(cart, mockCheckoutForm()))
                .isInstanceOf(BackendServiceException.class)
                .hasMessageContaining("Order request was rejected");
    }

    @Test
    void testCreateOrder_5xxResponse_ThrowsBackendServiceException() {
        // Arrange
        Cart cart = mockCart();
        CheckoutOrderAPIClientImpl realClient =
                clientWithResponse(HttpStatus.INTERNAL_SERVER_ERROR);

        // Act + Assert
        assertThatThrownBy(() ->
                realClient.createOrder(cart, mockCheckoutForm())
        ).isInstanceOf(BackendServiceException.class)
                .hasMessageContaining("Order service unavailable");
    }


    @Test
    void testCreateOrder_NetworkFailure_ThrowsBackendServiceException() {
        Cart cart = mockCart();

        WebClientRequestException exception = Mockito.mock(WebClientRequestException.class);
        when(responseSpec.bodyToMono(CreateOrderResponse.class))
                .thenReturn(Mono.error(exception));

        assertThatThrownBy(() ->
                client.createOrder(cart, mockCheckoutForm())
        ).isInstanceOf(BackendServiceException.class);
    }


    @Test
    void testCreateOrder_ValidCart_MapsRequestCorrectly() {
        UUID userId = UUID.randomUUID();
        when(userSessionService.getCurrentUserId()).thenReturn(userId);

        Cart cart = mockCart();
        CheckoutForm form = mockCheckoutForm();

        CreateOrderResponse response = mock(CreateOrderResponse.class);
        when(responseSpec.bodyToMono(CreateOrderResponse.class))
                .thenReturn(Mono.just(response));

        ArgumentCaptor<CreateOrderRequest> captor =
                ArgumentCaptor.forClass(CreateOrderRequest.class);

        client.createOrder(cart, form);

        verify(requestBodySpec).bodyValue(captor.capture());

        CreateOrderRequest request = captor.getValue();

        assertThat(request.currency()).isEqualTo("USD");
        assertThat(request.customerId()).isEqualTo(userId);
        assertThat(request.items()).hasSize(1);
        assertThat(request.items().get(0).productName()).isEqualTo("Test Product");
    }

    // ----------------------------------------------------
    // Helpers
    // ----------------------------------------------------

    private Cart mockCart() {
        Cart cart = mock(Cart.class);
        when(cart.isEmpty()).thenReturn(false);
        when(cart.totalAmountUsdCents()).thenReturn(2000L);

        CartItem item = new CartItem(
                UUID.randomUUID(),
                "Test Product",
                2,
                1000
        );

        when(cart.items()).thenReturn(List.of(item));
        return cart;
    }

    private CheckoutForm mockCheckoutForm() {
        CheckoutForm form = mock(CheckoutForm.class);
        when(form.getCustomerEmail()).thenReturn("test@example.com");
        when(form.getCustomerAddress()).thenReturn("123 Test Street");
        return form;
    }

    private CheckoutOrderAPIClientImpl clientWithResponse(HttpStatus status) {
        ClientResponse response = ClientResponse.create(status)
                .header("Content-Type", "application/json")
                .body("{}")
                .build();

        WebClient localWebClient = WebClientTestSupport.webClientWithResponse(response);

        CheckoutOrderAPIClientImpl realClient =
                new CheckoutOrderAPIClientImpl(localWebClient, userSessionService);
        ReflectionTestUtils.setField(realClient, "currency", "USD");
        return realClient;
    }
}
