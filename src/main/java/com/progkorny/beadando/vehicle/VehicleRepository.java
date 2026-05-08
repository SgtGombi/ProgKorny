package com.progkorny.beadando.vehicle;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
	List<Vehicle> findByStatus(Integer status);

	List<Vehicle> findByStatusAndNameContainingIgnoreCase(Integer status, String name);

	@Query("""
			select distinct v
			from Vehicle v
			left join fetch v.features f
			where v.status = 1
			and (:type is null or v.type = :type)
			and (:maxKm is null or v.km <= :maxKm)
			and (:maxYear is null or v.yearOfManufacture <= :maxYear)
			and (:fuel is null or v.fuel = :fuel)
		""")
	List<Vehicle> findActiveByFilters(
			@Param("type") String type,
			@Param("maxKm") Integer maxKm,
			@Param("maxYear") Integer maxYear,
			@Param("fuel") String fuel
	);

	@Query("""
			select distinct v
			from Vehicle v
			left join fetch v.features f
		""")
	List<Vehicle> findAllWithFeatures();

	@Query("""
			select v
			from Vehicle v
			left join fetch v.features f
			where v.id = :id
		""")
	Vehicle findByIdWithFeatures(@Param("id") Long id);
}
