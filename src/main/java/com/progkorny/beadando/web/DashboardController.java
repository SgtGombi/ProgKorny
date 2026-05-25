package com.progkorny.beadando.web;

import com.progkorny.beadando.feature.Feature;
import com.progkorny.beadando.vehicle.Vehicle;
import com.progkorny.beadando.vehicle.VehicleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Controller
public class DashboardController {

    private final VehicleService vehicleService;

    public DashboardController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("vehicles", vehicleService.getAllWithFeatures());
        model.addAttribute("features", vehicleService.getAllFeatures());
        return "dashboard";
    }

    @GetMapping("/dashboard/vehicles/new")
    public String newVehicle(Model model) {
        model.addAttribute("vehicleForm", new Vehicle());
        model.addAttribute("features", vehicleService.getAllFeatures());
        return "vehicle-form";
    }

    @GetMapping("/dashboard/vehicles/{id}/edit")
    public String editVehicle(@PathVariable Long id, Model model) {
        Vehicle vehicle = vehicleService.getByIdWithFeatures(id);
        Set<Long> ids = new HashSet<>();
        for (Feature f : vehicle.getFeatures()) ids.add(f.getId());
        vehicle.setFeatureIds(ids);
        model.addAttribute("vehicleForm", vehicle);
        model.addAttribute("features", vehicleService.getAllFeatures());
        return "vehicle-form";
    }

    @PostMapping("/dashboard/vehicles/save")
    public String saveVehicle(Vehicle vehicle,
                              @RequestParam(name = "imageFile", required = false) MultipartFile imageFile) {
        if (vehicle.getId() != null && (imageFile == null || imageFile.isEmpty())) {
            vehicleService.getById(vehicle.getId())
                    .ifPresent(existing -> vehicle.setImgUrl(existing.getImgUrl()));
        } else {
            vehicle.setImgUrl(resolveImageUrl(vehicle.getImgUrl(), imageFile));
        }
        Set<Long> featureIds = vehicle.getFeatureIds() == null ? new HashSet<>() : vehicle.getFeatureIds();
        List<Feature> selected = vehicleService.getFeaturesByIds(featureIds);
        vehicle.setFeatures(new HashSet<>(selected));
        vehicleService.save(vehicle);
        return "redirect:/dashboard";
    }

    @PostMapping("/dashboard/vehicles/{id}/delete")
    public String deleteVehicle(@PathVariable Long id) {
        vehicleService.deleteById(id);
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard/features/new")
    public String newFeature(Model model) {
        model.addAttribute("feature", new Feature());
        return "feature-form";
    }

    @GetMapping("/dashboard/features/{id}/edit")
    public String editFeature(@PathVariable Long id, Model model) {
        Feature feature = vehicleService.getFeatureById(id).orElseGet(Feature::new);
        model.addAttribute("feature", feature);
        return "feature-form";
    }

    @PostMapping("/dashboard/features/save")
    public String saveFeature(Feature feature) {
        vehicleService.saveFeature(feature);
        return "redirect:/dashboard";
    }

    @PostMapping("/dashboard/features/{id}/delete")
    public String deleteFeature(@PathVariable Long id) {
        vehicleService.deleteFeatureById(id);
        return "redirect:/dashboard";
    }

    private String resolveImageUrl(String existingUrl, MultipartFile imageFile) {
        if (imageFile == null || imageFile.isEmpty()) {
            return existingUrl;
        }
        String original = imageFile.getOriginalFilename();
        String extension = "";
        if (original != null && original.contains(".")) {
            extension = original.substring(original.lastIndexOf('.'));
        }
        String fileName = UUID.randomUUID() + extension;
        Path targetDir = Paths.get("src", "main", "resources", "static", "images");
        try {
            Files.createDirectories(targetDir);
            Path targetFile = targetDir.resolve(fileName);
            imageFile.transferTo(targetFile);
        } catch (IOException ex) {
            throw new IllegalStateException("Kep feltoltes sikertelen", ex);
        }
        return "/images/" + fileName;
    }
}