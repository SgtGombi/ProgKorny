package com.progkorny.beadando.vehicle;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public List<Vehicle> getActiveVehicles(String type, Integer maxKm, Integer maxYear, String fuel) {
        String normalizedFuel = (fuel == null || fuel.isBlank()) ? null : fuel.trim();
        String normalizedType = (type == null || type.isBlank()) ? null : type.trim();
        return vehicleRepository.findActiveByFilters(normalizedType, maxKm, maxYear, normalizedFuel);
    }
}
