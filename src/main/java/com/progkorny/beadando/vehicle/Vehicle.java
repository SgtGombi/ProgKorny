package com.progkorny.beadando.vehicle;

import com.progkorny.beadando.feature.Feature;
import com.progkorny.beadando.user.User;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 60)
    private String brand;

    @Column(name = "plate_number", nullable = false, length = 15)
    private String plateNumber;

    @Column(nullable = false, length = 10)
    private String type;

    @Column(name = "img_url", length = 255)
    private String imgUrl;

    @Column(nullable = false)
    private Integer km;

    @Column(name = "year_of_manufacture", nullable = false)
    private Integer yearOfManufacture;

    @Column(nullable = false, length = 40)
    private String color;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false, length = 12)
    private String fuel;

    @Column(name = "condition_status", nullable = false, length = 15)
    private String conditionStatus;

    @Column(nullable = false)
    private Integer status;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User seller;

    @ManyToMany
    @JoinTable(
            name = "vehicle_features",
            joinColumns = @JoinColumn(name = "vehicle_id"),
            inverseJoinColumns = @JoinColumn(name = "feature_id")
    )
    private Set<Feature> features = new HashSet<>();

    @Transient
    private Set<Long> featureIds = new HashSet<>();
}