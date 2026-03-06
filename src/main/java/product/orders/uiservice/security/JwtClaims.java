package product.orders.uiservice.security;

import java.util.Set;
import java.util.UUID;

/**
 * Claims extracted from a JWT token
 * @param userId id of the user
 * @param email email address of the user
 * @param roles the user's roles
 */
public record JwtClaims(UUID userId,
                        String email,
                        Set<String> roles) {
}
