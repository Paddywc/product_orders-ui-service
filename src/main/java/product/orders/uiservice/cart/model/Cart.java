package product.orders.uiservice.cart.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a user's shopping cart.
 */
public class Cart implements Serializable {

    private final Map<UUID, CartItem> items = new LinkedHashMap<>();

    /**
     * Add an item to the cart, merging with existing item (sum the quantities) if it already exist in the cart
     * @param item the item to add
     */
    public void addItem(CartItem item) {
        items.merge(
                item.productId(),
                item,
                (existing, incoming) ->
                        new CartItem(
                                existing.productId(),
                                existing.productName(),
                                existing.priceUsdCentsSnapshot(),
                                existing.quantity() + incoming.quantity()
                        )
        );
    }

    /**
     * Completely remove an item from the cart
     * @param productId
     */
    public void removeItem(UUID productId) {
        items.remove(productId);
    }

    /**
     * Get all items in the cart
     * @return a collection of cart items
     */
    public Collection<CartItem> items() {
        return items.values();
    }

    /**
     * Get the total amount in USD cents of all items in the cart
     * @return the total amount in USD cents
     */
    public long totalAmountUsdCents() {
        return items.values().stream()
                .mapToLong(CartItem::totalPrice)
                .sum();
    }

    /**
     *
     * @return the sum of each item X its quantity
     */
    public int totalItemCount(){
        return items.values().stream()
                .mapToInt(CartItem::quantity)
                .sum();
    }

    /**
     * Checks if the cart is empty
     * @return true if the cart is empty, false otherwise
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }
}
