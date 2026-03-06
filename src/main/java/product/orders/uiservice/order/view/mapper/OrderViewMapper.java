package product.orders.uiservice.order.view.mapper;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import product.orders.uiservice.order.enums.OrderProgress;
import product.orders.uiservice.order.dto.OrderDto;
import product.orders.uiservice.order.dto.OrderItemDto;
import product.orders.uiservice.order.enums.PaymentStatus;
import product.orders.uiservice.order.view.OrderItemViewModel;
import product.orders.uiservice.order.view.OrderViewModel;
import product.orders.uiservice.util.DateFormatter;
import product.orders.uiservice.util.MoneyFormatter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper that converts an OrderDto to an OrderViewModel
 */
@Component
public class OrderViewMapper {

    private final MoneyFormatter moneyFormatter;
    private final DateFormatter dateFormatter;

    private final MessageSource messageSource;

    public OrderViewMapper(MoneyFormatter moneyFormatter, DateFormatter dateFormatter, MessageSource messageSource) {
        this.moneyFormatter = moneyFormatter;
        this.dateFormatter = dateFormatter;
        this.messageSource = messageSource;
    }

    private OrderItemViewModel toOrderItemView(OrderItemDto orderItemDto){
        return new OrderItemViewModel(
                orderItemDto.productId(),
                orderItemDto.productName(),
                orderItemDto.quantity(),
                moneyFormatter.formatUsd(orderItemDto.unitPriceUSDCents()));
    }

    public OrderViewModel toOrderView(OrderDto orderDto){
        List<OrderItemViewModel> orderItemViewModels = orderDto.items().stream()
                .map(this::toOrderItemView)
                .collect(Collectors.toList());
        return new OrderViewModel(
                orderDto.orderId(),
                dateFormatter.format(orderDto.createdAt()),
                orderItemViewModels,
                moneyFormatter.formatUsd(orderDto.totalAmountUSDCents()),
                orderDto.customerEmail(),
                orderDto.customerAddress(),
                orderDto.currency(),
                orderDto.status(),
                makeOrderProgressText(orderDto.progress()),
                makePaymentStatusText(orderDto.paymentStatus()));
    }

    private String makeOrderProgressText(OrderProgress progress){
        return switch (progress){
            // Use messages
            case AWAITING_PAYMENT_AND_INVENTORY_RESERVATION -> messageSource.getMessage("order.progress.awaitingPaymentAndInventoryReservation", null, null);
            case AWAITING_INVENTORY_RESERVATION -> messageSource.getMessage("order.progress.awaitingInventoryReservation", null, null);
            case AWAITING_PAYMENT -> messageSource.getMessage("order.progress.awaitingPayment", null, null);
            case CONFIRMED -> messageSource.getMessage("order.progress.confirmed", null, null);
            case CANCELLED_AWAITING_PAYMENT_REFUND -> messageSource.getMessage("order.progress.cancelledAwaitingPaymentRefund", null, null);
            case CANCELLED_INVENTORY_RESERVATION_FAILED -> messageSource.getMessage("order.progress.cancelledInventoryReservationFailed", null, null);
            case CANCELLED_PAYMENT_FAILED -> messageSource.getMessage("order.progress.cancelledPaymentFailed", null, null);
            default -> messageSource.getMessage("order.progress.unknown", null, null);
        };
    }

    private String makePaymentStatusText(PaymentStatus paymentStatus){
        return switch (paymentStatus){
            case PENDING -> messageSource.getMessage("order.paymentStatus.pending", null, null);
            case COMPLETED -> messageSource.getMessage("order.paymentStatus.completed", null, null);
            case FAILED -> messageSource.getMessage("order.paymentStatus.failed", null, null);
            case REFUNDED -> messageSource.getMessage("order.paymentStatus.refunded", null, null);
        };
    }
}
