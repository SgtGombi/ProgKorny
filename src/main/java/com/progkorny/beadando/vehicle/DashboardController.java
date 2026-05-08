package com.progkorny.beadando.vehicle;

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

@Controller
public class DashboardController {

    private final VehicleService vehicleService;
    private final FeatureService featureService;

    public DashboardController(VehicleService vehicleService, FeatureService featureService) {
        this.vehicleService = vehicleService;
        this.featureService = featureService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("vehicles", vehicleService.getAllWithFeatures());
        model.addAttribute("features", featureService.getAll());
        return "dashboard";
    }

    @GetMapping("/dashboard/vehicles/new")
    public String newVehicle(Model model) {
        model.addAttribute("vehicleForm", new VehicleForm());
        model.addAttribute("features", featureService.getAll());
        return "vehicle-form";
    }

    @GetMapping("/dashboard/vehicles/{id}/edit")
    public String editVehicle(@PathVariable Long id, Model model) {
        Vehicle vehicle = vehicleService.getByIdWithFeatures(id);
        VehicleForm form = toForm(vehicle);
        model.addAttribute("vehicleForm", form);
        model.addAttribute("features", featureService.getAll());
        return "vehicle-form";
    }

    @PostMapping("/dashboard/vehicles/save")
    public String saveVehicle(VehicleForm form, @RequestParam(name = "imageFile", required = false) MultipartFile imageFile) {
        Vehicle vehicle = form.getId() == null ? new Vehicle() : vehicleService.getById(form.getId()).orElseGet(Vehicle::new);
        vehicle.setName(form.getName());
        vehicle.setBrand(form.getBrand());
        vehicle.setPlateNumber(form.getPlateNumber());
        vehicle.setType(form.getType());
        vehicle.setImgUrl(resolveImageUrl(form.getImgUrl(), imageFile));
        vehicle.setKm(form.getKm());
        vehicle.setYearOfManufacture(form.getYearOfManufacture());
        vehicle.setColor(form.getColor());
        vehicle.setPrice(form.getPrice());
        vehicle.setFuel(form.getFuel());
        vehicle.setConditionStatus(form.getConditionStatus());
        vehicle.setStatus(form.getStatus());
        vehicle.setDescription(form.getDescription());
        Set<Long> featureIds = form.getFeatureIds() == null ? new HashSet<>() : form.getFeatureIds();
        List<Feature> selected = featureService.getByIds(featureIds);
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
        Feature feature = featureService.getById(id).orElseGet(Feature::new);
        model.addAttribute("feature", feature);
        return "feature-form";
    }

    @PostMapping("/dashboard/features/save")
    public String saveFeature(Feature feature) {
        featureService.save(feature);
        return "redirect:/dashboard";
    }

    @PostMapping("/dashboard/features/{id}/delete")
    public String deleteFeature(@PathVariable Long id) {
        featureService.deleteById(id);
        return "redirect:/dashboard";
    }

    private VehicleForm toForm(Vehicle vehicle) {
        VehicleForm form = new VehicleForm();
        form.setId(vehicle.getId());
        form.setName(vehicle.getName());
        form.setBrand(vehicle.getBrand());
        form.setPlateNumber(vehicle.getPlateNumber());
        form.setType(vehicle.getType());
        form.setImgUrl(vehicle.getImgUrl());
        form.setKm(vehicle.getKm());
        form.setYearOfManufacture(vehicle.getYearOfManufacture());
        form.setColor(vehicle.getColor());
        form.setPrice(vehicle.getPrice());
        form.setFuel(vehicle.getFuel());
        form.setConditionStatus(vehicle.getConditionStatus());
        form.setStatus(vehicle.getStatus());
        form.setDescription(vehicle.getDescription());
        Set<Long> featureIds = new HashSet<>();
        if (vehicle.getFeatures() != null) {
            for (Feature feature : vehicle.getFeatures()) {
                featureIds.add(feature.getId());
            }
        }
        form.setFeatureIds(featureIds);
        return form;
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
