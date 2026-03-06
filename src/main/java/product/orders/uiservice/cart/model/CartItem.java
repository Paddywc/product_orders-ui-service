package product.orders.uiservice.cart.model;

import java.util.UUID;

/**
 * Represents an item in the user's cart.
 * @param productId the unique identifier of the product
 * @param productName the name of the product (at the time of adding to the cart)
 * @param priceUsdCentsSnapshot the price of the product in USD cents at the time of adding to the cart
 * @param quantity the quantity of the product in the cart
 */
public record CartItem(UUID productId,
                       String productName,
                       long priceUsdCentsSnapshot,
                       int quantity) {

    public long totalPrice() {
        return priceUsdCentsSnapshot * quantity;
    }
}
