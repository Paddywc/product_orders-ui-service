package product.orders.uiservice.checkout.view;

import java.util.List;

/**
 * View model for the checkout page
 * @param items the items being checked out
 * @param totalPriceFormatted String representation of the total price
 */
public record CheckoutViewModel(List<CheckoutItemViewModel> items, String totalPriceFormatted) {
}
