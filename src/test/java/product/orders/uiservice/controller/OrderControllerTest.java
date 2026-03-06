package product.orders.uiservice.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import product.orders.uiservice.cart.model.Cart;
import product.orders.uiservice.cart.service.CartService;
import product.orders.uiservice.order.dto.OrderDto;
import product.orders.uiservice.order.service.OrderService;
import product.orders.uiservice.order.view.OrderViewModel;
import product.orders.uiservice.order.view.mapper.OrderViewMapper;
import product.orders.uiservice.security.UserPrincipal;
import product.orders.uiservice.service.UserSessionService;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = OrderController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = CheckoutControllerAdvice.class
        ))
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    OrderService orderService;

    @MockitoBean
    OrderViewMapper viewMapper;

    @MockitoBean
    UserSessionService userSessionService;

    @MockitoBean
    CartService cartService;


    @BeforeEach
    void setUp() {
        // Needed for global model attributes
        Cart cart = mock(Cart.class);
        when(cart.totalItemCount()).thenReturn(0);
        when(cartService.getCart()).thenReturn(cart);
        when(userSessionService.isAuthenticated()).thenReturn(false);
    }

    @Test
    void testViewOrder_OrderExists_ReturnsOrderViewPage() throws Exception {
        // Arrange
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        OrderDto orderDto = mock(OrderDto.class);
        when(orderDto.customerId()).thenReturn(userId);
        OrderViewModel viewModel = mock(OrderViewModel.class);

        when(orderService.getOrder(orderId)).thenReturn(orderDto);
        when(viewMapper.toOrderView(orderDto)).thenReturn(viewModel);

        // Have the usr principal return the same user id as the order dto
        UserPrincipal principal = new UserPrincipal(
                userId,
                "user@example.com",
                List.of(new SimpleGrantedAuthority("USER")),
                "token"
        );
        Authentication userAuthentication =
                new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        principal.getAuthorities()
                );

        // Act
        mockMvc.perform(get("/orders/{id}", orderId)
                        .principal(userAuthentication))
                // Assert
                .andExpect(status().isOk())
                .andExpect(view().name("orders/view"))
                .andExpect(model().attribute("order", viewModel));

        verify(orderService).getOrder(orderId);
        verify(viewMapper).toOrderView(orderDto);
    }

    @Test
    void testListUserOrders_UserHasOrders_ReturnsOrdersList() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();

        OrderDto order1 = mock(OrderDto.class);
        OrderDto order2 = mock(OrderDto.class);

        OrderViewModel view1 = mock(OrderViewModel.class);
        OrderViewModel view2 = mock(OrderViewModel.class);

        when(userSessionService.getCurrentUserId()).thenReturn(userId);
        when(orderService.getCustomerOrders(userId)).thenReturn(List.of(order1, order2));
        when(viewMapper.toOrderView(order1)).thenReturn(view1);
        when(viewMapper.toOrderView(order2)).thenReturn(view2);

        // Act + Assert
        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(view().name("orders/list"))
                .andExpect(model().attribute("orders", List.of(view1, view2)));

        verify(userSessionService).getCurrentUserId();
        verify(orderService).getCustomerOrders(userId);
        verify(viewMapper).toOrderView(order1);
        verify(viewMapper).toOrderView(order2);
    }

    @Test
    void testListUserOrders_NoOrders_ReturnsEmptyList() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();

        when(userSessionService.getCurrentUserId()).thenReturn(userId);
        when(orderService.getCustomerOrders(userId)).thenReturn(List.of());

        // Act + Assert
        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(view().name("orders/list"))
                .andExpect(model().attribute("orders", List.of()));

        verify(orderService).getCustomerOrders(userId);
        verifyNoInteractions(viewMapper);
    }


}
