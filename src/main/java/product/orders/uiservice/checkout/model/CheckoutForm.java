package product.orders.uiservice.checkout.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Form for collecting user information for checkout
 */
public class CheckoutForm {
    @NotBlank @Email String customerEmail;
    @NotBlank @Size(max=2000) String customerAddress;

    public CheckoutForm() {
    }

    public CheckoutForm(String customerEmail, String customerAddress) {
        this.customerEmail = customerEmail;
        this.customerAddress = customerAddress;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }
}
