package app.mediatracker.db.security;

import app.mediatracker.db.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
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

    private final JwtService jwtService;

    public JwtFilter(JwtService jwtService) {
        this.jwtService = jwtService;
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
        String token = extractJwtFromCookies(request);
        String username = jwtService.extractUsername(token);

        if (jwtService.verifyToken(token, username)) {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extrahiert das JWT-Token aus den Cookies der Anfrage.
     *
     * @param request Die HTTP-Anfrage
     * @return Das JWT-Token oder null, wenn kein Token gefunden wurde
     */
    private String extractJwtFromCookies(HttpServletRequest request) {
        for (Cookie cookie : request.getCookies()) {
            if ("jwt".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
