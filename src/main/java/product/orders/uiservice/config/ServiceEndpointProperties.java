package product.orders.uiservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * URL endpoints for microservices.
 */
@ConfigurationProperties(prefix = "services")
public class ServiceEndpointProperties {

    private Service productService;
    private Service orderService;
    private Service authService;
    private Service paymentService;

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

    public Service getAuthService() {
        return authService;
    }

    public void setAuthService(Service authService) {
        this.authService = authService;
    }

    public Service getPaymentService() {
        return paymentService;
    }

    public void setPaymentService(Service paymentService) {
        this.paymentService = paymentService;
    }
}
