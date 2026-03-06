package product.orders.uiservice.cart.view;

import java.util.List;

/**
 * View model for the shopping cart
 */
public record CartViewModel(List<CartItemViewModel> items,
                            String totalFormatted,
                            boolean empty) {
}
