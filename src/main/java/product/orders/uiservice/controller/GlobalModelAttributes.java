package product.orders.uiservice.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import product.orders.uiservice.cart.model.Cart;
import product.orders.uiservice.cart.service.CartService;
import product.orders.uiservice.service.UserSessionService;

/**
 * Includes a cart item count and an authentication status in the model for all views.
 */
@ControllerAdvice
public class GlobalModelAttributes {
    private final CartService cartService;
    private final UserSessionService userSessionService;

    public GlobalModelAttributes(CartService cartService, UserSessionService userSessionService) {
        this.cartService = cartService;
        this.userSessionService = userSessionService;
    }

    @ModelAttribute("cartItemCount")
    public int cartItemCount() {
        Cart cart = cartService.getCart();
        return cart.totalItemCount();
    }

    @ModelAttribute("isAuthenticated")
    public boolean isAuthenticated() {
        return userSessionService.isAuthenticated();
    }
}
