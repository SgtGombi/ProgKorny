package com.progkorny.beadando;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

//Alkalamzás indítási teszt, betölti a teszt application.propertiest, ami H2-t használ és nem SQl , a flyway tiltva van.
@SpringBootTest
@ActiveProfiles("test")
class BeadandoApplicationTests {

    @Test
    void contextLoads() {
        // A kontextus betöltése a teszt – ha idáig eljut, minden rendben van
    }
}
