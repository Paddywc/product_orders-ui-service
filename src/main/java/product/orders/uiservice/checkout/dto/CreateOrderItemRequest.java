package product.orders.uiservice.checkout.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.UUID;

/**
 * Request for creating an order item that is sent to the order service
 */
public record CreateOrderItemRequest(@NotNull UUID productId,
                                     @NotNull String productName,
                                     @Positive int quantity,
                                     @PositiveOrZero long unitPriceCents) {
}
