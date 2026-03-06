package product.orders.uiservice.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import product.orders.uiservice.cart.model.Cart;
import product.orders.uiservice.cart.service.CartService;
import product.orders.uiservice.cart.view.CartItemViewModel;
import product.orders.uiservice.cart.view.CartViewModel;
import product.orders.uiservice.cart.view.mapper.CartViewMapper;
import product.orders.uiservice.checkout.view.mapper.CheckoutViewMapper;
import product.orders.uiservice.service.UserSessionService;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = CartController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = CheckoutControllerAdvice.class
        )
)
class CartControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    CartService cartService;

    @MockitoBean
    CartViewMapper cartViewMapper;


    @MockitoBean
    UserSessionService userSessionService;


    @Test
    @DisplayName("viewCart adds cart view model to model for non-empty cart")
    void testViewCart_CartHasItems_AddsCartAndTotalToModel() throws Exception {
        // Arrange
        Cart cart = mock(Cart.class);
        CartViewModel cartView = new CartViewModel(
                List.of(new CartItemViewModel(UUID.randomUUID(), "Widget", "$2.50", 2, "$5.00")),
                "$5.00",
                false
        );
        when(cartService.getCart()).thenReturn(cart);
        when(cartViewMapper.toView(cart)).thenReturn(cartView);

        // Act
        mockMvc.perform(get("/cart"))
                // Assert
                .andExpect(status().isOk())
                .andExpect(view().name("cart/view"))
                .andExpect(model().attribute("cart", cartView));

        verify(cartService, times(2)).getCart();
        verify(cartViewMapper).toView(cart);
    }

    @Test
    void testViewCart_CartIsEmpty_AddsCartAndZeroTotalToModel() throws Exception {
        // Arrange
        Cart cart = mock(Cart.class);
        CartViewModel cartView = new CartViewModel(List.of(), "$0.00", true);
        when(cartService.getCart()).thenReturn(cart);
        when(cartViewMapper.toView(cart)).thenReturn(cartView);

        // Act
        mockMvc.perform(get("/cart"))
                // Assert
                .andExpect(status().isOk())
                .andExpect(view().name("cart/view"))
                .andExpect(model().attribute("cart", cartView));

        verify(cartViewMapper).toView(cart);
    }

    @Test
    void testAddItem_WhenValidRequest_RedirectsToProductsWithFlashAttribute() throws Exception {
        // Arrange
        UUID productId = UUID.randomUUID();


        // Need to mock the cart for the redirect as cart is a global model attribuite
        Cart cart = mock(Cart.class);
        when(cartService.getCart()).thenReturn(cart);

        // Act
        mockMvc.perform(post("/cart/add")
                        .param("productId", productId.toString())
                        .param("quantity", "2"))
                // Assert
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products"))
                .andExpect(flash().attribute("cartAdded", true));

        verify(cartService).addItem(productId, 2);
    }

    @Test
    void testRemoveItem_WhenValidRequest_RedirectsToCart() throws Exception {
        // Arrange
        UUID productId = UUID.randomUUID();

        // Need to mock the cart for the redirect as cart is a global model attribuite
        Cart cart = mock(Cart.class);
        when(cartService.getCart()).thenReturn(cart);

        // Act + Assert
        mockMvc.perform(post("/cart/remove")
                        .param("productId", productId.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));

        verify(cartService).removeItem(productId);
    }

}
