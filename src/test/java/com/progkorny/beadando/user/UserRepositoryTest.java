package com.progkorny.beadando.user;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


//A @DataJpaTest H2 in-memory adatbázist indít a tesztekhez.
 //A Flyway migrációk ki vannak kapcsolva (test application.properties alapján).//
// // Hibernate automatikusan létrehozza a táblákat a @Entity osztályokból.

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


  // ha username létezik, a findByusername megtalálja és visszaadja
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

    //Ha a felhasználó nem létezik, findByUsername üres Optional-t ad vissza.
    @Test
    void shouldReturnEmpty_whenUserDoesNotExist() {
        Optional<User> result = userRepository.findByUsername("senki");

        assertThat(result).isEmpty();
    }


    //több felhasználó esetén adja azt amelyiket kérjük
    @Test
    void shouldReturnCorrectUser_whenMultipleUsersExist() {
        saveTestUser("elso_user", "ROLE_USER");
        saveTestUser("masodik_user", "ROLE_USER");

        Optional<User> result = userRepository.findByUsername("masodik_user");

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("masodik_user");
    }


    //
    // Mentés után a felhasználó kap egy generált ID-t (nem null).
    @Test
    void shouldAssignIdAfterSave() {
        User user = saveTestUser("id_teszt", "ROLE_USER");

        assertThat(user.getId()).isNotNull();
    }


    // ROLE_ADMIN  felhasználó is menthető és visszakereshető.
    @Test
    void shouldSaveAdminRoleUser() {
        saveTestUser("admin_user", "ROLE_ADMIN");

        Optional<User> result = userRepository.findByUsername("admin_user");

        assertThat(result).isPresent();
        assertThat(result.get().getRole()).isEqualTo("ROLE_ADMIN");
    }


    // deleteById tényleg töröl
    @Test
    void shouldDeleteUserById() {
        User user = saveTestUser("torlendo_user", "ROLE_USER");
        Long id = user.getId();

        userRepository.deleteById(id);

        assertThat(userRepository.findById(id)).isEmpty();
    }
}