package product.orders.uiservice.controller;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import product.orders.uiservice.exception.BackendServiceException;
import product.orders.uiservice.exception.OrderNotFoundException;
import product.orders.uiservice.exception.ProductNotFoundException;

import java.util.UUID;

/**
 * Test controller for throwing various exceptions for testing purposes.
 */
@Controller
@RequestMapping("/test")
@Profile("test")
class TestExceptionThrowingController {

    @GetMapping("/order-not-found")
    public String orderNotFound() {
        throw new OrderNotFoundException(UUID.randomUUID());
    }

    @GetMapping("/product-not-found")
    public String productNotFound() {
        throw new ProductNotFoundException(UUID.randomUUID());
    }

    @GetMapping("/backend-error")
    public String backendError() {
        throw new BackendServiceException("Service down");
    }

    @GetMapping("/illegal-arg")
    public String illegalArg() {
        throw new IllegalArgumentException("Bad input");
    }

    @GetMapping("/generic")
    public String generic() {
        throw new RuntimeException("Boom");
    }
}
