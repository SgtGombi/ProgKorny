package com.progkorny.beadando.web;

import com.progkorny.beadando.feature.Feature;
import com.progkorny.beadando.vehicle.VehicleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
}