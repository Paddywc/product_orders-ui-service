package product.orders.uiservice.cart.view;

import java.util.UUID;

/**
 * View model for a single item in the shopping cart. A list within the {@link CartViewModel}
 */
public record CartItemViewModel(UUID productId,
                                String productName,
                                String unitPriceFormatted,
                                int quantity,
                                String totalPriceFormatted) {
}
