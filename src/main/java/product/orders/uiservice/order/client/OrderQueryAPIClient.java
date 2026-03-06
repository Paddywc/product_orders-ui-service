package product.orders.uiservice.order.client;

import product.orders.uiservice.order.dto.OrderDto;

import java.util.List;
import java.util.UUID;

/**
 * Client for querying the Order service
 */
public interface OrderQueryAPIClient {
    /**
     * Get order by id
     *
     * @param orderId - order id
     * @return OrderDto for the order with the given id
     */
    OrderDto getOrder(UUID orderId);

    /**
     * Get all orders for a customer
     *
     * @param customerId - customer id
     * @return list of orders for the customer
     */
    List<OrderDto> getCustomerOrders(UUID customerId);
}
