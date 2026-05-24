package com.dbp.democarpultec.repository;

import com.dbp.democarpultec.model.*;
import com.dbp.democarpultec.model.enums.Carreras;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class RideRepositoryTest extends BaseRepositoryTest {
    @Autowired private RideRepository rideRepository;
    @Autowired private PublicationRepository publicationRepository;
    @Autowired private VehicleRepository vehicleRepository;
    @Autowired private UserRepository userRepository;

    private User driver;
    private Vehicle vehicle;
    private Publication publication;
    private Ride ride;
    private final LocalDateTime departureTime = LocalDateTime.of(2025, 6, 1, 7, 30);

    @BeforeEach
    void setUp() {
        rideRepository.deleteAll();
        publicationRepository.deleteAll();
        vehicleRepository.deleteAll();
        userRepository.deleteAll();

        driver = userRepository.save(User.builder()
                .name("Juan").lastName("Perez")
                .email("juan.perez@utec.edu.pe")
                .phone("111111111").studentCode("202410001")
                .career(Carreras.Ciencia_de_la_Computacion)
                .cycle(5).rating(4.5)
                .build());

        vehicle = vehicleRepository.save(Vehicle.builder()
                .owner(driver).plate("ABC-123")
                .brand("Toyota").model("Corolla")
                .color("Blanco").seats(4)
                .build());

        publication = publicationRepository.save(Publication.builder()
                .fromUTEC(true).driverToPassenger(true)
                .seats(3).titulo("Viaje a Miraflores")
                .destinationOrOrigin("Miraflores")
                .departureTime(departureTime)
                .author(driver)
                .build());

        ride = Ride.builder()
                .publication(publication)
                .driver(driver)
                .vehicle(vehicle)
                .fromUTEC(true)
                .destinationOrOrigin("Miraflores")
                .departureTime(departureTime)
                .build();
    }

    @Test
    void shouldSaveRideWhenValidData() {
        Ride saved = rideRepository.save(ride);

        assertNotNull(saved.getId());
        assertEquals("Miraflores", saved.getDestinationOrOrigin());
        assertTrue(saved.getFromUTEC());
    }

    @Test
    void shouldGenerateIdAutomaticallyWhenRideIsSaved() {
        Ride saved = rideRepository.save(ride);

        assertNotNull(saved.getId());
        assertTrue(saved.getId() > 0);
    }

    @Test
    void shouldSaveRideWithDriverAndVehicleWhenBothExist() {
        Ride saved = rideRepository.save(ride);

        assertEquals(driver.getId(), saved.getDriver().getId());
        assertEquals(vehicle.getId(), saved.getVehicle().getId());
        assertEquals(publication.getId(), saved.getPublication().getId());
    }

    @Test
    void shouldReturnRideWhenIdExists() {
        Ride saved = rideRepository.save(ride);

        Optional<Ride> result = rideRepository.findById(saved.getId());

        assertTrue(result.isPresent());
        assertEquals("Miraflores", result.get().getDestinationOrOrigin());
        assertTrue(result.get().getFromUTEC());
    }

    @Test
    void shouldReturnEmptyWhenIdDoesNotExist() {
        Optional<Ride> result = rideRepository.findById(999L);

        assertFalse(result.isPresent());
    }

    @Test
    void shouldReturnRideWhenPublicationIdExists() {
        Ride saved = rideRepository.save(ride);

        Optional<Ride> result = rideRepository.findByPublication_Id(publication.getId());

        assertTrue(result.isPresent());
        assertEquals(saved.getId(), result.get().getId());
        assertEquals("Miraflores", result.get().getDestinationOrOrigin());
    }

    @Test
    void shouldReturnEmptyWhenPublicationIdDoesNotExist() {
        Optional<Ride> result = rideRepository.findByPublication_Id(999L);

        assertFalse(result.isPresent());
    }

    @Test
    void shouldReturnCorrectRideWhenMultipleRidesExistAndPublicationMatches() {
        rideRepository.save(ride);

        Publication publication2 = publicationRepository.save(Publication.builder()
                .fromUTEC(false).driverToPassenger(true)
                .seats(2).titulo("Viaje a San Isidro")
                .destinationOrOrigin("San Isidro")
                .departureTime(departureTime)
                .author(driver)
                .build());

        Ride ride2 = rideRepository.save(Ride.builder()
                .publication(publication2)
                .driver(driver).vehicle(vehicle)
                .fromUTEC(false)
                .destinationOrOrigin("San Isidro")
                .departureTime(departureTime)
                .build());

        Optional<Ride> result = rideRepository.findByPublication_Id(publication2.getId());

        assertTrue(result.isPresent());
        assertEquals(ride2.getId(), result.get().getId());
        assertEquals("San Isidro", result.get().getDestinationOrOrigin());
    }

    @Test
    void shouldReturnAllRidesWhenMultipleRidesExist() {
        rideRepository.save(ride);

        Publication publication2 = publicationRepository.save(Publication.builder()
                .fromUTEC(false).driverToPassenger(true)
                .seats(2).titulo("Viaje a San Isidro")
                .destinationOrOrigin("San Isidro")
                .departureTime(departureTime)
                .author(driver)
                .build());

        rideRepository.save(Ride.builder()
                .publication(publication2)
                .driver(driver).vehicle(vehicle)
                .fromUTEC(false)
                .destinationOrOrigin("San Isidro")
                .departureTime(departureTime)
                .build());

        List<Ride> result = rideRepository.findAll();

        assertEquals(2, result.size());
    }

    @Test
    void shouldReturnEmptyListWhenNoRidesExist() {
        assertTrue(rideRepository.findAll().isEmpty());
    }

    @Test
    void shouldUpdateRideWhenRideExists() {
        Ride saved = rideRepository.save(ride);

        saved.setDestinationOrOrigin("Surco");
        saved.setFromUTEC(false);
        Ride updated = rideRepository.save(saved);

        assertEquals("Surco", updated.getDestinationOrOrigin());
        assertFalse(updated.getFromUTEC());
        assertEquals(saved.getId(), updated.getId());
    }

    @Test
    void shouldDeleteRideWhenRideExists() {
        Ride saved = rideRepository.save(ride);
        Long id = saved.getId();

        rideRepository.deleteById(id);

        assertFalse(rideRepository.findById(id).isPresent());
    }

    @Test
    void shouldNotFailWhenDeletingAllRides() {
        rideRepository.save(ride);

        rideRepository.deleteAll();

        assertEquals(0, rideRepository.count());
    }

    @Test
    void shouldReturnTrueWhenRideExists() {
        Ride saved = rideRepository.save(ride);

        assertTrue(rideRepository.existsById(saved.getId()));
    }

    @Test
    void shouldReturnFalseWhenRideDoesNotExist() {
        assertFalse(rideRepository.existsById(999L));
    }

    @Test
    void shouldThrowExceptionWhenPublicationIsUsedByTwoRides() {
        rideRepository.save(ride);
        Ride duplicate = Ride.builder()
                .publication(publication)
                .driver(driver).vehicle(vehicle)
                .fromUTEC(true)
                .destinationOrOrigin("Miraflores")
                .departureTime(departureTime)
                .build();

        assertThrows(Exception.class, () -> rideRepository.saveAndFlush(duplicate));
    }

    @Test
    void shouldReturnCorrectCountWhenRidesAreSaved() {
        rideRepository.save(ride);

        Publication publication2 = publicationRepository.save(Publication.builder()
                .fromUTEC(false).driverToPassenger(true)
                .seats(2).titulo("Viaje a San Isidro")
                .destinationOrOrigin("San Isidro")
                .departureTime(departureTime)
                .author(driver)
                .build());

        rideRepository.save(Ride.builder()
                .publication(publication2)
                .driver(driver).vehicle(vehicle)
                .fromUTEC(false)
                .destinationOrOrigin("San Isidro")
                .departureTime(departureTime)
                .build());

        assertEquals(2, rideRepository.count());
    }
}