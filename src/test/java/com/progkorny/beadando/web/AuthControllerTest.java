package com.progkorny.beadando.web;

import com.progkorny.beadando.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


 //A SecurityConfig-ot explicit @Import-tal kell behúzni, mert a @WebMvcTest
 // alapból nem tölti be az egyedi @Configuration osztályokat.
 //
 //A SecurityConfig függ a UserService-től és a PasswordEncoder-től,
 // ezért mindkettőt @MockitoBean-nel kell pótolni.

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, AppConfig.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // SecurityConfig -> DaoAuthenticationProvider -> UserService
    @MockitoBean
    private UserService userService;

    // AppConfig-ban van definiálva a PasswordEncoder bean
    @MockitoBean
    private PasswordEncoder passwordEncoder;



   //loginhoz nem kell bejelentkezés, és megfelelő tempaltet ad vissza
    @Test
    void shouldReturnLoginView() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk()) // HTTP 200 ok , van acess
                .andExpect(view().name("login"));
    }


     //A regisztrációs oldal szintén nyilvánosan elérhető,
     //és a "register" template-et rendereli.
    @Test
    void shouldReturnRegisterView() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }


    //ha registered , akkor átrányít a loginre
    @Test
    void shouldRedirectToLoginAfterSuccessfulRegistration() throws Exception {
        when(userService.existsByUsername("ujuser")).thenReturn(false);
        doNothing().when(userService).register(anyString(), anyString());

        mockMvc.perform(post("/register")
                        .param("username", "ujuser")
                        .param("password", "Jelszo123")
                        .with(csrf()))                        // CSRF token szükséges Spring Security miatt
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registered"));
    }



     //Ha a felhasználónév már foglalt, a szerver visszaküldi a register oldalt
     //és az "error" modell attribútum jelen van.
    @Test
    void shouldShowErrorWhenUsernameAlreadyExists() throws Exception {
        when(userService.existsByUsername("letezousr")).thenReturn(true);

        mockMvc.perform(post("/register")
                        .param("username", "letezousr")
                        .param("password", "barmijelszo")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("error")); // Hibaüzenet a modellben
    }

}