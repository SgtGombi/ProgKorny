package com.progkorny.beadando.web;

import com.progkorny.beadando.vehicle.Vehicle;
import com.progkorny.beadando.vehicle.VehicleService;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// VehicleController tesztjei – a főoldal ("/") végpontját teszteljük.

@WebMvcTest(VehicleController.class)
@Import({SecurityConfig.class, AppConfig.class})
class VehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // VehicleService mock-olva nem kell valódi adatbázis
    @MockitoBean
    private VehicleService vehicleService;

    // SecurityConfig -> DaoAuthenticationProvider -> UserService
    @MockitoBean
    private com.progkorny.beadando.user.UserService userService;

    // AppConfig-os password encoder
    @MockitoBean
    private PasswordEncoder passwordEncoder;



    //filter nélküli index betölt e
    @Test
    void shouldLoadIndexWithoutFilters() throws Exception {
        when(vehicleService.getActiveVehicles(any(), any(), any(), any()))
                .thenReturn(List.of());

        mockMvc.perform(get("/"))
                .andExpect(status().isOk()) //csekkolja e hogy OK (200as statusz e)
                .andExpect(view().name("index")) //asszertálja hogy ez a view neve
                .andExpect(model().attributeExists("vehicles")); //asszertálja hogy a model tartalmazzal az attributot
    }


    //
    //A "type=GK" szűrőparaméter átkerül a modellbe és a service-nek is átadódik
    @Test
    void shouldPassTypeFilterToServiceAndModel() throws Exception {
        Vehicle gk = new Vehicle();
        gk.setName("Audi A4");
        gk.setType("GK");

        when(vehicleService.getActiveVehicles("GK", null, null, null))
                .thenReturn(List.of(gk));

        mockMvc.perform(get("/").param("type", "GK")) //a GK tipusnak léteznie kell
                .andExpect(status().isOk()) //200as statusz
                .andExpect(model().attribute("type", "GK"));

        // Ellenőrizzük, hogy a service-t helyes paraméterekkel hívták meg
        verify(vehicleService).getActiveVehicles("GK", null, null, null);
    }

    // -------------------------------------------------------------------------
    // GET / – több szűrő egyszerre
    // -------------------------------------------------------------------------

    //több filter is müködik, mindkettő alkalmazhato
    @Test
    void shouldHandleMultipleFiltersMaxKmAndFuel() throws Exception {
        when(vehicleService.getActiveVehicles(any(), any(), any(), any()))
                .thenReturn(List.of());

        mockMvc.perform(get("/")
                        .param("maxKm", "100000")
                        .param("fuel", "benzin"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("maxKm", "100000"))
                .andExpect(model().attribute("fuel", "benzin"));
    }


     //Ha a maxKm értéke nem szám pl. "abc" a controller nem dob kivételt –
     // null-ként kezeli és az oldal simán betöltődi.
    @Test
    void shouldNotThrowExceptionForInvalidMaxKm() throws Exception {
        when(vehicleService.getActiveVehicles(any(), any(), any(), any()))
                .thenReturn(List.of());

        mockMvc.perform(get("/").param("maxKm", "nemszam"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }


    // ha nincs egyező jűrmü, attól még 200as válasz jön, csak üres a lista
    @Test
    void shouldReturnEmptyVehicleListWhenNoResults() throws Exception {
        when(vehicleService.getActiveVehicles(any(), any(), any(), any()))
                .thenReturn(List.of());

        mockMvc.perform(get("/").param("type", "MOTOR"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("vehicles", List.of()));
    }
}