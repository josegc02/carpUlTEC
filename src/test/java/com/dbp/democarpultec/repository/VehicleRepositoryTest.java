package com.dbp.democarpultec.repository;

import com.dbp.democarpultec.PostgresContainerTest;
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
class VehicleRepositoryTest extends PostgresContainerTest {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveVehicleWhenValidData() {
        User owner = userRepository.save(RepositoryTestFactory.user("vehicleOwner"));
        Vehicle vehicle = RepositoryTestFactory.vehicle(owner, "ABC");

        Vehicle savedVehicle = vehicleRepository.save(vehicle);

        assertNotNull(savedVehicle.getId());
        assertEquals(owner.getId(), savedVehicle.getOwner().getId());
        assertEquals("Toyota", savedVehicle.getBrand());
    }

    @Test
    void shouldDeleteVehicleWhenVehicleExists() {
        User owner = userRepository.save(RepositoryTestFactory.user("deleteVehicleOwner"));
        Vehicle savedVehicle = vehicleRepository.save(RepositoryTestFactory.vehicle(owner, "DEL"));

        vehicleRepository.deleteById(savedVehicle.getId());

        assertTrue(vehicleRepository.findById(savedVehicle.getId()).isEmpty());
    }

    @Test
    void shouldReturnEmptyWhenVehicleDoesNotExist() {
        Optional<Vehicle> result = vehicleRepository.findById(999L);

        assertTrue(result.isEmpty());
    }
}
