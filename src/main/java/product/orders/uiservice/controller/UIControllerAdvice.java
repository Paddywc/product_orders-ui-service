package product.orders.uiservice.controller;

import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import product.orders.uiservice.cart.service.CartService;
import product.orders.uiservice.exception.BackendServiceException;
import product.orders.uiservice.exception.OrderNotFoundException;
import product.orders.uiservice.exception.ProductNotFoundException;

/**
 * Base controller advice for handling exceptions across the UI controllers
 */
@ControllerAdvice(annotations = Controller.class)
public class UIControllerAdvice {

    protected final CartService cartService;

    public UIControllerAdvice(CartService cartService) {
        this.cartService = cartService;
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public String handleOrderNotFound(OrderNotFoundException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("orderNotFound", true);
        return "redirect:/orders";
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public String handleProductNotFound(ProductNotFoundException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("productNotFound", true);
        return "redirect:/products";
    }

    @ExceptionHandler({BackendServiceException.class, WebClientRequestException.class})
    public ModelAndView handleServiceUnavailableError(BackendServiceException ex) {
        ModelAndView modelAndView = new ModelAndView("error/service-unavailable");
        modelAndView.setStatus(HttpStatus.SERVICE_UNAVAILABLE);
        modelAndView.addObject("errorMessage", ex.getMessage());
        return modelAndView;
    }

    @ExceptionHandler({IllegalArgumentException.class,
            IllegalStateException.class,
            MethodArgumentTypeMismatchException.class,
            HttpMediaTypeNotSupportedException.class})
    public ModelAndView handleBusinessError(RuntimeException ex) {
        ModelAndView modelAndView = new ModelAndView("error/business-error");
        modelAndView.setStatus(HttpStatus.BAD_REQUEST);
        modelAndView.addObject("errorMessage", ex.getMessage());
        return modelAndView;
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ModelAndView handleNotFound(NoHandlerFoundException ex) {
        ModelAndView modelAndView = new ModelAndView("error/not-found");
        modelAndView.setStatus(HttpStatus.NOT_FOUND);
        modelAndView.addObject("errorMessage", ex.getMessage());
        return modelAndView;
    }


    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ModelAndView handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        ModelAndView modelAndView = new ModelAndView("error/method-not-allowed");
        modelAndView.setStatus(HttpStatus.METHOD_NOT_ALLOWED);
        modelAndView.addObject("errorMessage", ex.getMessage());
        return modelAndView;
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ModelAndView handleValidation(Exception ex) {
        BindingResult bindingResult = ex instanceof MethodArgumentNotValidException manv
                ? manv.getBindingResult()
                : ((BindException) ex).getBindingResult();
        Object target = bindingResult.getTarget();

        return validationModelAndView(target, bindingResult);
    }

    protected @NonNull ModelAndView validationModelAndView(Object target, BindingResult bindingResult) {
        ModelAndView modelAndView = new ModelAndView("error/business-error");
        modelAndView.setStatus(HttpStatus.BAD_REQUEST);
        modelAndView.addObject("errorMessage", "Validation failed.");
        return modelAndView;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleUnhandled(Exception ex) {
        ModelAndView modelAndView = new ModelAndView("error/general-error");
        modelAndView.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        modelAndView.addObject("errorMessage", ex.getMessage());
        return modelAndView;
    }


}
