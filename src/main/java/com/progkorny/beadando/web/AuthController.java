package com.progkorny.beadando.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.progkorny.beadando.user.UserService;

@Controller
public class AuthController {

    // userservice objektum letrehozasa
    private final UserService userService;
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }
    // kezeli a POST reget
    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           Model model) {
        if (userService.existsByUsername(username)) {
            model.addAttribute("error", "Ez a felhasználónév már foglalt.");
            return "register";
        }
        userService.register(username, password);
        return "redirect:/login?registered";
    }

    @GetMapping("/403")
    public String accessDenied() {
        return "403";
    }
}