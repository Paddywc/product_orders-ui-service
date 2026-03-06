package product.orders.uiservice.checkout.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

/**
 * Request for creating an order that is sent to the order service
 */
public record CreateOrderRequest(@NotEmpty List<CreateOrderItemRequest> items,
                                 @NotNull UUID customerId,
                                 @NotNull @Email String customerEmail,

                                 @NotNull @Size(max=2000) String customerAddress,
                                 @NotNull Long totalAmountCents,
                                 @NotNull String currency) {
}
