package product.orders.uiservice.cart.model;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.UUID;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Cart} focusing on the {@link Cart#addItem(CartItem)} method behavior.
 */
class CartTest {

    /**
     * Verifies {@link Cart#addItem(CartItem)} stores a new item in an empty cart.
     */
    @Test
    void testAddItem_EmptyCart_AddsItem() {
        // Arrange
        Cart cart = new Cart();
        UUID productId = UUID.randomUUID();
        CartItem item = new CartItem(productId, "Coffee", 499, 2);

        // Act
        cart.addItem(item);

        // Assert
        CartItem stored = findItem(cart, productId);
        assertNotNull(stored);
        assertEquals(1, cart.items().size());
        assertEquals(productId, stored.productId());
        assertEquals("Coffee", stored.productName());
        assertEquals(499, stored.priceUsdCentsSnapshot());
        assertEquals(2, stored.quantity());
    }

    /**
     * Verifies {@link Cart#addItem(CartItem)} merges quantity for an existing product id.
     */
    @Test
    void testAddItem_ExistingProductId_MergesQuantityAndKeepsSnapshot() {
        // Arrange
        Cart cart = new Cart();
        UUID productId = UUID.randomUUID();
        CartItem original = new CartItem(productId, "Tea", 350, 1);
        CartItem incoming = new CartItem(productId, "Tea Updated", 999, 3);
        cart.addItem(original);

        // Act
        cart.addItem(incoming);

        // Assert
        CartItem stored = findItem(cart, productId);
        assertNotNull(stored);
        assertEquals(1, cart.items().size());
        assertEquals(productId, stored.productId());
        assertEquals("Tea", stored.productName());
        assertEquals(350, stored.priceUsdCentsSnapshot());
        assertEquals(4, stored.quantity());
    }

    /**
     * Verifies {@link Cart#addItem(CartItem)} keeps distinct items for different product ids.
     */
    @Test
    void testAddItem_DifferentProductIds_AddsMultipleItems() {
        // Arrange
        Cart cart = new Cart();
        UUID firstProductId = UUID.randomUUID();
        UUID secondProductId = UUID.randomUUID();
        CartItem first = new CartItem(firstProductId, "Soda", 199, 1);
        CartItem second = new CartItem(secondProductId, "Chips", 299, 2);

        // Act
        cart.addItem(first);
        cart.addItem(second);

        // Assert
        assertEquals(2, cart.items().size());
        assertNotNull(findItem(cart, firstProductId));
        assertNotNull(findItem(cart, secondProductId));
    }

    /**
     * Verifies {@link Cart#addItem(CartItem)} accumulates quantity across multiple adds.
     */
    @Test
    void testAddItem_SameProductAddedTwice_AccumulatesQuantity() {
        // Arrange
        Cart cart = new Cart();
        UUID productId = UUID.randomUUID();
        cart.addItem(new CartItem(productId, "Cookie", 250, 1));

        // Act
        cart.addItem(new CartItem(productId, "Cookie", 250, 4));

        // Assert
        CartItem stored = findItem(cart, productId);
        assertNotNull(stored);
        assertEquals(1, cart.items().size());
        assertEquals(5, stored.quantity());
    }

    /**
     * Verifies {@link Cart#addItem(CartItem)} keeps each item's quantity for distinct product ids.
     */
    @Test
    void testAddItem_DifferentProductIds_PreservesEachQuantity() {
        // Arrange
        Cart cart = new Cart();
        UUID coffeeId = UUID.randomUUID();
        UUID muffinId = UUID.randomUUID();
        cart.addItem(new CartItem(coffeeId, "Coffee", 399, 2));

        // Act
        cart.addItem(new CartItem(muffinId, "Muffin", 299, 5));

        // Assert
        CartItem coffee = findItem(cart, coffeeId);
        CartItem muffin = findItem(cart, muffinId);
        assertNotNull(coffee);
        assertNotNull(muffin);
        assertEquals(2, coffee.quantity());
        assertEquals(5, muffin.quantity());
    }

    @Test
    void testRemoveItem_ExistingProduct_RemovesIt() {
        // Arrange
        Cart cart = new Cart();
        UUID productId = UUID.randomUUID();

        cart.addItem(new CartItem(
                productId,
                "Test Product",
                100L,
                1
        ));

        // Act
        cart.removeItem(productId);

        // Assert
        assertThat(cart.isEmpty()).isTrue();
    }

    @Test
    void testTotalAmountUsdCents_MultipleItems_CalculatesCorrectly() {
        // Arrange
        Cart cart = new Cart();

        // Act
        cart.addItem(new CartItem(
                UUID.randomUUID(),
                "Product A",
                150L,
                2
        ));

        cart.addItem(new CartItem(
                UUID.randomUUID(),
                "Product B",
                200L,
                1
        ));

        // Assert
        assertThat(cart.totalAmountUsdCents()).isEqualTo(500L);
    }

    @Test
    void testTotalItemCount_MultipleItems_ReturnsCorrectSum() {
        // Arrange
        Cart cart = new Cart();

        // Act
        cart.addItem(new CartItem(
                UUID.randomUUID(),
                "Product A",
                100L,
                3
        ));

        cart.addItem(new CartItem(
                UUID.randomUUID(),
                "Product B",
                100L,
                2
        ));

        // Assert
        assertThat(cart.totalItemCount()).isEqualTo(5);
    }

    @Test
    void testIsEmpty_NewCart_ReturnsTrue() {
        // Arrange
        Cart cart = new Cart();

        // Assert
        assertThat(cart.isEmpty()).isTrue();
        assertThat(cart.totalItemCount()).isZero();
        assertThat(cart.totalAmountUsdCents()).isZero();
    }

    @Test
    void testIsEmpty_CartWithItems_ReturnsFalse() {
        // Arrange
        Cart cart = new Cart();
        // Act
        cart.addItem(new CartItem(UUID.randomUUID(), "Test Product", 100L, 1));
        // Assert
        assertThat(cart.isEmpty()).isFalse();
    }

    private CartItem findItem(Cart cart, UUID productId) {
        return cart.items().stream()
                .filter(item -> item.productId().equals(productId))
                .findFirst()
                .orElse(null);
    }
}
