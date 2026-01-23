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
 * Filter für die JWT-basierte Authentifizierung.
 * <p>
 * Dieser Filter prüft eingehende Requests auf gültige JWT-Tokens in den Cookies
 * und setzt bei erfolgreicher Validierung den Security-Context.
 * </p>
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final TokenService tokenService;

    public JwtFilter(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    /**
     * Verarbeitet die eingehende HTTP-Anfrage.
     * <p>
     * Extrahiert das JWT-Token aus den Cookies, validiert es und setzt bei
     * erfolgreicher Validierung den Security-Context mit den Benutzerinformationen.
     * </p>
     *
     * @param request     Die HTTP-Anfrage
     * @param response    Die HTTP-Antwort
     * @param filterChain Die Filter-Kette zur Weiterverarbeitung
     * @throws ServletException Bei Fehlern in der Servlet-Verarbeitung
     * @throws IOException      Bei Ein-/Ausgabefehlern
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = extractAccessTokenFromCookies(request);

        if (token != null) {
            try {
                String userId = tokenService.verifyTokenAndGetUserId(token);
                ObjectId userIdObjectId = new ObjectId(userId);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userIdObjectId, null, Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (JWTVerificationException e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        return path.startsWith("/auth/");
    }

    /**
     * Extrahiert das JWT-Token aus den Cookies der Anfrage.
     *
     * @param request Die HTTP-Anfrage
     * @return Das JWT-Token oder null, wenn kein Token gefunden wurde
     */
    private String extractAccessTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        // BUGFIX: Null-Check hinzugefügt.
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