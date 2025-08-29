package com.example.HNR.Config;

import com.example.HNR.Model.Mongodb.User;
import com.example.HNR.Service.UserService;
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

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> userOptional = userService.findByEmail(email);

        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("Utilisateur non trouvé avec l'email: " + email);
        }

        User user = userOptional.get();

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(Arrays.asList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())))
                .build();
    }
}