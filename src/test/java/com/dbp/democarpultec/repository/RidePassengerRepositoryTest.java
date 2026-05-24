package com.dbp.democarpultec.repository;

import com.dbp.democarpultec.PostgresContainerTest;
import com.dbp.democarpultec.model.Publication;
import com.dbp.democarpultec.model.Ride;
import com.dbp.democarpultec.model.User;
import com.dbp.democarpultec.model.Vehicle;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class RidePassengerRepositoryTest extends PostgresContainerTest {

    @Autowired
    private RidePassengerRepository ridePassengerRepository;

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private PublicationRepository publicationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Test
    void shouldReturnTrueWhenPassengerExistsInRide() {
        Ride ride = saveRide();
        User passenger = userRepository.save(RepositoryTestFactory.user("ridePassenger"));
        ridePassengerRepository.save(RepositoryTestFactory.ridePassenger(ride, passenger, 1));

        boolean exists = ridePassengerRepository.existsByRide_IdAndPassenger_Id(ride.getId(), passenger.getId());

        assertTrue(exists);
    }

    @Test
    void shouldSumSeatsReservedByRideIdWhenPassengersExist() {
        Ride ride = saveRide();
        User passengerOne = userRepository.save(RepositoryTestFactory.user("seatPassengerOne"));
        User passengerTwo = userRepository.save(RepositoryTestFactory.user("seatPassengerTwo"));
        ridePassengerRepository.save(RepositoryTestFactory.ridePassenger(ride, passengerOne, 1));
        ridePassengerRepository.save(RepositoryTestFactory.ridePassenger(ride, passengerTwo, 2));

        Integer reservedSeats = ridePassengerRepository.sumSeatsReservedByRide_Id(ride.getId());

        assertEquals(3, reservedSeats);
    }

    @Test
    void shouldReturnZeroSeatsWhenRideHasNoPassengers() {
        Ride ride = saveRide();

        Integer reservedSeats = ridePassengerRepository.sumSeatsReservedByRide_Id(ride.getId());

        assertEquals(0, reservedSeats);
    }

    private Ride saveRide() {
        User author = userRepository.save(RepositoryTestFactory.user("passengerRideAuthor"));
        User driver = userRepository.save(RepositoryTestFactory.user("passengerRideDriver"));
        Vehicle vehicle = vehicleRepository.save(RepositoryTestFactory.vehicle(driver, "PAS"));
        Publication publication = publicationRepository.save(RepositoryTestFactory.publication(author));
        return rideRepository.save(RepositoryTestFactory.ride(publication, driver, vehicle));
    }
}
