package com.syndicare.repository;

import com.syndicare.domain.Charge;
import com.syndicare.domain.ChargeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ChargeRepository extends JpaRepository<Charge, Long> {
    List<Charge> findByApartmentId(Long apartmentId);
    List<Charge> findByApartmentOwnerId(Long ownerId);
    List<Charge> findByApartmentBuildingId(Long buildingId);
    List<Charge> findByApartmentIdAndPeriod(Long apartmentId, LocalDate period);
    List<Charge> findByPeriod(LocalDate period);
    List<Charge> findByStatus(ChargeStatus status);
    long countByStatus(ChargeStatus status);
}
