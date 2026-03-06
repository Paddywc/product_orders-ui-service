package product.orders.uiservice.checkout.view.mapper;

import org.springframework.stereotype.Component;
import product.orders.uiservice.cart.model.Cart;
import product.orders.uiservice.checkout.view.CheckoutItemViewModel;
import product.orders.uiservice.checkout.view.CheckoutViewModel;
import product.orders.uiservice.util.MoneyFormatter;

import java.util.List;

/**
 * Maps a cart to a checkout view model
 */
@Component
public class CheckoutViewMapper {
    private final MoneyFormatter moneyFormatter;

    public CheckoutViewMapper(MoneyFormatter moneyFormatter) {
        this.moneyFormatter = moneyFormatter;
    }

    public CheckoutViewModel toCheckoutView(Cart cart) {
        List<CheckoutItemViewModel> items =
                cart.items().stream()
                        .map(item -> new CheckoutItemViewModel(
                                item.productName(),
                                item.quantity(),
                                moneyFormatter.formatUsd(item.priceUsdCentsSnapshot()),
                                moneyFormatter.formatUsd(item.totalPrice())
                        ))
                        .toList();

        return new CheckoutViewModel(
                items,
                moneyFormatter.formatUsd(cart.totalAmountUsdCents())
        );
    }
}
