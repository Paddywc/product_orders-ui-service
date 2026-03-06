package product.orders.uiservice.exception;

import java.util.UUID;

/**
 * Exception thrown when an order is not found by its ID.
 */
public class OrderNotFoundException extends BackendServiceException{
    public OrderNotFoundException(UUID orderId) {
        super(createMessage(orderId));
    }


    private static String createMessage(UUID orderId){
        return "Order not found: " + orderId;
    }
}
