package product.orders.uiservice.view;

import java.util.UUID;

public record ProductCardViewModel(UUID productId,
                                   String name,
                                   String priceFormatted) {
}
