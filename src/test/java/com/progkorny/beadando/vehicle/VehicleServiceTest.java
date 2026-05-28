package com.progkorny.beadando.vehicle;

import com.progkorny.beadando.feature.Feature;
import com.progkorny.beadando.feature.FeatureRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

// Mockito-val teszteljük a VehicleService-t, nem kell adatbázis
@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private FeatureRepository featureRepository;

    @InjectMocks
    private VehicleService vehicleService;

    private Vehicle testVehicle;
    private Feature testFeature;

    // Minden teszt előtt létrehozunk egy alap jármű és extra objektumot
    @BeforeEach
    void setUp() {
        testVehicle = new Vehicle();
        testVehicle.setId(1L);
        testVehicle.setName("Teszt Audi");
        testVehicle.setBrand("Audi");
        testVehicle.setStatus(1);
        testVehicle.setKm(50000);
        testVehicle.setYearOfManufacture(2020);
        testVehicle.setFuel("benzin");
        testVehicle.setType("GK");

        testFeature = new Feature();
        testFeature.setId(1L);
        testFeature.setName("Klíma");
    }

    // Ellenőrzi, hogy az aktív járművek lekérése meghívja a repository-t
    @Test
    void shouldReturnActiveVehicles() {
        when(vehicleRepository.findActiveByFilters(null, null, null, null))
                .thenReturn(List.of(testVehicle));

        List<Vehicle> result = vehicleService.getActiveVehicles(null, null, null, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Teszt Audi");
        verify(vehicleRepository).findActiveByFilters(null, null, null, null);
    }

    // Ellenőrzi, hogy üres/null szűrők null-ra normalizálódnak
    @Test
    void shouldNormalizeEmptyFiltersToNull() {
        when(vehicleRepository.findActiveByFilters(null, null, null, null))
                .thenReturn(List.of());

        List<Vehicle> result = vehicleService.getActiveVehicles("", null, null, "");

        assertThat(result).isEmpty();
        // Az üres string null-lá kell alakuljon a service-ben
        verify(vehicleRepository).findActiveByFilters(null, null, null, null);
    }

    // Ellenőrzi, hogy típus és üzemanyag szűrők helyesen átadódnak
    @Test
    void shouldPassFiltersCorrectly() {
        when(vehicleRepository.findActiveByFilters("GK", 100000, 2022, "benzin"))
                .thenReturn(List.of(testVehicle));

        List<Vehicle> result = vehicleService.getActiveVehicles("GK", 100000, 2022, "benzin");

        assertThat(result).hasSize(1);
        verify(vehicleRepository).findActiveByFilters("GK", 100000, 2022, "benzin");
    }

    // Ellenőrzi az összes jármű (extrakkkal együtt) lekérését
    @Test
    void shouldReturnAllVehiclesWithFeatures() {
        when(vehicleRepository.findAllWithFeatures()).thenReturn(List.of(testVehicle));

        List<Vehicle> result = vehicleService.getAllWithFeatures();

        assertThat(result).hasSize(1);
        verify(vehicleRepository).findAllWithFeatures();
    }

    // Ellenőrzi az ID alapú keresést
    @Test
    void shouldReturnVehicleById_whenExists() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(testVehicle));

        Optional<Vehicle> result = vehicleService.getById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
    }

    // Ellenőrzi, hogy nem létező ID esetén üres Optional jön vissza
    @Test
    void shouldReturnEmpty_whenVehicleNotFound() {
        when(vehicleRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Vehicle> result = vehicleService.getById(99L);

        assertThat(result).isEmpty();
    }

    // Ellenőrzi a mentés helyes működését
    @Test
    void shouldSaveVehicle() {
        when(vehicleRepository.save(testVehicle)).thenReturn(testVehicle);

        Vehicle result = vehicleService.save(testVehicle);

        assertThat(result.getName()).isEqualTo("Teszt Audi");
        verify(vehicleRepository).save(testVehicle);
    }

    // Ellenőrzi a törlés meghívását
    @Test
    void shouldDeleteVehicleById() {
        vehicleService.deleteById(1L);
        verify(vehicleRepository).deleteById(1L);
    }

    // Ellenőrzi az összes extra lekérését
    @Test
    void shouldReturnAllFeatures() {
        when(featureRepository.findAll()).thenReturn(List.of(testFeature));

        List<Feature> result = vehicleService.getAllFeatures();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Klíma");
    }

    // Ellenőrzi az extra ID alapú keresését
    @Test
    void shouldReturnFeatureById_whenExists() {
        when(featureRepository.findById(1L)).thenReturn(Optional.of(testFeature));

        Optional<Feature> result = vehicleService.getFeatureById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Klíma");
    }

    // Ellenőrzi az ID-lista alapú extra lekérést
    @Test
    void shouldReturnFeaturesByIds() {
        when(featureRepository.findAllById(Set.of(1L))).thenReturn(List.of(testFeature));

        List<Feature> result = vehicleService.getFeaturesByIds(Set.of(1L));

        assertThat(result).hasSize(1);
    }

    // Ellenőrzi az extra mentését
    @Test
    void shouldSaveFeature() {
        when(featureRepository.save(testFeature)).thenReturn(testFeature);

        Feature result = vehicleService.saveFeature(testFeature);

        assertThat(result.getName()).isEqualTo("Klíma");
        verify(featureRepository).save(testFeature);
    }

    // Ellenőrzi az extra törlésének meghívását
    @Test
    void shouldDeleteFeatureById() {
        vehicleService.deleteFeatureById(1L);
        verify(featureRepository).deleteById(1L);
    }
}