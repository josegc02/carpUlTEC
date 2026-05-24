package com.dbp.democarpultec.repository;

import com.dbp.democarpultec.model.User;
import com.dbp.democarpultec.model.Vehicle;
import com.dbp.democarpultec.model.enums.Carreras;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class VehicleRepositoryTest extends BaseRepositoryTest {
    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private UserRepository userRepository;

    private User owner;
    private Vehicle vehicle;

    @BeforeEach
    void setUp() {
        vehicleRepository.deleteAll();
        userRepository.deleteAll();

        owner = userRepository.save(User.builder()
                .name("Juan")
                .lastName("Perez")
                .email("juan.perez@utec.edu.pe")
                .phone("111111111")
                .studentCode("202410001")
                .career(Carreras.Ciencia_de_la_Computacion)
                .cycle(5)
                .rating(4.5)
                .build());

        vehicle = Vehicle.builder()
                .owner(owner)
                .plate("ABC-123")
                .brand("Toyota")
                .model("Corolla")
                .color("Blanco")
                .seats(4)
                .build();
    }

    @Test
    void shouldSaveVehicleWhenValidData() {
        Vehicle saved = vehicleRepository.save(vehicle);

        assertNotNull(saved.getId());
        assertEquals("ABC-123", saved.getPlate());
        assertEquals("Toyota", saved.getBrand());
    }

    @Test
    void shouldGenerateIdAutomaticallyWhenVehicleIsSaved() {
        Vehicle saved = vehicleRepository.save(vehicle);

        assertNotNull(saved.getId());
        assertTrue(saved.getId() > 0);
    }

    @Test
    void shouldSaveVehicleWithOwnerWhenOwnerExists() {
        Vehicle saved = vehicleRepository.save(vehicle);

        assertNotNull(saved.getOwner());
        assertEquals(owner.getId(), saved.getOwner().getId());
    }

    @Test
    void shouldReturnVehicleWhenIdExists() {
        Vehicle saved = vehicleRepository.save(vehicle);

        Optional<Vehicle> result = vehicleRepository.findById(saved.getId());

        assertTrue(result.isPresent());
        assertEquals("ABC-123", result.get().getPlate());
        assertEquals("Toyota", result.get().getBrand());
    }

    @Test
    void shouldReturnEmptyWhenIdDoesNotExist() {
        Optional<Vehicle> result = vehicleRepository.findById(999L);

        assertFalse(result.isPresent());
    }

    @Test
    void shouldReturnAllVehiclesWhenMultipleVehiclesExist() {
        vehicleRepository.save(vehicle);
        vehicleRepository.save(Vehicle.builder()
                .owner(owner)
                .plate("XYZ-999")
                .brand("Honda")
                .model("Civic")
                .color("Negro")
                .seats(5)
                .build());

        List<Vehicle> result = vehicleRepository.findAll();

        assertEquals(2, result.size());
    }

    @Test
    void shouldReturnEmptyListWhenNoVehiclesExist() {
        assertTrue(vehicleRepository.findAll().isEmpty());
    }

    @Test
    void shouldUpdateVehicleWhenVehicleExists() {
        Vehicle saved = vehicleRepository.save(vehicle);

        saved.setPlate("NEW-001");
        saved.setBrand("Honda");
        Vehicle updated = vehicleRepository.save(saved);

        assertEquals("NEW-001", updated.getPlate());
        assertEquals("Honda", updated.getBrand());
        assertEquals(saved.getId(), updated.getId());
    }

    @Test
    void shouldDeleteVehicleWhenVehicleExists() {
        Vehicle saved = vehicleRepository.save(vehicle);
        Long id = saved.getId();

        vehicleRepository.deleteById(id);

        assertFalse(vehicleRepository.findById(id).isPresent());
    }

    @Test
    void shouldNotFailWhenDeletingAllVehicles() {
        vehicleRepository.save(vehicle);
        vehicleRepository.save(Vehicle.builder()
                .owner(owner)
                .plate("XYZ-999")
                .brand("Honda")
                .model("Civic")
                .color("Negro")
                .seats(5)
                .build());

        vehicleRepository.deleteAll();

        assertEquals(0, vehicleRepository.count());
    }

    @Test
    void shouldReturnTrueWhenVehicleExists() {
        Vehicle saved = vehicleRepository.save(vehicle);

        assertTrue(vehicleRepository.existsById(saved.getId()));
    }

    @Test
    void shouldReturnFalseWhenVehicleDoesNotExist() {
        assertFalse(vehicleRepository.existsById(999L));
    }

    @Test
    void shouldThrowExceptionWhenPlateIsDuplicated() {
        vehicleRepository.save(vehicle);
        Vehicle duplicate = Vehicle.builder()
                .owner(owner)
                .plate("ABC-123")
                .brand("Honda")
                .model("Civic")
                .seats(4)
                .build();

        assertThrows(Exception.class, () -> vehicleRepository.saveAndFlush(duplicate));
    }

    @Test
    void shouldReturnCorrectCountWhenVehiclesAreSaved() {
        vehicleRepository.save(vehicle);
        vehicleRepository.save(Vehicle.builder()
                .owner(owner)
                .plate("XYZ-999")
                .brand("Honda")
                .model("Civic")
                .color("Negro")
                .seats(5)
                .build());

        assertEquals(2, vehicleRepository.count());
    }
}