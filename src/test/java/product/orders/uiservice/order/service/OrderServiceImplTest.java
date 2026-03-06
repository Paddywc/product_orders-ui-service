package product.orders.uiservice.order.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import product.orders.uiservice.order.client.OrderQueryAPIClient;
import product.orders.uiservice.order.dto.OrderDto;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    OrderQueryAPIClient orderQueryAPIClient;

    @InjectMocks
    OrderServiceImpl orderService;

    @Test
    void testGetOrder_WhenOrderIdProvided_DelegatesToApiClient() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        OrderDto orderDto = mock(OrderDto.class);
        when(orderQueryAPIClient.getOrder(orderId)).thenReturn(orderDto);

        // Act
        OrderDto result = orderService.getOrder(orderId);

        // Assert
        assertThat(result).isSameAs(orderDto);
        verify(orderQueryAPIClient).getOrder(orderId);
        verifyNoMoreInteractions(orderQueryAPIClient);
    }

    @Test
    void testGetCustomerOrders_WhenCustomerIdProvided_DelegatesToApiClient() {
        // Arrange
        UUID customerId = UUID.randomUUID();
        List<OrderDto> orders = List.of(mock(OrderDto.class), mock(OrderDto.class));
        when(orderQueryAPIClient.getCustomerOrders(customerId)).thenReturn(orders);

        // Act
        List<OrderDto> result = orderService.getCustomerOrders(customerId);

        // Assert
        assertThat(result).isSameAs(orders);
        verify(orderQueryAPIClient).getCustomerOrders(customerId);
        verifyNoMoreInteractions(orderQueryAPIClient);
    }
}
