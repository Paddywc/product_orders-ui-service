package product.orders.uiservice.order.view;

import java.util.List;
import java.util.UUID;

/**
 * View model for an order
 */
public record OrderViewModel(UUID orderId,
                             String orderDateFormatted,
                             List<OrderItemViewModel> orderItems,
                             String totalPriceFormatted,
                             String customerEmail,
                             String customerAddress,
                             String currency,
                             String status,
                             String progress,
                             String paymentStatus) {
}
