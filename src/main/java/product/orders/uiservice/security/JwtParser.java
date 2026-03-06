package product.orders.uiservice.security;

/**
 * Parses JWT tokens and extracts claims
 */
public interface JwtParser {
    JwtClaims parse(String token);

}
