package com.progkorny.beadando.web;

import com.progkorny.beadando.user.User;
import com.progkorny.beadando.user.UserRepository;
import com.progkorny.beadando.vehicle.Vehicle;
import com.progkorny.beadando.vehicle.VehicleService;
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

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VehicleService vehicleService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private Authentication authentication;

    private User testUser;

    @BeforeEach
    void setup() {
        testUser = new User();
        testUser.setUsername("john");

        when(authentication.getName()).thenReturn("john");
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(testUser));
    }

    @Test
    void testSaveVehicleSuccess() throws Exception {
        when(vehicleService.getFeaturesByIds(any())).thenReturn(List.of());

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
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/?submitted"));

        ArgumentCaptor<Vehicle> captor = ArgumentCaptor.forClass(Vehicle.class);
        verify(vehicleService).save(captor.capture());

        Vehicle saved = captor.getValue();

        assertEquals(testUser, saved.getSeller());
        assertEquals(1, saved.getStatus());
    }

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