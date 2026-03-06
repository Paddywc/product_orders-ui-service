package product.orders.uiservice.checkout.dto;

import java.util.List;
import java.util.UUID;

/**
 * Request for creating a checkout session that is sent to the payment service
 */
public record CreateCheckoutRequest(UUID orderId,
                                    List<CheckoutRequestItem> items,
                                    String userEmail,
                                    String currency,
                                    String successUrl,
                                    String cancelUrl) {
}
