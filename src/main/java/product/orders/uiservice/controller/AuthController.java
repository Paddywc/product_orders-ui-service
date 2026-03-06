package product.orders.uiservice.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import product.orders.uiservice.auth.dto.LoginResponse;
import product.orders.uiservice.auth.client.AuthServiceClient;
import product.orders.uiservice.auth.model.LoginForm;
import product.orders.uiservice.auth.model.RegisterForm;
import product.orders.uiservice.exception.BackendServiceException;
import product.orders.uiservice.exception.EmailAlreadyExistsException;
import product.orders.uiservice.exception.LoginFailedException;
import product.orders.uiservice.exception.RegistrationFailedException;
import product.orders.uiservice.security.JwtClaims;
import product.orders.uiservice.security.JwtParser;
import product.orders.uiservice.security.UserPrincipal;

import java.util.Collection;
import java.util.Collections;

/**
 * Controller for authentication related endpoints. Sends to the auth microservice for authentication.
 */
@Controller
public class AuthController {

    private static final String ACCESS_TOKEN_SESSION_KEY = "ACCESS_TOKEN";

    private final AuthServiceClient authServiceClient;

    private final RequestCache requestCache;

    private final JwtParser jwtParser;

    public AuthController(AuthServiceClient authServiceClient, RequestCache requestCache, JwtParser jwtParser) {
        this.authServiceClient = authServiceClient;
        this.requestCache = requestCache;
        this.jwtParser = jwtParser;
    }

    @GetMapping("/login")
    public String loginForm(Model model) {
        if (!model.containsAttribute("loginForm")) {
            model.addAttribute("loginForm", new LoginForm());
        }
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("loginForm") LoginForm loginForm,
                        BindingResult bindingResult,
                        HttpServletRequest request,
                        HttpServletResponse response,
                        Model model) {
        if (bindingResult.hasErrors()) {
            return "auth/login";
        }

        try {
            LoginResponse loginTokenResponse = authServiceClient.login(loginForm.getEmail(), loginForm.getPassword());
            if (loginTokenResponse == null) {
                throw new LoginFailedException();
            }
            // Validate and parse JWT
            JwtClaims claims = jwtParser.parse(loginTokenResponse.token());

            //  Build authorities
            Collection<? extends GrantedAuthority> authorities =
                    claims.roles() == null
                            ? Collections.emptyList()
                            : claims.roles().stream()
                            .map(SimpleGrantedAuthority::new)
                            .toList();

            // Build principal
            UserPrincipal principal = new UserPrincipal(
                    claims.userId(),
                    claims.email(),
                    authorities,
                    loginTokenResponse.token()
            );

            //  Create Authentication
            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(
                            principal,
                            null,
                            authorities
                    );

            //  Store in SecurityContext
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);

            // Persist SecurityContext to session
            HttpSession session = request.getSession(true);
            session.setAttribute(
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    context
            );

            // Redirect to the previous page if it exists.
            SavedRequest savedRequest = requestCache.getRequest(request, response);
            if (savedRequest != null) {
                requestCache.removeRequest(request, response);
                return "redirect:" + savedRequest.getRedirectUrl();
            }

            return "redirect:/products";
        } catch (LoginFailedException ex) {
            model.addAttribute("loginError", true);
            return "auth/login";
        } catch (BackendServiceException ex) {
            model.addAttribute("backendServiceError", true);
            return "auth/login";
        }
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        if (!model.containsAttribute("registerForm")) {
            model.addAttribute("registerForm", new RegisterForm());
        }
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerForm") RegisterForm registerForm,
                           BindingResult bindingResult,
                           HttpServletRequest request,
                           HttpServletResponse response,
                           HttpSession session,
                           Model model) {
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        try {
            if (authServiceClient.register(registerForm.getEmail(), registerForm.getPassword()) == null) {
                throw new RegistrationFailedException();
            }
            LoginResponse loginTokenResponse = authServiceClient.login(registerForm.getEmail(), registerForm.getPassword());
            if (loginTokenResponse == null) {
                throw new RegistrationFailedException();
            }
            session.setAttribute(ACCESS_TOKEN_SESSION_KEY, loginTokenResponse.token());



            SavedRequest savedRequest = requestCache.getRequest(request, response);
            if (savedRequest != null) {
                requestCache.removeRequest(request, response);
                return "redirect:" + savedRequest.getRedirectUrl();
            }

            return "redirect:/products";
        } catch (EmailAlreadyExistsException ex) {
            model.addAttribute("emailExistsError", true);
            return "auth/register";
        } catch (RegistrationFailedException | LoginFailedException ex) {
            model.addAttribute("registerError", true);
            return "auth/register";
        } catch (BackendServiceException ex) {
            model.addAttribute("backendServiceError", true);
            return "auth/register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        return "redirect:/products";
    }
}
