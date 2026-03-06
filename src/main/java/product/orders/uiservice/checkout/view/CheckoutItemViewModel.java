package product.orders.uiservice.checkout.view;

/**
 * View model for a single item in the checkout page, part of the {@link CheckoutViewModel}
 * @param productName the name of the product
 * @param quantity the quantity of the product being checked out
 * @param perUnitPriceFormatted String representation of the price per unit
 * @param totalPriceFormatted String representation of the total price for this item
 */
public record CheckoutItemViewModel(String productName,
                                    int quantity,
                                    String perUnitPriceFormatted,
                                    String totalPriceFormatted) {
}
