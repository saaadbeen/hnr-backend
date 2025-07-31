    package com.example.HNR.Config;

    import com.example.HNR.Security.JwtFilter;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.context.annotation.*;
    import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
    import org.springframework.security.config.http.SessionCreationPolicy;
    import org.springframework.security.crypto.bcrypt.*;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.security.web.*;
    import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
    import org.springframework.security.config.annotation.web.builders.HttpSecurity;

    @Configuration
    @EnableWebSecurity
    public class SecurityConfig {

        @Autowired
        private JwtFilter jwtFilter;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http
                    .csrf(csrf -> csrf.disable()) //  CSRF désactivé pour API stateless
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/auth/**").permitAll() // ✅ autorise /auth/*
                            .anyRequest().authenticated() //  toutes les autres nécessitent un token
                    )
                    .sessionManagement(session -> session
                            .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 🧠 pas de session
                    )
                    .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); // ➕ JWT filter

            return http.build();
        }

        //  Encodage des mots de passe avec BCrypt
        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }