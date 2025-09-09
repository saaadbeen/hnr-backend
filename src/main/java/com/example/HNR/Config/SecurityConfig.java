package com.example.HNR.Config;

import com.example.HNR.Config.JwtAuthenticationEntryPoint;
import com.example.HNR.Config.JwtRequestFilter;
import com.example.HNR.Model.Mongodb.User;
import com.example.HNR.Repository.Mongodb.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    @Autowired private JwtRequestFilter jwtRequestFilter;
    @Autowired private UserRepository userRepository;

    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return email -> userRepository.findByEmail(email)
                .map(user -> org.springframework.security.core.userdetails.User.builder()
                        .username(user.getEmail())
                        .password(user.getPassword())
                        .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())))
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .authorizeHttpRequests(auth -> auth
                        // Preflight
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Auth public
                        .requestMatchers("/api/auth/login", "/api/auth/refresh", "/api/auth/register").permitAll()

                        // Santé / Actuator (dev)
                        .requestMatchers("/api/actuator/**", "/actuator/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/fichiers/upload").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/fichiers/raw/**").permitAll()
                        // Notifications REST
                        .requestMatchers(HttpMethod.GET, "/api/notifications/**").hasAnyRole("MEMBRE_DSI","GOUVERNEUR","AGENT_AUTORITE")
                        .requestMatchers(HttpMethod.POST, "/api/notifications/*/read").hasAnyRole("MEMBRE_DSI","GOUVERNEUR","AGENT_AUTORITE")
                        .requestMatchers(HttpMethod.POST, "/api/notifications/read-all").hasAnyRole("MEMBRE_DSI","GOUVERNEUR","AGENT_AUTORITE")
                        .requestMatchers(HttpMethod.DELETE, "/api/notifications/*").hasAnyRole("MEMBRE_DSI","GOUVERNEUR","AGENT_AUTORITE")

                        // SSE notif (nécessite JWT)
                        .requestMatchers(HttpMethod.GET, "/api/notifications/stream").hasAnyRole("MEMBRE_DSI","GOUVERNEUR","AGENT_AUTORITE")
                        .requestMatchers("/api/notifications/stream/status").hasRole("MEMBRE_DSI")
                        .requestMatchers("/api/notifications/stream/disconnect/*").hasRole("MEMBRE_DSI")
                        .requestMatchers("/api/notifications/stream/test").hasRole("MEMBRE_DSI")
                        .requestMatchers("/api/notifications/stream/cleanup").hasRole("MEMBRE_DSI")

                        // Autres APIs métier
                        .requestMatchers("/api/actions/**","/api/missions/**","/api/changements/**")
                        .hasAnyRole("MEMBRE_DSI","GOUVERNEUR","AGENT_AUTORITE")

                        // Tout le reste
                        .anyRequest().authenticated()
                );

        // Filtre JWT
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration c = new CorsConfiguration();

        // Front dev
        c.setAllowedOrigins(List.of("http://localhost:3000", "http://127.0.0.1:3000"));

        c.setAllowedMethods(List.of("GET","POST","PUT","DELETE","PATCH","OPTIONS"));
        c.setAllowedHeaders(List.of(
                "Authorization","Content-Type","Accept","Cache-Control",
                "X-Requested-With","Origin","Last-Event-ID"
        ));
        c.setExposedHeaders(List.of("Authorization","Content-Disposition"));

        // Pas de cookies -> false (on utilise Authorization: Bearer)
        c.setAllowCredentials(false);
        c.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", c);
        return src;
    }
}
