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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class RideRepositoryTest extends PostgresContainerTest {

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private PublicationRepository publicationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Test
    void shouldFindRideByPublicationIdWhenRideExists() {
        Ride savedRide = saveRide("rideAuthor", "rideDriver", "RID");

        Optional<Ride> result = rideRepository.findByPublication_Id(savedRide.getPublication().getId());

        assertTrue(result.isPresent());
        assertEquals(savedRide.getId(), result.get().getId());
    }

    @Test
    void shouldReturnEmptyWhenPublicationHasNoRide() {
        User author = userRepository.save(RepositoryTestFactory.user("rideEmptyAuthor"));
        Publication publication = publicationRepository.save(RepositoryTestFactory.publication(author));

        Optional<Ride> result = rideRepository.findByPublication_Id(publication.getId());

        assertTrue(result.isEmpty());
    }

    private Ride saveRide(String authorPrefix, String driverPrefix, String platePrefix) {
        User author = userRepository.save(RepositoryTestFactory.user(authorPrefix));
        User driver = userRepository.save(RepositoryTestFactory.user(driverPrefix));
        Vehicle vehicle = vehicleRepository.save(RepositoryTestFactory.vehicle(driver, platePrefix));
        Publication publication = publicationRepository.save(RepositoryTestFactory.publication(author));
        return rideRepository.save(RepositoryTestFactory.ride(publication, driver, vehicle));
    }
}
