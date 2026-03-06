package product.orders.uiservice.product.view;

import java.util.UUID;

/**
 * View model for a product detail page
 */
public record ProductDetailViewModel(UUID productId,
                                     String name,
                                     String description,
                                     String priceFormatted,
                                     String category,
                                     boolean active) {
}
