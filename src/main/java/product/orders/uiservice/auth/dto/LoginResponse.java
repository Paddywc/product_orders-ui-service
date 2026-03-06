package product.orders.uiservice.auth.dto;

/**
 * DTO for the response from the authentication service containing the JWT token.
 * @param token the JWT token for the authenticated user
 */
public record LoginResponse(String token) {
}
