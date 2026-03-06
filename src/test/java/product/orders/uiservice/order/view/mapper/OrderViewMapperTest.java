package product.orders.uiservice.order.view.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.context.MessageSource;
import product.orders.uiservice.order.enums.OrderProgress;
import product.orders.uiservice.order.dto.OrderDto;
import product.orders.uiservice.order.dto.OrderItemDto;
import product.orders.uiservice.order.enums.PaymentStatus;
import product.orders.uiservice.order.view.OrderItemViewModel;
import product.orders.uiservice.order.view.OrderViewModel;
import product.orders.uiservice.util.DateFormatter;
import product.orders.uiservice.util.MoneyFormatter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;


class OrderViewMapperTest {

    private OrderViewMapper orderViewMapper;

    @Mock
    private MoneyFormatter moneyFormatter;

    @Mock
    private DateFormatter dateFormatter;

    @Mock
    private MessageSource messageSource;


    @BeforeEach
    void setUp() {
        if (moneyFormatter == null) {
            moneyFormatter = mock(MoneyFormatter.class);
        }
        if (dateFormatter == null) {
            dateFormatter = mock(DateFormatter.class);
        }
        if (messageSource == null) {
            messageSource = mock(MessageSource.class);
        }
        orderViewMapper = new OrderViewMapper(moneyFormatter, dateFormatter, messageSource);
    }

    @Test
    void testToOrderView_OrderWithItems_MapsFieldsAndFormatsValues() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        Instant createdAt = Instant.parse("2026-02-10T10:15:30Z");
        OrderItemDto firstItem = new OrderItemDto(UUID.randomUUID(), "Espresso", 2, 350);
        OrderItemDto secondItem = new OrderItemDto(UUID.randomUUID(), "Filter Coffee", 1, 500);
        OrderDto orderDto = new OrderDto(
                orderId,
                List.of(firstItem, secondItem),
                customerId,
                "buyer@example.com",
                "123 Bean Street",
                1200,
                "USD",
                "PAID",
                OrderProgress.AWAITING_INVENTORY_RESERVATION,
                PaymentStatus.COMPLETED,
                createdAt
        );

        when(dateFormatter.format(createdAt)).thenReturn("Feb 10, 2026");
        when(moneyFormatter.formatUsd(350)).thenReturn("$3.50");
        when(moneyFormatter.formatUsd(500)).thenReturn("$5.00");
        when(moneyFormatter.formatUsd(1200)).thenReturn("$12.00");

        // Act
        OrderViewModel viewModel = orderViewMapper.toOrderView(orderDto);

        // Assert
        assertEquals(orderId, viewModel.orderId());
        assertEquals("Feb 10, 2026", viewModel.orderDateFormatted());
        assertEquals("$12.00", viewModel.totalPriceFormatted());
        assertEquals("buyer@example.com", viewModel.customerEmail());
        assertEquals("123 Bean Street", viewModel.customerAddress());
        assertEquals("USD", viewModel.currency());
        assertEquals("PAID", viewModel.status());
        assertEquals(2, viewModel.orderItems().size());

        OrderItemViewModel firstViewItem = viewModel.orderItems().get(0);
        assertEquals(firstItem.productId(), firstViewItem.productId());
        assertEquals("Espresso", firstViewItem.productName());
        assertEquals(2, firstViewItem.quantity());
        assertEquals("$3.50", firstViewItem.unitPriceFormatted());

        OrderItemViewModel secondViewItem = viewModel.orderItems().get(1);
        assertEquals(secondItem.productId(), secondViewItem.productId());
        assertEquals("Filter Coffee", secondViewItem.productName());
        assertEquals(1, secondViewItem.quantity());
        assertEquals("$5.00", secondViewItem.unitPriceFormatted());

        verify(dateFormatter).format(createdAt);
        verify(moneyFormatter).formatUsd(350);
        verify(moneyFormatter).formatUsd(500);
        verify(moneyFormatter).formatUsd(1200);
        verifyNoMoreInteractions(dateFormatter, moneyFormatter);
    }

    @Test
    void testToOrderView_EmptyItemsList_ReturnsEmptyItemsAndFormattedTotal() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        Instant createdAt = Instant.parse("2026-02-11T09:00:00Z");
        OrderDto orderDto = new OrderDto(
                orderId,
                List.of(),
                customerId,
                "guest@example.com",
                "456 Roast Avenue",
                0,
                "USD",
                "NEW",
                OrderProgress.AWAITING_PAYMENT_AND_INVENTORY_RESERVATION,
                PaymentStatus.PENDING,
                createdAt
        );

        when(dateFormatter.format(createdAt)).thenReturn("Feb 11, 2026");
        when(moneyFormatter.formatUsd(0)).thenReturn("$0.00");

        // Act
        OrderViewModel viewModel = orderViewMapper.toOrderView(orderDto);

        // Assert
        assertEquals(orderId, viewModel.orderId());
        assertEquals("Feb 11, 2026", viewModel.orderDateFormatted());
        assertTrue(viewModel.orderItems().isEmpty());
        assertEquals("$0.00", viewModel.totalPriceFormatted());
        assertEquals("guest@example.com", viewModel.customerEmail());
        assertEquals("456 Roast Avenue", viewModel.customerAddress());
        assertEquals("USD", viewModel.currency());
        assertEquals("NEW", viewModel.status());

        verify(dateFormatter).format(createdAt);
        verify(moneyFormatter).formatUsd(0);
        verifyNoMoreInteractions(dateFormatter, moneyFormatter);
    }
}
