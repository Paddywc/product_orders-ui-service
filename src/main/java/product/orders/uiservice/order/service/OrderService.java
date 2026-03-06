package product.orders.uiservice.order.service;

import product.orders.uiservice.order.dto.OrderDto;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderDto getOrder(UUID orderId);

    List<OrderDto> getCustomerOrders(UUID customerId);
}
