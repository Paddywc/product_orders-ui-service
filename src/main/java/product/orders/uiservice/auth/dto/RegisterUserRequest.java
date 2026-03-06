package product.orders.uiservice.auth.dto;

/**
 * DTO for the request to register a new user with the authentication service.
 * @param email the email address of the user
 * @param rawPassword the raw password of the user
 */
public record RegisterUserRequest(String email, String rawPassword) {
}
