package product.orders.uiservice.view;

import java.util.UUID;

public record ProductDetailViewModel(UUID productId,
                                     String name,
                                     String description,
                                     String priceFormatted,
                                     String category,
                                     boolean active) {
}
