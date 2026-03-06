package product.orders.uiservice.auth.dto;

/**
 * DTO for the request to authenticate a user with the authentication service.
 * @param email the email address of the user
 * @param password the raw password of the user
 */
public record LoginRequest(String email, String password) {
}
