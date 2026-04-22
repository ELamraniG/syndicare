package com.syndicare.repositories.buildings;

import com.syndicare.domain.entities.buildings.Apartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApartmentRepository extends JpaRepository<Apartment, Long> {
    List<Apartment> findByBuildingId(Long buildingId);
    List<Apartment> findByOwnerId(Long ownerId);
    List<Apartment> findByResidentId(Long residentId);
}
