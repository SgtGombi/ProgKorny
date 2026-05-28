package com.progkorny.beadando.feature;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@ActiveProfiles("test")
class FeatureRepositoryTest {

    @Autowired
    private FeatureRepository featureRepository;

    // Segédfüggvény: elment egy Feature-t az adatbázisba
    private Feature saveFeature(String name) {
        Feature feature = new Feature();
        feature.setName(name);
        return featureRepository.save(feature);
    }


    // Mentett Feature generált ID-t kap – nem null és pozitív
    @Test
    void shouldAssignIdAfterSave() {
        Feature feature = saveFeature("Klíma");

        assertThat(feature.getId()).isNotNull();
        assertThat(feature.getId()).isPositive();
    }

    // -------------------------------------------------------------------------
    // findById – visszakeresi a mentett elemet
    // -------------------------------------------------------------------------

    /**
     * findById megtalálja a mentett Feature-t és a neve helyes.
     */
    @Test
    void shouldFindSavedFeatureById() {
        Feature feature = saveFeature("ABS");

        Optional<Feature> result = featureRepository.findById(feature.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("ABS");
    }


    //Nem létező ID esetén findById üres Optional-t ad vissza.
    @Test
    void shouldReturnEmpty_whenFeatureNotFound() {
        Optional<Feature> result = featureRepository.findById(9999L);

        assertThat(result).isEmpty();
    }

    //visszaadja az összes mentett featuret
    @Test
    void shouldReturnAllFeatures() {
        saveFeature("Tempomat");
        saveFeature("Bőrülés");
        saveFeature("Navigáció");

        List<Feature> all = featureRepository.findAll();

        assertThat(all).hasSizeGreaterThanOrEqualTo(3);
    }

    //deleteById után az elem nem található findById-dal sem.

    @Test
    void shouldDeleteFeatureById() {
        Feature feature = saveFeature("Panoramateto");
        Long id = feature.getId();

        featureRepository.deleteById(id);

        assertThat(featureRepository.findById(id)).isEmpty();
    }


    //findAllById egyszerre több ID alapján adja vissza a Featureöket.
    // Ezt a VehicleService is használja feature éist összeállításho.
    @Test
    void shouldFindAllByIds() {
        Feature f1 = saveFeature("Vonóhorog");
        Feature f2 = saveFeature("Riasztó");

        List<Feature> result = featureRepository.findAllById(List.of(f1.getId(), f2.getId()));

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Feature::getName)
                .containsExactlyInAnyOrder("Vonóhorog", "Riasztó");
    }
}