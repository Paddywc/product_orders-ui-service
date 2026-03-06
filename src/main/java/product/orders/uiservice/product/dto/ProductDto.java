package product.orders.uiservice.product.dto;

import java.util.UUID;

/**
 * DTO returned by the Products service query API.
 * Used only by the UI service for data transport.
 */
public record ProductDto(UUID productId,
                         String name,
                         String description,
                         long priceUSDCents,
                         String category,
                         String status) {
}
