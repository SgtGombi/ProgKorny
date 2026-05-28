package com.progkorny.beadando.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UserRepository integrációs tesztjei.
 *
 * Spring Boot 4-ben a @DataJpaTest csomag helye megváltozott:
 *   org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
 *
 * A @DataJpaTest H2 in-memory adatbázist indít a tesztekhez.
 * A Flyway migrációk ki vannak kapcsolva (test application.properties alapján).
 * A JPA/Hibernate automatikusan létrehozza a táblákat a @Entity osztályokból.
 * Minden teszt tranzakcióban fut és rollback-kel zárul.
 */
@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    // Segédfüggvény: elment egy tesztfelhasználót az adatbázisba
    private User saveTestUser(String username, String role) {
        User user = new User();
        user.setUsername(username);
        user.setPassword("$2a$10$hashelt_jelszo");
        user.setRole(role);
        return userRepository.save(user);
    }

    // -------------------------------------------------------------------------
    // findByUsername – létező felhasználó
    // -------------------------------------------------------------------------

    /**
     * Ha a felhasználó létezik az adatbázisban, findByUsername megtalálja és visszaadja.
     */
    @Test
    void shouldFindExistingUserByUsername() {
        saveTestUser("pelda_user", "ROLE_USER");

        Optional<User> result = userRepository.findByUsername("pelda_user");

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("pelda_user");
    }

    // -------------------------------------------------------------------------
    // findByUsername – nem létező felhasználó
    // -------------------------------------------------------------------------

    /**
     * Ha a felhasználó nem létezik, findByUsername üres Optional-t ad vissza.
     */
    @Test
    void shouldReturnEmpty_whenUserDoesNotExist() {
        Optional<User> result = userRepository.findByUsername("senki");

        assertThat(result).isEmpty();
    }

    // -------------------------------------------------------------------------
    // findByUsername – több felhasználó között a helyes kerül vissza
    // -------------------------------------------------------------------------

    /**
     * Több felhasználó esetén csak a kért felhasználónevet adja vissza.
     */
    @Test
    void shouldReturnCorrectUser_whenMultipleUsersExist() {
        saveTestUser("elso_user", "ROLE_USER");
        saveTestUser("masodik_user", "ROLE_USER");

        Optional<User> result = userRepository.findByUsername("masodik_user");

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("masodik_user");
    }

    // -------------------------------------------------------------------------
    // save – mentés után ID kap értéket
    // -------------------------------------------------------------------------

    /**
     * Mentés után a felhasználó kap egy generált ID-t (nem null).
     */
    @Test
    void shouldAssignIdAfterSave() {
        User user = saveTestUser("id_teszt", "ROLE_USER");

        assertThat(user.getId()).isNotNull();
    }

    // -------------------------------------------------------------------------
    // save – admin szerepkör is elmenthető
    // -------------------------------------------------------------------------

    /**
     * ROLE_ADMIN szerepkörű felhasználó is menthető és visszakereshető.
     */
    @Test
    void shouldSaveAdminRoleUser() {
        saveTestUser("admin_user", "ROLE_ADMIN");

        Optional<User> result = userRepository.findByUsername("admin_user");

        assertThat(result).isPresent();
        assertThat(result.get().getRole()).isEqualTo("ROLE_ADMIN");
    }

    // -------------------------------------------------------------------------
    // deleteById – törlés után nem található
    // -------------------------------------------------------------------------

    /**
     * deleteById után a felhasználó eltűnik az adatbázisból.
     */
    @Test
    void shouldDeleteUserById() {
        User user = saveTestUser("torlendo_user", "ROLE_USER");
        Long id = user.getId();

        userRepository.deleteById(id);

        assertThat(userRepository.findById(id)).isEmpty();
    }
}