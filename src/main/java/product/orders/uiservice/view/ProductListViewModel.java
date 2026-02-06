package product.orders.uiservice.view;

import java.util.List;
import java.util.UUID;

public record ProductListViewModel(
        List<ProductCardViewModel> products
) {
}