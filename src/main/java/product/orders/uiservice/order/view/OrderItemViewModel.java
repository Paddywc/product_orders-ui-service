package product.orders.uiservice.order.view;

import java.util.UUID;

/**
 * An item within an {@link OrderViewModel}
 */
public record OrderItemViewModel(UUID productId, String productName, int quantity, String unitPriceFormatted) {
}
