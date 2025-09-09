package com.example.HNR.Config;

import com.example.HNR.Model.Mongodb.User;
import com.example.HNR.Service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.debug("Tentative de chargement de l'utilisateur avec email: {}", email);

        Optional<User> userOptional = userService.findByEmail(email);

        if (userOptional.isEmpty()) {
            logger.warn("Utilisateur non trouvé avec l'email: {}", email);
            throw new UsernameNotFoundException("Utilisateur non trouvé avec l'email: " + email);
        }

        User user = userOptional.get();

        logger.debug("Utilisateur trouvé: {} avec rôle: {}", user.getEmail(), user.getRole());

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(Arrays.asList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())))
                .build();
    }
}