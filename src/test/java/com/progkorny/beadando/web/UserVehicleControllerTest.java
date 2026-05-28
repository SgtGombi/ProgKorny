package com.progkorny.beadando.web;

import com.progkorny.beadando.user.User;
import com.progkorny.beadando.user.UserRepository;
import com.progkorny.beadando.vehicle.Vehicle;
import com.progkorny.beadando.vehicle.VehicleService;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserVehicleController.class)
class UserVehicleControllerTest {

    // ez a USerVehicleControllert ellenőrzi. A felhasználóként való mentés esetén helyesen állítjuk e be az adatokat és
    // és az állapotok validak e
    //A MockMvc segítségével HTTP kéréseket tudunk szimulálni
    // anélkül, hogy valódi webszervert kellene indítani.

    //A VehicleService mockolt verziója.
    @Autowired
    private MockMvc mockMvc;


    @MockitoBean
    private VehicleService vehicleService;

    @MockitoBean
    private UserRepository userRepository;

    //Spring Security mockolt authenticationje, igy szimulálva a bejelentkezett felhasználó
    @MockitoBean
    private Authentication authentication;

    private User testUser;

    // MINDEN TESZT ELŐTT LE KELL FUTNIA MERT BEÁLLÍTJA A TESZTFELHASZNÁLÓT
    @BeforeEach
    void setup() {
        testUser = new User();
        testUser.setUsername("janos");

        when(authentication.getName()).thenReturn("janos");
        // ha a repóban rákeresünk, hozza vissza a beállított adatokat.
        when(userRepository.findByUsername("janos")).thenReturn(Optional.of(testUser));
    }


    //képfájl és vehicle mentése eegyaránt
    @Test
    void testSaveVehicleSuccess() throws Exception {
        //featurelista legyen üres
        when(vehicleService.getFeaturesByIds(any())).thenReturn(List.of());

        //egy képzeletbeli képfájl
        MockMultipartFile image = new MockMultipartFile(
                "imageFile",
                "car.jpg",
                "image/jpeg",
                "fake-image-content".getBytes()
        );


        mockMvc.perform(multipart("/vehicles/save")
                        .file(image)
                        .param("featureIds", "1", "2")
                        .flashAttr("vehicle", new Vehicle())
                        .principal(authentication))
                // Ellenőrizzük, hogy átirányítás történt-e. , beépített metódus
                .andExpect(status().is3xxRedirection())
                // Ellenőrizzük az hogy jó helyre irányított e ?
                .andExpect(redirectedUrl("/?submitted"));

        //argumentcaptor elfogja a vehiclet objektumot a controller és a service között
        ArgumentCaptor<Vehicle> captor = ArgumentCaptor.forClass(Vehicle.class);
        // Ellenőrizzük, hogy a save metódus meghívódott-e.
        verify(vehicleService).save(captor.capture());
        // Lekérjük az elmenteni kívánt Vehicle objektumot.
        Vehicle saved = captor.getValue();
        //Ellenőrizzük, hogy a seller mező megfelelően lett-e beállítva.
        assertEquals(testUser, saved.getSeller());
        assertEquals(1, saved.getStatus());
    }

    //képes bohóckodás nélküli mentés ellenőrzése
    @Test
    void testSaveVehicleWithoutImage() throws Exception {
        when(vehicleService.getFeaturesByIds(any())).thenReturn(List.of());

        mockMvc.perform(post("/vehicles/save")
                        .param("featureIds", "1")
                        .flashAttr("vehicle", new Vehicle())
                        .principal(authentication))
                .andExpect(status().is3xxRedirection());

        verify(vehicleService).save(any(Vehicle.class));
    }
}