package product.orders.uiservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import product.orders.uiservice.cart.model.Cart;
import product.orders.uiservice.cart.service.CartService;
import product.orders.uiservice.cart.view.CartViewModel;
import product.orders.uiservice.cart.view.mapper.CartViewMapper;

import java.util.UUID;

/**
 * Controller for cart related endpoints
 */
@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final CartViewMapper cartViewMapper;

    public CartController(CartService cartService, CartViewMapper cartViewMapper) {
        this.cartService = cartService;
        this.cartViewMapper = cartViewMapper;
    }

    @GetMapping
    public String viewCart(Model model) {
        Cart cart = cartService.getCart();
        CartViewModel cartView = cartViewMapper.toView(cart);
        model.addAttribute("cart", cartView);
        return "cart/view";
    }

    @PostMapping("/add")
    public String addItem(@RequestParam UUID productId,
                          @RequestParam(defaultValue = "1") int quantity,
                          RedirectAttributes redirectAttributes) {
        cartService.addItem(productId, quantity);

        redirectAttributes.addFlashAttribute("cartAdded", true);

        return "redirect:/products";
    }

    @PostMapping("/remove")
    public String removeItem(@RequestParam UUID productId) {
        cartService.removeItem(productId);
        return "redirect:/cart";
    }
}
