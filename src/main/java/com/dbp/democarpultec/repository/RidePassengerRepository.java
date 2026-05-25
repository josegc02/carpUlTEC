package com.dbp.democarpultec.repository;

import com.dbp.democarpultec.model.RidePassenger;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RidePassengerRepository extends JpaRepository<RidePassenger, Long> {
    boolean existsByRide_IdAndPassenger_Id(Long rideId, Long passengerId);

    @Query("select coalesce(sum(rp.seatsReserved), 0) from RidePassenger rp where rp.ride.id = :rideId")
    Integer sumSeatsReservedByRide_Id(Long rideId);
}
