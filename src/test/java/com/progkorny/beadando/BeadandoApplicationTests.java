package com.progkorny.beadando;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Alkalmazás-indítási teszt (smoke test).
 *
 * Ellenőrzi, hogy a teljes Spring kontextus sikeresen betölthető-e.
 * Ha valamelyik bean konfigurációja hibás, ez a teszt azonnal megbukik.
 *
 * A "test" profil aktiválja a src/test/resources/application.properties fájlt,
 * amely H2 in-memory adatbázist és letiltott Flyway-t konfigurál.
 */
@SpringBootTest
@ActiveProfiles("test")
class BeadandoApplicationTests {

    /**
     * Ha a Spring kontextus sikeresen elindul, a teszt átmegy.
     * Nincs szükség assertre – maga az indítás az ellenőrzés.
     */
    @Test
    void contextLoads() {
        // A kontextus betöltése a teszt – ha idáig eljut, minden rendben van
    }
}
