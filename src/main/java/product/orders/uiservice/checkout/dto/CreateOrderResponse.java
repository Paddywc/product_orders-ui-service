package product.orders.uiservice.checkout.dto;

import java.util.UUID;

/**
 * Response for creating an order that is sent to the UI service from the order service
 */
public record CreateOrderResponse(
        UUID orderId,
        UUID customerId,
        String customerEmail,
        String customerAddress,

        long totalAmountCents,
        String currency
) {


}
