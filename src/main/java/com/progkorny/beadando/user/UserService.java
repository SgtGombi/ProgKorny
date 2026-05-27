package com.progkorny.beadando.user;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    // SpringSecurity intezi a logint es a logikat
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String cleanUsername = username.trim();

        User user = userRepository.findByUsername(cleanUsername)
                .orElseThrow(() -> new UsernameNotFoundException(cleanUsername));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername().trim())
                .password(user.getPassword().trim())
                .roles(user.getRole().trim().replace("ROLE_", ""))
                .build();
    }
    // register folyamata, bcrypt, default user szerep
    public void register(String username, String rawPassword) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole("ROLE_USER");
        userRepository.save(user);
    }
    // letezik-e a user
    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
}