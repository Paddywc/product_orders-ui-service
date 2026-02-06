package product.orders.uiservice.exception;

/**
 * Thrown when a backend service (product, order, inventory, payment)
 * cannot be reached or returns an error the UI cannot recover from.
 * <p>
 * This exception is UI-specific and must never leak outside this service.
 */
public class BackendServiceException extends RuntimeException {
    public BackendServiceException(String message) {
        super(message);
    }

    public BackendServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
