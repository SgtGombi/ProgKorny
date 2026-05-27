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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// A UserService unit tesztjei Mockito segítségével
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;

    // Alap tesztfelhasználó létrehozása minden teszt előtt
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("tesztuser");
        testUser.setPassword("$2a$10$hashedpassword");
        testUser.setRole("ROLE_USER");
    }

    // Ellenőrzi, hogy létező felhasználó esetén UserDetails-t ad vissza
    @Test
    void loadUserByUsername_letezoPelhasznalo_visszaadjaUserDetails() {
        when(userRepository.findByUsername("tesztuser")).thenReturn(Optional.of(testUser));

        UserDetails result = userService.loadUserByUsername("tesztuser");

        assertThat(result.getUsername()).isEqualTo("tesztuser");
        assertThat(result.getAuthorities()).anyMatch(a -> a.getAuthority().equals("ROLE_USER"));
    }

    // Ellenőrzi, hogy nem létező felhasználó esetén kivétel keletkezik
    @Test
    void loadUserByUsername_nemLetezo_kiveteltDob() {
        when(userRepository.findByUsername("nemletezik")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.loadUserByUsername("nemletezik"))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    // Ellenőrzi, hogy az admin szerepkör helyesen kerül betöltésre
    @Test
    void loadUserByUsername_adminFelhasznalo_adminSzerepkor() {
        testUser.setRole("ROLE_ADMIN");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(testUser));
        testUser.setUsername("admin");

        UserDetails result = userService.loadUserByUsername("admin");

        assertThat(result.getAuthorities()).anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    // Ellenőrzi, hogy a regisztráció bcrypt titkosítással menti a jelszót
    @Test
    void register_ujFelhasznaloMentese() {
        when(passwordEncoder.encode("jelszo123")).thenReturn("$2a$10$hashed");

        userService.register("ujuser", "jelszo123");

        // Ellenőrizzük, hogy a save meghívódott és a jelszó titkosítva van
        verify(userRepository).save(argThat(u ->
                u.getUsername().equals("ujuser") &&
                        u.getPassword().equals("$2a$10$hashed") &&
                        u.getRole().equals("ROLE_USER")
        ));
    }

    // Ellenőrzi, hogy létező felhasználónév esetén true-t ad vissza
    @Test
    void existsByUsername_letezik_igaz() {
        when(userRepository.findByUsername("tesztuser")).thenReturn(Optional.of(testUser));

        boolean result = userService.existsByUsername("tesztuser");

        assertThat(result).isTrue();
    }

    // Ellenőrzi, hogy nem létező felhasználónév esetén false-t ad vissza
    @Test
    void existsByUsername_nemLetezik_hamis() {
        when(userRepository.findByUsername("senki")).thenReturn(Optional.empty());

        boolean result = userService.existsByUsername("senki");

        assertThat(result).isFalse();
    }

    // Ellenőrzi, hogy a felhasználónévből levágja a szóközöket bejelentkezéskor
    @Test
    void loadUserByUsername_szokozokLevagas() {
        when(userRepository.findByUsername("tesztuser")).thenReturn(Optional.of(testUser));

        // Szóközzel adják meg a felhasználónevet - le kell vágni
        UserDetails result = userService.loadUserByUsername("  tesztuser  ");

        assertThat(result.getUsername()).isEqualTo("tesztuser");
    }
}