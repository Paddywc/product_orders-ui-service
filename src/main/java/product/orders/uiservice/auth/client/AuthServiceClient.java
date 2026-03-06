package product.orders.uiservice.auth.client;

import product.orders.uiservice.auth.dto.LoginResponse;

import java.util.UUID;

/**
 * Interface for interacting with the authentication service.
 */
public interface AuthServiceClient {

    /**
     * Send a login request to the authentication service.
     * @param email the email address of the user
     * @param password the raw password of the user
     * @return the response from the authentication service containing the JWT token
     */
    LoginResponse login(String email, String password);

    /**
     * Register a new user with the authentication service.
     * @param email the email address of the user
     * @param rawPassword the raw password of the user
     * @return the UUID of the newly registered user
     */
    UUID register(String email, String rawPassword);
}
