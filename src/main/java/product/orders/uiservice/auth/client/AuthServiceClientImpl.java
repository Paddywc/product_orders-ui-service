package product.orders.uiservice.auth.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import product.orders.uiservice.auth.dto.LoginResponse;
import product.orders.uiservice.auth.dto.LoginRequest;
import product.orders.uiservice.auth.dto.RegisterUserRequest;
import product.orders.uiservice.exception.BackendServiceException;
import product.orders.uiservice.exception.EmailAlreadyExistsException;
import product.orders.uiservice.exception.LoginFailedException;
import product.orders.uiservice.exception.RegistrationFailedException;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class AuthServiceClientImpl implements AuthServiceClient {

    private final WebClient webClient;

    public AuthServiceClientImpl(WebClient authWebClient) {
        this.webClient = authWebClient;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LoginResponse login(String email, String password) {
        return webClient.post()
                .uri("/api/auth/login")
                .bodyValue(new LoginRequest(email, password))
                .exchangeToMono(response ->{
                    if (response.statusCode().is4xxClientError()) {
                        return Mono.error(new LoginFailedException());
                    }
                   if(response.statusCode().is5xxServerError()){
                       return Mono.error(new BackendServiceException("Auth service unavailable"));
                   }
                    return response.bodyToMono(LoginResponse.class);
                })
                .onErrorMap(WebClientRequestException.class,
                        ex -> new BackendServiceException("Auth service unavailable", ex))
                .block();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UUID register(String email, String rawPassword) {
        return webClient.post()
                .uri("/api/auth/register")
                .bodyValue(new RegisterUserRequest(email, rawPassword))
                .exchangeToMono(response -> {

                    if (response.statusCode().value() == 409) {
                        return Mono.error(new EmailAlreadyExistsException());
                    }

                    if (response.statusCode().is4xxClientError()) {
                        return Mono.error(new RegistrationFailedException());
                    }

                    if (response.statusCode().is5xxServerError()) {
                        return Mono.error(
                                new BackendServiceException("Auth service unavailable")
                        );
                    }

                    return response.bodyToMono(UUID.class);
                })
                .onErrorMap(WebClientRequestException.class,
                        ex -> new BackendServiceException("Auth service unavailable", ex))
                .block();
    }
}
