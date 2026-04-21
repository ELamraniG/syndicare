package com.syndicare.repositories.buildings;

import com.syndicare.domain.entities.buildings.Building;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BuildingRepository extends JpaRepository<Building, Long> {
}
