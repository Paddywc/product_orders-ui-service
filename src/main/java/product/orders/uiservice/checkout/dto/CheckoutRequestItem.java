package product.orders.uiservice.checkout.dto;

import java.util.UUID;

/**
 * An item that should be checked out as part of a {@link CreateCheckoutRequest}
 */
public record CheckoutRequestItem(UUID productId, Long quantity, String name, Long priceInCents) {
}
