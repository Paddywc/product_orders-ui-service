package product.orders.uiservice.exception;

import java.util.UUID;

/**
 * Error thrown when a product is not found by its id
 */
public class ProductNotFoundException  extends BackendServiceException{
    public ProductNotFoundException(UUID productId) {
        super(createMessage(productId));
    }


    private static String createMessage(UUID productId){
        return "Product not found: " + productId;
    }
}
