package com.progkorny.beadando.user;

import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String cleanUsername = username.trim();
        System.out.println("PRÓBA LOGIN: " + cleanUsername);

        User user = userRepository.findByUsername(cleanUsername)
                .orElseThrow(() -> new UsernameNotFoundException(cleanUsername));

        System.out.println("USER TALÁLVA " + user.getUsername());

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername().trim())
                .password(user.getPassword().trim())
                .roles(user.getRole().trim().replace("ROLE_", ""))
                .build();
    }

    public void register(String username, String rawPassword) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole("ROLE_USER");
        userRepository.save(user);
    }

    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
}