package product.orders.uiservice.product.view;

import java.util.UUID;

/**
 * View model for a product card within a {@link ProductListViewModel}
 */
public record ProductCardViewModel(UUID productId,
                                   String name,
                                   String category,
                                   String priceFormatted) {
}
