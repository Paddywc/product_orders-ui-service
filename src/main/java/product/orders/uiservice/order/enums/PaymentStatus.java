package product.orders.uiservice.order.enums;

/**
 * Status of the payment for an order. Duplicated from the PaymentStatus enum in the order service and returned in
 * the {@link product.orders.uiservice.order.dto.OrderDto}
 */
public enum PaymentStatus {
    PENDING,
    COMPLETED,
    FAILED,
    REFUNDED
}
