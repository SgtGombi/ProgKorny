package com.progkorny.beadando.web;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.progkorny.beadando.feature.Feature;
import com.progkorny.beadando.user.User;
import com.progkorny.beadando.user.UserRepository;
import com.progkorny.beadando.vehicle.Vehicle;
import com.progkorny.beadando.vehicle.VehicleService;

@Controller
@RequestMapping("/")
public class UserVehicleController {

    private final VehicleService vehicleService;
    private final UserRepository userRepository;

    public UserVehicleController(VehicleService vehicleService, UserRepository userRepository) {
        this.vehicleService = vehicleService;
        this.userRepository = userRepository;
    }

    @GetMapping("/vehicles/new")
    public String newVehicle(Model model) {
        model.addAttribute("vehicleForm", new Vehicle());
        model.addAttribute("features", vehicleService.getAllFeatures());
        return "vehicle-form";
    }

    @PostMapping("/vehicles/save")
    public String saveVehicle(Vehicle vehicle,
                              @RequestParam(name = "imageFile", required = false) MultipartFile imageFile,
                              Authentication auth) {
        User seller = userRepository.findByUsername(auth.getName()).orElseThrow();
        vehicle.setSeller(seller);
        vehicle.setStatus(1);
        vehicle.setImgUrl(resolveImageUrl(imageFile));
        Set<Long> featureIds = vehicle.getFeatureIds() == null ? new HashSet<>() : vehicle.getFeatureIds();
        List<Feature> selected = vehicleService.getFeaturesByIds(featureIds);
        vehicle.setFeatures(new HashSet<>(selected));
        vehicleService.save(vehicle);
        return "redirect:/?submitted";
    }

    private String resolveImageUrl(MultipartFile imageFile) {
        if (imageFile == null || imageFile.isEmpty()) return null;

        String original = imageFile.getOriginalFilename();
        String extension = (original != null && original.contains("."))
                ? original.substring(original.lastIndexOf('.')) : "";
        String fileName = UUID.randomUUID() + extension;
        Path targetDir = Paths.get("src", "main", "resources", "static", "images");

        try {
            Files.createDirectories(targetDir);
            imageFile.transferTo(targetDir.resolve(fileName));
        } catch (IOException ex) {
            throw new IllegalStateException("Kép feltöltés sikertelen", ex);
        }

        return "/images/" + fileName;
    }
}