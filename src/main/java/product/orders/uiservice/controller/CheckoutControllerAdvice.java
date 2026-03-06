package product.orders.uiservice.controller;

import org.jspecify.annotations.NonNull;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.ModelAndView;
import product.orders.uiservice.cart.model.Cart;
import product.orders.uiservice.cart.service.CartService;
import product.orders.uiservice.checkout.model.CheckoutForm;
import product.orders.uiservice.checkout.view.mapper.CheckoutViewMapper;

/**
 * Extra validation required for the checkout form
 */
@ControllerAdvice(assignableTypes = CheckoutController.class)
public class CheckoutControllerAdvice extends UIControllerAdvice {
    private final CheckoutViewMapper checkoutViewMapper;

    public CheckoutControllerAdvice(CartService cartService, CheckoutViewMapper checkoutViewMapper) {
        super(cartService);
        this.checkoutViewMapper = checkoutViewMapper;
    }

    @Override
    protected @NonNull ModelAndView validationModelAndView(Object target, BindingResult bindingResult) {
        if (target instanceof CheckoutForm checkoutForm) {
            return checkoutValidationError(checkoutForm, bindingResult);
        }

        return super.validationModelAndView(target, bindingResult);
    }

    private ModelAndView checkoutValidationError(CheckoutForm checkoutForm, BindingResult bindingResult) {
        Cart cart = cartService.getCart();
        if (cart.isEmpty()) {
            return new ModelAndView("redirect:/products");
        }

        ModelAndView modelAndView = new ModelAndView("checkout/review");
        modelAndView.addObject("cart", checkoutViewMapper.toCheckoutView(cart));
        modelAndView.addObject("checkoutForm", checkoutForm);
        modelAndView.addObject(BindingResult.MODEL_KEY_PREFIX + "checkoutForm", bindingResult);
        return modelAndView;
    }
}
