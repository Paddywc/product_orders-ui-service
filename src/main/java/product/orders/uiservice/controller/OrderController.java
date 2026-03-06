package product.orders.uiservice.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import product.orders.uiservice.order.dto.OrderDto;
import product.orders.uiservice.order.service.OrderService;
import product.orders.uiservice.order.view.OrderViewModel;
import product.orders.uiservice.order.view.mapper.OrderViewMapper;
import product.orders.uiservice.security.UserPrincipal;
import product.orders.uiservice.service.UserSessionService;

import java.security.Principal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Controller for order related endpoints. Handles viewing and listing orders.
 */
@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    private final OrderViewMapper viewMapper;

    private final UserSessionService userSessionService;

    public OrderController(OrderService orderService, OrderViewMapper viewMapper, UserSessionService userSessionService) {
        this.orderService = orderService;
        this.viewMapper = viewMapper;
        this.userSessionService = userSessionService;
    }

    @GetMapping("/{id}")
    public String viewOrder(@PathVariable("id") UUID orderId,
                            @RequestParam(name = "paymentSuccess", required = false) Boolean paymentSuccess,
                            @RequestParam(name = "paymentCancelled", required = false) Boolean paymentCancelled,
                            Model model,
                            Principal principal) {

        OrderDto order = orderService.getOrder(orderId);

        // Redirect if not an admin or the order user
        UserPrincipal userPrincipal = (UserPrincipal) ((Authentication) principal).getPrincipal();
        boolean canView = order.customerId().equals(userPrincipal.getUserId());
        if (!canView) {
            for (GrantedAuthority authority : userPrincipal.getAuthorities()) {
                String authorityName = authority.getAuthority();
                if (authorityName != null && authorityName.equals("ADMIN")) {
                    canView = true;
                    break;
                }
            }
        }
        if (!canView) {
            return "redirect:/orders";
        }


        OrderViewModel orderView = viewMapper.toOrderView(order);

        model.addAttribute("order", orderView);

        if (Boolean.TRUE.equals(paymentSuccess)) {
            model.addAttribute("paymentSuccess", true);
        }

        if (Boolean.TRUE.equals(paymentCancelled)) {
            model.addAttribute("paymentCancelled", true);
        }

        return "orders/view";
    }

    @GetMapping
    public String listUserOrders(Model model) {
        UUID userId = userSessionService.getCurrentUserId();
        List<OrderDto> orders = orderService.getCustomerOrders(userId);
        List<OrderViewModel> orderViews = orders.stream().map(viewMapper::toOrderView).collect(Collectors.toList());
        model.addAttribute("orders", orderViews);
        return "orders/list";
    }

}
