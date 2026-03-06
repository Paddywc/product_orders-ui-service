package product.orders.uiservice.product.view.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import product.orders.uiservice.product.dto.ProductDto;
import product.orders.uiservice.product.view.ProductDetailViewModel;
import product.orders.uiservice.util.MoneyFormatter;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class ProductViewMapperTest {

    private ProductViewMapper productViewMapper;

    @Mock
    private MoneyFormatter moneyFormatter;

    @BeforeEach
    void setUp() {
        if(moneyFormatter == null){
            moneyFormatter = mock(MoneyFormatter.class);
        }
        productViewMapper = new ProductViewMapper(moneyFormatter);
    }

    @Test
    void testProductViewMapperToDetailsView_ActiveStatus_MapsFieldsAndActiveTrue() {
        // Arrange
        UUID productId = UUID.randomUUID();
        ProductDto productDto = new ProductDto(
                productId,
                "Single Origin",
                "Ethiopian beans",
                1299L,
                "Coffee",
                "ACTIVE"
        );
        when(moneyFormatter.formatUsd(1299L)).thenReturn("$12.99");

        // Act
        ProductDetailViewModel viewModel = productViewMapper.toDetailsView(productDto);

        // Assert
        assertEquals(productId, viewModel.productId());
        assertEquals("Single Origin", viewModel.name());
        assertEquals("Ethiopian beans", viewModel.description());
        assertEquals("$12.99", viewModel.priceFormatted());
        assertEquals("Coffee", viewModel.category());
        assertTrue(viewModel.active());
        verify(moneyFormatter).formatUsd(1299L);
        verifyNoMoreInteractions(moneyFormatter);
    }

    @Test
    void testProductViewMapperToDetailsView_InactiveStatus_MapsFieldsAndActiveFalse() {
        // Arrange
        UUID productId = UUID.randomUUID();
        ProductDto productDto = new ProductDto(
                productId,
                "Mug",
                "Ceramic mug",
                0L,
                "Accessories",
                "INACTIVE"
        );
        when(moneyFormatter.formatUsd(0L)).thenReturn("$0.00");

        // Act
        ProductDetailViewModel viewModel = productViewMapper.toDetailsView(productDto);

        // Assert
        assertEquals(productId, viewModel.productId());
        assertEquals("Mug", viewModel.name());
        assertEquals("Ceramic mug", viewModel.description());
        assertEquals("$0.00", viewModel.priceFormatted());
        assertEquals("Accessories", viewModel.category());
        assertFalse(viewModel.active());
        verify(moneyFormatter).formatUsd(0L);
        verifyNoMoreInteractions(moneyFormatter);
    }
}
