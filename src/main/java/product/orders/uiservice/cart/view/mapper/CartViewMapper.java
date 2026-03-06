package product.orders.uiservice.cart.view.mapper;

import org.springframework.stereotype.Component;
import product.orders.uiservice.cart.model.Cart;
import product.orders.uiservice.cart.model.CartItem;
import product.orders.uiservice.cart.view.CartItemViewModel;
import product.orders.uiservice.cart.view.CartViewModel;
import product.orders.uiservice.util.MoneyFormatter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Maps a {@link Cart} to a {@link CartViewModel}
 */
@Component
public class CartViewMapper {

    private final MoneyFormatter moneyFormatter;

    public CartViewMapper(MoneyFormatter moneyFormatter) {
        this.moneyFormatter = moneyFormatter;
    }

    public CartViewModel toView(Cart cart) {
        List<CartItemViewModel> items = cart.items().stream()
                .map(this::toItemView)
                .collect(Collectors.toList());

        return new CartViewModel(
                items,
                moneyFormatter.formatUsd(cart.totalAmountUsdCents()),
                cart.isEmpty()
        );
    }

    private CartItemViewModel toItemView(CartItem item) {
        return new CartItemViewModel(
                item.productId(),
                item.productName(),
                moneyFormatter.formatUsd(item.priceUsdCentsSnapshot()),
                item.quantity(),
                moneyFormatter.formatUsd(item.totalPrice())
        );
    }
}
