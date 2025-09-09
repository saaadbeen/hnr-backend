package com.example.HNR.Config;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        final String requestURI = request.getRequestURI();
        final String method = request.getMethod();

        // Log de la requête pour debug (à retirer en production)
        if (logger.isDebugEnabled()) {
            logger.debug("Processing request: {} {}", method, requestURI);
        }

        // Ignorer les endpoints publics et les requêtes OPTIONS (CORS preflight)
        if (isPublicEndpoint(requestURI) || "OPTIONS".equalsIgnoreCase(method)) {
            chain.doFilter(request, response);
            return;
        }

        // Extraire et traiter le token JWT
        try {
            String jwtToken = extractTokenFromRequest(request);

            if (jwtToken != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                processJwtAuthentication(request, jwtToken);
            }
        } catch (Exception e) {
            logger.warn("Erreur lors du traitement de l'authentification JWT pour {}: {}",
                    requestURI, e.getMessage());
            // Ne pas bloquer la requête, laisser Spring Security gérer l'absence d'authentification
        }

        chain.doFilter(request, response);
    }

    /**
     * Extraire le token JWT du header Authorization
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        final String requestTokenHeader = request.getHeader(AUTHORIZATION_HEADER);

        if (StringUtils.hasText(requestTokenHeader) && requestTokenHeader.startsWith(BEARER_PREFIX)) {
            return requestTokenHeader.substring(BEARER_PREFIX.length());
        }

        // Log seulement si ce n'est pas un endpoint public
        if (!isPublicEndpoint(request.getRequestURI())) {
            logger.debug("Token JWT manquant ou format incorrect pour: {}", request.getRequestURI());
        }

        return null;
    }

    /**
     * Traiter l'authentification JWT et configurer le contexte Spring Security
     */
    private void processJwtAuthentication(HttpServletRequest request, String jwtToken) {
        try {
            // Extraire l'username du token
            String username = jwtTokenUtil.getUsernameFromToken(jwtToken);

            if (!StringUtils.hasText(username)) {
                logger.warn("Username vide dans le token JWT");
                return;
            }

            // Valider le token
            if (!jwtTokenUtil.validateToken(jwtToken, username)) {
                logger.warn("Token JWT invalide ou expiré pour l'utilisateur: {}", username);
                return;
            }

            // Récupérer le rôle depuis le token
            String role = jwtTokenUtil.getRoleFromToken(jwtToken);

            // Créer les autorités Spring Security
            List<SimpleGrantedAuthority> authorities;
            if (StringUtils.hasText(role)) {
                // S'assurer que le rôle commence par "ROLE_"
                String formattedRole = role.startsWith("ROLE_") ? role : "ROLE_" + role;
                authorities = Arrays.asList(new SimpleGrantedAuthority(formattedRole));
            } else {
                logger.warn("Aucun rôle trouvé dans le token pour l'utilisateur: {}", username);
                authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")); // Rôle par défaut
            }

            // Créer l'objet d'authentification Spring Security
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    username, null, authorities);
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Configurer le contexte de sécurité
            SecurityContextHolder.getContext().setAuthentication(authToken);

            logger.debug("Utilisateur authentifié avec succès: {} avec rôle: {}", username, role);

        } catch (Exception e) {
            logger.error("Erreur lors du traitement de l'authentification JWT: {}", e.getMessage());
            // Nettoyer le contexte en cas d'erreur
            SecurityContextHolder.clearContext();
        }
    }

    /**
     * Vérifier si l'endpoint est public (ne nécessite pas d'authentification)
     */
    private boolean isPublicEndpoint(String uri) {
        if (uri == null) {
            return false;
        }

        // Liste des patterns d'endpoints publics
        String[] publicPatterns = {
                "/api/auth/login",
                "/api/auth/register", // Bien qu'il soit protégé par @PreAuthorize, on laisse passer le filtre
                "/files/",
                "/api/actuator/health", "/api/actuator/info",
                "/actuator/health",
                "/error"
        };

        for (String pattern : publicPatterns) {
            if (uri.startsWith(pattern)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Déterminer si le filtre doit s'appliquer à cette requête
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        // Ne pas filtrer les ressources statiques
        return path.startsWith("/static/") ||
                path.startsWith("/api/public/") ||
                path.startsWith("/api/actuator/") || path.startsWith("/actuator/") ||
                path.startsWith("/public/") ||
                path.endsWith(".css") ||
                path.endsWith(".js") ||
                path.endsWith(".png") ||
                path.endsWith(".jpg") ||
                path.endsWith(".jpeg") ||
                path.endsWith(".gif") ||
                path.endsWith(".ico");
    }
}