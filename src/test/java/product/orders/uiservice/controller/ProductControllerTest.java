package product.orders.uiservice.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import product.orders.uiservice.cart.model.Cart;
import product.orders.uiservice.cart.service.CartService;
import product.orders.uiservice.checkout.view.mapper.CheckoutViewMapper;
import product.orders.uiservice.product.dto.ProductDto;
import product.orders.uiservice.product.service.ProductService;
import product.orders.uiservice.product.view.ProductDetailViewModel;
import product.orders.uiservice.product.view.ProductListViewModel;
import product.orders.uiservice.product.view.mapper.ProductViewMapper;
import product.orders.uiservice.service.UserSessionService;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ProductController.class,
        excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = CheckoutControllerAdvice.class
        ))
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private ProductViewMapper productViewMapper;


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
    void testListProducts_NoCategoryProvided_ReturnsProductsAndCategories() throws Exception {
        // Arrange
        ProductListViewModel listView = mock(ProductListViewModel.class);

        when(productService.getActiveProducts(null))
                .thenReturn(List.of());
        when(productViewMapper.toListView(any()))
                .thenReturn(listView);

        // Act
        mockMvc.perform(get("/products"))
                // Assert
                .andExpect(status().isOk())
                .andExpect(view().name("products/list"))
                .andExpect(model().attribute("products", listView))
                .andExpect(model().attributeExists("categories"));

        verify(productService).getActiveProducts(null);
        verify(productViewMapper).toListView(any());
    }

    @Test
    void testListProducts_CategoryProvided_FiltersProducts() throws Exception {
        // Arrange
        ProductListViewModel listView = mock(ProductListViewModel.class);

        String category = "ELECTRONICS";
        when(productService.getActiveProducts(category))
                .thenReturn(List.of());
        when(productViewMapper.toListView(any()))
                .thenReturn(listView);

        // Act
        mockMvc.perform(get("/products")
                        .param("category", category))
                // Assert
                .andExpect(status().isOk())
                .andExpect(view().name("products/list"))
                .andExpect(model().attribute("products", listView));

        verify(productService).getActiveProducts(category);
    }

    @Test
    void testProductDetails_ValidId_ReturnsDetailsView() throws Exception {
        // Arrange
        UUID productId = UUID.randomUUID();
        ProductDto productDto = mock(ProductDto.class);
        ProductDetailViewModel detailsView = mock(ProductDetailViewModel.class);

        when(productService.getProduct(productId)).thenReturn(productDto);
        when(productViewMapper.toDetailsView(productDto)).thenReturn(detailsView);

        // Act + Assert
        mockMvc.perform(get("/products/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(view().name("products/details"))
                .andExpect(model().attribute("product", detailsView));

        verify(productService).getProduct(productId);
        verify(productViewMapper).toDetailsView(productDto);
    }
}
