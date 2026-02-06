package product.orders.uiservice.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;


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
                .build();
    }


}
