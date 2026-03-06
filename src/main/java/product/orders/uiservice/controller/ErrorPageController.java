package product.orders.uiservice.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller for error pages. Displays custom views for 404, 405, and 500 errors.
 */
@Controller
public class ErrorPageController implements ErrorController {

    @RequestMapping("/error")
    public ModelAndView handleError(HttpServletRequest request, HttpServletResponse response) {
        int status = resolveStatus(request);
        String viewName = resolveViewName(status);
        String errorMessage = resolveErrorMessage(request);

        response.setStatus(status);

        ModelAndView modelAndView = new ModelAndView(viewName);
        modelAndView.addObject("errorMessage", errorMessage);
        return modelAndView;
    }

    private int resolveStatus(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (status instanceof Integer statusCode) {
            return statusCode;
        }
        return HttpStatus.INTERNAL_SERVER_ERROR.value();
    }

    private String resolveViewName(int status) {
        return switch (status) {
            case 404 -> "error/not-found";
            case 405 -> "error/method-not-allowed";
            case 503 -> "error/service-unavailable";
            case 400 -> "error/business-error";
            default -> "error/general-error";
        };
    }

    private String resolveErrorMessage(HttpServletRequest request) {
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        if (message instanceof String text && !text.isBlank()) {
            return text;
        }
        Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        if (exception instanceof Throwable error) {
            String text = error.getMessage();
            if (text != null && !text.isBlank()) {
                return text;
            }
        }
        Object fallback = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        if (fallback instanceof String text && !text.isBlank()) {
            return text;
        }
        return "Unexpected error";
    }
}
