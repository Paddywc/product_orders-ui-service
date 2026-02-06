package product.orders.uiservice.exception;

import java.util.UUID;

public class ProductNotFoundException  extends BackendServiceException{
    public ProductNotFoundException(UUID productId) {
        super(createMessage(productId));
    }

    public ProductNotFoundException(UUID productId, Throwable cause) {
        super(createMessage(productId), cause);
    }

    private static String createMessage(UUID productId){
        return "Product not found: " + productId;
    }
}
