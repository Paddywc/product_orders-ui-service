package product.orders.uiservice.checkout.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CheckoutFormTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testConstructor_ValidInput_NoValidationErrors() {
        CheckoutForm form =
                new CheckoutForm("customer@example.com", "123 Main Street");

        Set<ConstraintViolation<CheckoutForm>> violations =
                validator.validate(form);

        assertThat(violations).isEmpty();
    }

    @Test
    void testConstructor_BlankEmail_ValidationError() {
        CheckoutForm form =
                new CheckoutForm("", "123 Main Street");

        Set<ConstraintViolation<CheckoutForm>> violations =
                validator.validate(form);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("customerEmail"));
    }

    @Test
    void testConstructor_InvalidEmail_ValidationError() {
        CheckoutForm form =
                new CheckoutForm("not-an-email", "123 Main Street");

        Set<ConstraintViolation<CheckoutForm>> violations =
                validator.validate(form);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("customerEmail"));
    }

    @Test
    void testConstructor_BlankAddress_ValidationError() {
        CheckoutForm form =
                new CheckoutForm("customer@example.com", "");

        Set<ConstraintViolation<CheckoutForm>> violations =
                validator.validate(form);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("customerAddress"));
    }

    @Test
    void testConstructor_AddressTooLong_ValidationError() {
        String longAddress = "a".repeat(2001);

        CheckoutForm form =
                new CheckoutForm("customer@example.com", longAddress);

        Set<ConstraintViolation<CheckoutForm>> violations =
                validator.validate(form);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("customerAddress"));
    }
}
