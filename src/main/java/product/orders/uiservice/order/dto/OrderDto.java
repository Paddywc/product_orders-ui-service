package product.orders.uiservice.order.dto;

import product.orders.uiservice.order.enums.OrderProgress;
import product.orders.uiservice.order.enums.PaymentStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * DTO returned by the Orders service query API.
 * Used only by the UI service for data transport.
 */
public record OrderDto(UUID orderId,
                       List<OrderItemDto> items,
                       UUID customerId,
                       String customerEmail,
                       String customerAddress,
                       long totalAmountUSDCents,
                       String currency,
                       String status,
                       OrderProgress progress,
                       PaymentStatus paymentStatus,
                       Instant createdAt
                       ) {
}
