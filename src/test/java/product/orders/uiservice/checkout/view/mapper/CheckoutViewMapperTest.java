package product.orders.uiservice.checkout.view.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import product.orders.uiservice.cart.model.Cart;
import product.orders.uiservice.cart.model.CartItem;
import product.orders.uiservice.checkout.view.CheckoutItemViewModel;
import product.orders.uiservice.checkout.view.CheckoutViewModel;
import product.orders.uiservice.util.MoneyFormatter;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;


class CheckoutViewMapperTest {

    private CheckoutViewMapper checkoutViewMapper;

    @Mock
    private MoneyFormatter moneyFormatter;

    @BeforeEach
    void beforeEach() {
        if (moneyFormatter == null) {
            moneyFormatter = mock(MoneyFormatter.class);
        }
        if (checkoutViewMapper == null) {
            checkoutViewMapper = new CheckoutViewMapper(moneyFormatter);
        }
    }

    @Test
    void testToCheckoutView_CartWithItems_MapsItemFieldsAndTotal() {
        // Arrange
        Cart cart = new Cart();
        CartItem firstItem = new CartItem(UUID.randomUUID(), "Coffee", 150, 2);
        CartItem secondItem = new CartItem(UUID.randomUUID(), "Mug", 500, 1);
        cart.addItem(firstItem);
        cart.addItem(secondItem);

        when(moneyFormatter.formatUsd(150)).thenReturn("$1.50");
        when(moneyFormatter.formatUsd(300)).thenReturn("$3.00");
        when(moneyFormatter.formatUsd(500)).thenReturn("$5.00");
        when(moneyFormatter.formatUsd(800)).thenReturn("$8.00");

        // Act
        CheckoutViewModel viewModel = checkoutViewMapper.toCheckoutView(cart);

        // Assert
        List<CheckoutItemViewModel> items = viewModel.items();
        assertEquals(2, items.size());
        assertEquals("Coffee", items.get(0).productName());
        assertEquals(2, items.get(0).quantity());
        assertEquals("$1.50", items.get(0).perUnitPriceFormatted());
        assertEquals("$3.00", items.get(0).totalPriceFormatted());
        assertEquals("Mug", items.get(1).productName());
        assertEquals(1, items.get(1).quantity());
        assertEquals("$5.00", items.get(1).perUnitPriceFormatted());
        assertEquals("$5.00", items.get(1).totalPriceFormatted());
        assertEquals("$8.00", viewModel.totalPriceFormatted());


        verify(moneyFormatter).formatUsd(150);
        verify(moneyFormatter).formatUsd(300);
        // One unit, so called for the base price and total
        verify(moneyFormatter, times(2)).formatUsd(500);
        verify(moneyFormatter).formatUsd(800);

        verifyNoMoreInteractions(moneyFormatter);
    }

    @Test
    void testToCheckoutView_EmptyCart_ReturnsEmptyItemsAndZeroTotal() {
        // Arrange
        Cart cart = new Cart();
        when(moneyFormatter.formatUsd(0)).thenReturn("$0.00");

        // Cct
        CheckoutViewModel viewModel = checkoutViewMapper.toCheckoutView(cart);

        // Assert
        assertTrue(viewModel.items().isEmpty());
        assertEquals("$0.00", viewModel.totalPriceFormatted());

        verify(moneyFormatter).formatUsd(0);
        verifyNoMoreInteractions(moneyFormatter);
    }
}
