# UI Service

The UI Service provides the user-facing interface for the Product Orders platform. It renders product, cart, checkout,
and order views.
The service acts as a frontend gateway, coordinating interactions between the user and the backend services.

## Responsibilities

- Render Thymeleaf pages for browsing products and orders
- Manage the shopping cart in the HTTP session
- Handle user login and registration by parsing and validating JWTs from the Auth Service
- Create orders and initiate Stripe checkout through backend services
- Provide graceful error handling

## Architecture Role

The UI Service is the platform's web entry point.

- **Aggregates backend APIs** from product, order, auth, and payment services
- **Maintains session state** for the shopping cart
- **Enforces UI access control** based on authenticated user roles

## Tech Stack

| Technology      | Purpose               |
|-----------------|-----------------------|
| Java 17         | Runtime               |
| Spring Boot     | Application framework |
| Spring MVC      | Web UI controllers    |
| Thymeleaf       | Server-rendered views |
| Spring Security | Authentication & auth |
| WebClient       | Backend service calls |
| Docker          | Containerization      |

## Environment Variables

An example list of environment variables is found in [`.env.example`](.env.example).

## Running the Service

Run the service using `docker-compose up --build` from [the root directory](../). To run this service in isolation, copy
the ui service from the root [docker-compose](../docker-compose.yaml) file and run it separately. The service will be
available on port 8085.

## Routes

### Products

- `GET /products` -- List active products (optional `?category=...`)
- `GET /products/{id}` -- Product details

### Cart

- `GET /cart` -- View cart
- `POST /cart/add` -- Add item to cart
- `POST /cart/remove` -- Remove item from cart

### Checkout

- `GET /checkout` -- Review checkout
- `POST /checkout` -- Create order and redirect to Stripe Checkout

### Orders

- `GET /orders` -- List current user's orders
- `GET /orders/{id}` -- View order details

### Auth

- `GET /login` -- Login form
- `POST /login` -- Authenticate user
- `GET /register` -- Registration form
- `POST /register` -- Register user
- `GET /logout` -- Logout and clear session


## Notes on security

- The UI uses **session-based authentication**. After login, a JWT is stored in the session and mapped to a
  `UserPrincipal`.
- The JWT is **validated locally** using the Auth Service JWKS (`SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_JWK_SET_URI`).
- Requests to protected pages are **redirected to `/login?required`** (not a 403).
- Public routes include `/products`, `/cart`, `/login`, `/register`, `/logout`, and error/actuator paths.
- Outbound service calls use WebClient:
  - **Order service calls** include `Authorization: Bearer <token>` when a user is authenticated.
  - **Product and auth calls** are unauthenticated.
  - **Payment calls** attach a token only where required.
