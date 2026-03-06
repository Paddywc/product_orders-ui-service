package product.orders.uiservice.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import product.orders.uiservice.cart.model.Cart;
import product.orders.uiservice.cart.service.CartService;
import product.orders.uiservice.checkout.model.CheckoutForm;
import product.orders.uiservice.checkout.service.CheckoutService;
import product.orders.uiservice.checkout.view.mapper.CheckoutViewMapper;
import product.orders.uiservice.exception.BackendServiceException;

import java.util.UUID;

/**
 * Controller for checkout related endpoints. Handles checkout form submission and redirects to payment service.
 */
@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    private final CartService cartService;
    private final CheckoutService checkoutService;

    private final CheckoutViewMapper viewMapper;

    public CheckoutController(CartService cartService, CheckoutService checkoutService, CheckoutViewMapper viewMapper) {
        this.cartService = cartService;
        this.checkoutService = checkoutService;
        this.viewMapper = viewMapper;
    }

    @GetMapping
    public String reviewCheckout(Model model) {
        Cart cart = cartService.getCart();

        if (cart.isEmpty()) {
            return "redirect:/products";
        }

        model.addAttribute("cart", viewMapper.toCheckoutView(cart));
        model.addAttribute("checkoutForm", new CheckoutForm());

        return "checkout/review";
    }

    @PostMapping
    public String submitCheckout(@Valid @ModelAttribute CheckoutForm checkoutForm,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes,
                                 Model model){
        Cart cart = cartService.getCart();

        if(cart.isEmpty()){
            return "redirect:/products";
        }

        if(bindingResult.hasErrors()){
            model.addAttribute("cart", viewMapper.toCheckoutView(cart));
            return "checkout/review";
        }

        try{
            UUID orderId = checkoutService.createOrder(cart,checkoutForm);
            String checkoutSessionUrl = checkoutService.createCheckoutSession(orderId, cart, checkoutForm.getCustomerEmail());
            if(checkoutSessionUrl == null){
                throw new BackendServiceException("Failed to create checkout session");
            }
            cartService.clear();

            return "redirect:" + checkoutSessionUrl;
        }catch (BackendServiceException ex){
            redirectAttributes.addFlashAttribute("backendServiceError", true);
            return "redirect:/checkout";
        }
    }
}
