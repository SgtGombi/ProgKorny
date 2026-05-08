package com.progkorny.beadando.vehicle;

import java.util.List;
import java.util.Optional;

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

    public List<Vehicle> getAllWithFeatures() {
        return vehicleRepository.findAllWithFeatures();
    }

    public Optional<Vehicle> getById(Long id) {
        return vehicleRepository.findById(id);
    }

    public Vehicle getByIdWithFeatures(Long id) {
        return vehicleRepository.findByIdWithFeatures(id);
    }

    public Vehicle save(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    public void deleteById(Long id) {
        vehicleRepository.deleteById(id);
    }
}
