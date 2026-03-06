package product.orders.uiservice.order.service;

import org.springframework.stereotype.Service;
import product.orders.uiservice.order.client.OrderQueryAPIClient;
import product.orders.uiservice.order.dto.OrderDto;

import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    private OrderQueryAPIClient orderQueryAPIClient;

    public OrderServiceImpl(OrderQueryAPIClient orderQueryAPIClient) {
        this.orderQueryAPIClient = orderQueryAPIClient;
    }

    @Override
    public OrderDto getOrder(UUID orderId) {
        return orderQueryAPIClient.getOrder(orderId);
    }

    @Override
    public List<OrderDto> getCustomerOrders(UUID customerId) {
        return orderQueryAPIClient.getCustomerOrders(customerId);
    }


}
