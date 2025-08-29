
package com.example.HNR.Config;

import com.example.HNR.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        final String requestTokenHeader = request.getHeader("Authorization");
        final String requestURI = request.getRequestURI();

        // Ignorer les endpoints publics
        if (isPublicEndpoint(requestURI)) {
            chain.doFilter(request, response);
            return;
        }

        String username = null;
        String jwtToken = null;

        // Extraire le token JWT du header Authorization
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                username = jwtTokenUtil.getUsernameFromToken(jwtToken);
            } catch (Exception e) {
                logger.warn("Impossible d'obtenir le token JWT: " + e.getMessage());
            }
        } else {
            logger.warn("Token JWT manquant ou format incorrect");
        }

        // Valider le token et configurer l'authentification
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                if (jwtTokenUtil.validateToken(jwtToken, username)) {
                    // Récupérer le rôle depuis le token
                    String role = jwtTokenUtil.getRoleFromToken(jwtToken);

                    // Créer les autorités Spring Security
                    List<SimpleGrantedAuthority> authorities = Arrays.asList(
                            new SimpleGrantedAuthority("ROLE_" + role)
                    );

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(username, null, authorities);
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (Exception e) {
                logger.warn("Erreur lors de la validation du token: " + e.getMessage());
            }
        }

        chain.doFilter(request, response);
    }

    private boolean isPublicEndpoint(String uri) {
        return uri.startsWith("/api/auth/login") ||
                uri.startsWith("/api/auth/register") ||
                uri.startsWith("/api/auth/test-password");
    }
}
