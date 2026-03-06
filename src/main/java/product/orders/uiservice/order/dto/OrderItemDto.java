package product.orders.uiservice.order.dto;

import java.util.UUID;

/**
 * DTO returned by the Orders service query API.
 * Used only by the UI service for data transport.
 */
public record OrderItemDto(UUID productId, String productName, int quantity, long unitPriceUSDCents) {
}
