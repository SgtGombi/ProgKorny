package com.progkorny.beadando.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

// A UserService unit tesztjei Mockito segítségével
// https://www.baeldung.com/spring-mockmvc-vs-webmvctest
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;

    // Alap tesztfelhasználó létrehozása minden teszt előtt, a
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("tesztuser");
        testUser.setPassword("$2a$10$hashedpassword");
        testUser.setRole("ROLE_USER");
    }

    // ha van felhasználó -> adjon userdetails
    @Test
    void shouldLoadExistingUserAsUserDetails() {
        when(userRepository.findByUsername("tesztuser")).thenReturn(Optional.of(testUser));

        UserDetails result = userService.loadUserByUsername("tesztuser");

        assertThat(result.getUsername()).isEqualTo("tesztuser");
        assertThat(result.getAuthorities()).anyMatch(a -> a.getAuthority().equals("ROLE_USER"));
    }

    // Nem létező felhasználó legyen kivétel
    @Test
    void shouldThrowException_whenUserNotFound() {
        when(userRepository.findByUsername("nemletezik")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.loadUserByUsername("nemletezik"))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    // Megnézi hogy tényleg az admin szerepkör kerül e betöltésre
    @Test
    void shouldLoadAdminUserWithAdminRole() {
        testUser.setRole("ROLE_ADMIN");
        testUser.setUsername("admin");

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(testUser));

        UserDetails result = userService.loadUserByUsername("admin");

        assertThat(result.getAuthorities()).anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    // Csekkol hogy tényleg Bcrypt e
    @Test
    void shouldRegisterNewUserWithEncodedPassword() {
        when(passwordEncoder.encode("jelszo123")).thenReturn("$2a$10$hashed");

        userService.register("ujuser", "jelszo123");

        // Ellenőrizzük, hogy a save meghívódott és a jelszó titkosítva van
        verify(userRepository).save(argThat(u ->
                u.getUsername().equals("ujuser") &&
                        u.getPassword().equals("$2a$10$hashed") &&
                        u.getRole().equals("ROLE_USER")
        ));
    }

    // Létező felhasználónév esetén true-t ad vissza
    @Test
    void shouldReturnTrue_whenUsernameExists() {
        when(userRepository.findByUsername("tesztuser")).thenReturn(Optional.of(testUser));

        boolean result = userService.existsByUsername("tesztuser");

        assertThat(result).isTrue();
    }

    // Ellenőrzi nem létező felh. esetén false-t ad vissza
    @Test
    void shouldReturnFalse_whenUsernameDoesNotExist() {
        when(userRepository.findByUsername("senki")).thenReturn(Optional.empty());

        boolean result = userService.existsByUsername("senki");

        assertThat(result).isFalse();
    }

    // Ellenőrz hogy a felhasználónévből levágja e a szóközöket bejelentkezéskor
    @Test
    void shouldTrimWhitespaceFromUsername() {
        when(userRepository.findByUsername("tesztuser")).thenReturn(Optional.of(testUser));

        // Szóközzel adják meg a felhasználónevet - le kell vágni
        UserDetails result = userService.loadUserByUsername("  tesztuser  ");

        assertThat(result.getUsername()).isEqualTo("tesztuser");
    }
}