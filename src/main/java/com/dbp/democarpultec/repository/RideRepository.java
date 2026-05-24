package com.dbp.democarpultec.repository;

import com.dbp.democarpultec.model.Ride;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RideRepository extends JpaRepository<Ride, Long> {
    Optional<Ride> findByPublication_Id(Long publicationId);
}
