package com.progkorny.beadando.web;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.progkorny.beadando.feature.Feature;
import com.progkorny.beadando.vehicle.Vehicle;
import com.progkorny.beadando.vehicle.VehicleService;

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
        Set<Long> featureIds = new HashSet<>();
        if (vehicle != null && vehicle.getFeatures() != null) {
            vehicle.getFeatures().forEach(feature -> featureIds.add(feature.getId()));
        }
        if (vehicle != null) {
            vehicle.setFeatureIds(featureIds);
        }
        model.addAttribute("vehicleForm", vehicle == null ? new Vehicle() : vehicle);
        model.addAttribute("features", vehicleService.getAllFeatures());
        return "vehicle-form";
    }

    @PostMapping("/dashboard/vehicles/save")
    public String saveVehicle(Vehicle vehicle,
                              @RequestParam(name = "imageFile", required = false) MultipartFile imageFile) {
        if (imageFile != null && !imageFile.isEmpty()) {
            vehicle.setImgUrl(resolveImageUrl(imageFile));
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

    private String resolveImageUrl(MultipartFile imageFile) {
        String original = imageFile.getOriginalFilename();
        String extension = (original != null && original.contains("."))
                ? original.substring(original.lastIndexOf('.')) : "";
        String fileName = UUID.randomUUID() + extension;
        Path targetDir = Paths.get("src", "main", "resources", "static", "images");

        try {
            Files.createDirectories(targetDir);
            imageFile.transferTo(targetDir.resolve(fileName));
        } catch (IOException ex) {
            throw new IllegalStateException("Kep feltoltes sikertelen", ex);
        }

        return "/images/" + fileName;
    }
}