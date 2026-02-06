package product.orders.uiservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "services")
public class ServiceEndpointProperties {

    private Service productService;
    private Service orderService;

    public static class Service {
        private String baseUrl;

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }
    }

    public Service getProductService() {
        return productService;
    }

    public void setProductService(Service productService) {
        this.productService = productService;
    }

    public Service getOrderService() {
        return orderService;
    }

    public void setOrderService(Service orderService) {
        this.orderService = orderService;
    }
}