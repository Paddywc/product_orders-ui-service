package product.orders.uiservice.support;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.function.Function;

public final class WebClientTestSupport {

    private WebClientTestSupport() {
    }

    public static WebClient webClientWithResponse(ClientResponse response) {
        ExchangeFunction exchangeFunction = request -> Mono.just(response);
        return WebClient.builder()
                .exchangeFunction(exchangeFunction)
                .build();
    }

    public static WebClient webClientWithFailure(Throwable throwable) {
        ExchangeFunction exchangeFunction = request -> Mono.error(throwable);
        return WebClient.builder()
                .exchangeFunction(exchangeFunction)
                .build();
    }

    public static <T> T webClientWithResponse(ClientResponse response, Function<WebClient, T> factory
    ) {
        return factory.apply(webClientWithResponse(response));
    }

    public static <T> T webClientWithFailure(Throwable throwable, Function<WebClient, T> factory) {
        return factory.apply(webClientWithFailure(throwable));
    }


    public static ClientResponse jsonResponse(HttpStatus status, String json) {
        return ClientResponse.create(status)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(json)
                .build();
    }
}
