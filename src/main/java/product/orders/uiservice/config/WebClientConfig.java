package product.orders.uiservice.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;
import product.orders.uiservice.security.UserPrincipal;

/**
 * Configuration for WebClient instances used to communicate with microservices.
 */
@Configuration
@EnableConfigurationProperties(ServiceEndpointProperties.class)
public class WebClientConfig {

    @Bean
    public WebClient productWebClient(ServiceEndpointProperties properties) {
        return WebClient.builder()
                .baseUrl(properties.getProductService().getBaseUrl())
                .build();
    }

    @Bean
    public WebClient orderWebClient(ServiceEndpointProperties properties) {
        return WebClient.builder()
                .baseUrl(properties.getOrderService().getBaseUrl())
                .filter((request, next) -> {

                    // Send authentication token in header if authenticated. This is required by all order services
                    Authentication auth = SecurityContextHolder
                            .getContext()
                            .getAuthentication();

                    if (auth != null && auth.getPrincipal() instanceof UserPrincipal principal) {

                        ClientRequest newRequest = ClientRequest
                                .from(request)
                                .headers(headers ->
                                        headers.setBearerAuth(principal.getToken())
                                )
                                .build();

                        return next.exchange(newRequest);
                    }

                    return next.exchange(request);
                })
                .build();
    }


    @Bean
    public WebClient authWebClient(ServiceEndpointProperties properties) {
        return WebClient.builder()
                .baseUrl(properties.getAuthService().getBaseUrl())
                .build();
    }

    @Bean
    public WebClient paymentWebClient(ServiceEndpointProperties properties) {
        // Token is only required by some payment services and is set in the header within those requests
        return WebClient.builder()
                .baseUrl(properties.getPaymentService().getBaseUrl())
                .build();
    }

}
