package product.orders.uiservice.security;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
public class JwtParserImpl implements JwtParser {

    private final JwtDecoder decoder;

    public JwtParserImpl(JwtDecoder decoder) {
        this.decoder = decoder;
    }

    public JwtClaims parse(String token) {

        Jwt jwt = decoder.decode(token);

        UUID userId = UUID.fromString(jwt.getSubject());
        String email = jwt.getClaim("email");
        List<String> roles = jwt.getClaim("roles");

        return new JwtClaims(userId, email, Set.copyOf(roles));
    }
}

