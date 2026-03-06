package product.orders.uiservice.order.enums;

/**
 * Summary of the progress of an order. Duplicated from the OrderProgress enum in the order service and returned in
 * the {@link product.orders.uiservice.order.dto.OrderDto}
 */
public enum OrderProgress {
    AWAITING_PAYMENT_AND_INVENTORY_RESERVATION,
    AWAITING_INVENTORY_RESERVATION,
    AWAITING_PAYMENT,
    CONFIRMED,
    CANCELLED_AWAITING_PAYMENT_REFUND,
    CANCELLED_INVENTORY_RESERVATION_FAILED,
    CANCELLED_PAYMENT_FAILED
}
