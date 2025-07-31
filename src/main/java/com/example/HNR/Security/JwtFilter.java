package com.example.HNR.Security;

import com.example.HNR.Security.CustomUserDetailsService;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    // 🔄 Cette méthode est exécutée à chaque requête HTTP
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 🔎 On lit l'en-tête Authorization (format attendu : Bearer TOKEN)
        String header = request.getHeader("Authorization");
        String token = null;
        String email = null;

        // ✅ Vérifie si un token Bearer est présent
        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7); // on retire "Bearer "
            email = jwtUtil.extractEmail(token);
        }

        // 🛡️ Si l'utilisateur n'est pas encore authentifié
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            // ✅ Vérifie la validité du token
            if (jwtUtil.isTokenValid(token)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());

                // 🔄 Ajoute les infos à la requête
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 🎯 Active l'utilisateur dans le contexte Spring Security
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // ⏭ Continue la requête HTTP normalement
        filterChain.doFilter(request, response);
    }
}