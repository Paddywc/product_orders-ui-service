package product.orders.uiservice.product.view;

import java.util.List;

/**
 * View model for a list of product cards
 */
public record ProductListViewModel(
        List<ProductCardViewModel> products
) {
}