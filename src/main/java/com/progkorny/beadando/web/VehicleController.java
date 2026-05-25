package com.progkorny.beadando.web;

import java.util.List;

import com.progkorny.beadando.vehicle.Vehicle;
import com.progkorny.beadando.vehicle.VehicleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller

public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping("/")
    public String index(
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "maxKm", required = false) String maxKm,
            @RequestParam(name = "maxYear", required = false) String maxYear,
            @RequestParam(name = "fuel", required = false) String fuel,
            Model model) {
        Integer maxKmValue = parseInteger(maxKm);
        Integer maxYearValue = parseInteger(maxYear);
        List<Vehicle> vehicles = vehicleService.getActiveVehicles(type, maxKmValue, maxYearValue, fuel);
        model.addAttribute("vehicles", vehicles);
        model.addAttribute("type", type == null ? "" : type);
        model.addAttribute("maxKm", maxKm == null ? "" : maxKm);
        model.addAttribute("maxYear", maxYear == null ? "" : maxYear);
        model.addAttribute("fuel", fuel == null ? "" : fuel);
        return "index";
    }

    private Integer parseInteger(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Integer.valueOf(value.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
