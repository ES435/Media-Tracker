package app.mediatracker.security;

import app.mediatracker.feature.auth.service.TokenService;
import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bson.types.ObjectId;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * Filter for JWT-based authentication.
 * <p>
 * This filter checks incoming requests for valid JWT tokens within cookies
 * and sets the Security Context upon successful validation.
 * </p>
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final TokenService tokenService;

    public JwtFilter(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    /**
     * Processes the incoming HTTP request.
     * <p>
     * Extracts the JWT token from cookies, validates it, and sets the
     * Security Context with user information upon successful validation.
     * </p>
     *
     * @param request     The HTTP request
     * @param response    The HTTP response
     * @param filterChain The filter chain for further processing
     * @throws ServletException In case of errors during servlet processing
     * @throws IOException      In case of I/O errors
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = extractAccessTokenFromCookies(request);

        if (token != null) {
            try {
                String userId = tokenService.verifyTokenAndGetUserId(token);
                ObjectId userIdObjectId = new ObjectId(userId);

                // Set the authentication object in the security context
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userIdObjectId, null, Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (JWTVerificationException e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Defines which paths should skip this filter.
     *
     * @param request The HTTP request
     * @return true if the path starts with /auth/, false otherwise
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        return path.startsWith("/auth/");
    }

    /**
     * Extracts the JWT token from the request cookies.
     *
     * @param request The HTTP request
     * @return The JWT token string or null if no token was found
     */
    private String extractAccessTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        // BUGFIX: Added null check for cookies array.
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if ("accessToken".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}