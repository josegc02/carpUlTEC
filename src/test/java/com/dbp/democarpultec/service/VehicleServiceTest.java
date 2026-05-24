package com.dbp.democarpultec.service;

import com.dbp.democarpultec.dto.VehicleRequestDto;
import com.dbp.democarpultec.dto.VehicleResponseDto;
import com.dbp.democarpultec.model.User;
import com.dbp.democarpultec.model.Vehicle;
import com.dbp.democarpultec.model.enums.Carreras;
import com.dbp.democarpultec.repository.VehicleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {
    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private VehicleService vehicleService;

    private User owner;
    private Vehicle vehicle;
    private VehicleRequestDto requestDto;

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .id(1L)
                .name("Juan")
                .lastName("Perez")
                .email("juan@utec.edu.pe")
                .phone("999999999")
                .studentCode("202410001")
                .career(Carreras.Ciencia_de_la_Computacion)
                .cycle(5)
                .rating(4.5)
                .build();

        vehicle = Vehicle.builder()
                .id(1L)
                .owner(owner)
                .plate("ABC-123")
                .brand("Toyota")
                .model("Corolla")
                .color("Blanco")
                .seats(4)
                .build();

        requestDto = VehicleRequestDto.builder()
                .ownerId(1L)
                .plate("ABC-123")
                .brand("Toyota")
                .model("Corolla")
                .color("Blanco")
                .seats(4)
                .build();
    }

    @Test
    void shouldReturnVehicleListWhenVehiclesExist() {
        when(vehicleRepository.findAll()).thenReturn(List.of(vehicle));

        List<VehicleResponseDto> result = vehicleService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("ABC-123", result.get(0).getPlate());
        assertEquals("Toyota", result.get(0).getBrand());
        assertEquals("Corolla", result.get(0).getModel());
        assertEquals("Blanco", result.get(0).getColor());
        assertEquals(4, result.get(0).getSeats());
        assertEquals(1L, result.get(0).getOwnerId());

        verify(vehicleRepository).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoVehiclesExist() {
        when(vehicleRepository.findAll()).thenReturn(Collections.emptyList());

        List<VehicleResponseDto> result = vehicleService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(vehicleRepository).findAll();
    }

    @Test
    void shouldReturnVehicleWhenIdExists() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));

        VehicleResponseDto result = vehicleService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("ABC-123", result.getPlate());
        assertEquals("Toyota", result.getBrand());
        assertEquals("Corolla", result.getModel());
        assertEquals("Blanco", result.getColor());
        assertEquals(4, result.getSeats());
        assertEquals(1L, result.getOwnerId());

        verify(vehicleRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenVehicleDoesNotExist() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> vehicleService.findById(1L));

        verify(vehicleRepository).findById(1L);
    }


    @Test
    void shouldReturnVehicleEntityWhenIdExists() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));

        Vehicle result = vehicleService.findEntityById(1L);

        assertNotNull(result);
        assertEquals("ABC-123", result.getPlate());
        assertEquals("Toyota", result.getBrand());

        verify(vehicleRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenVehicleEntityDoesNotExist() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> vehicleService.findEntityById(1L));

        verify(vehicleRepository).findById(1L);
    }

    @Test
    void shouldCreateVehicleWhenValidData() {
        when(userService.findEntityById(1L)).thenReturn(owner);
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle);

        VehicleResponseDto result = vehicleService.create(requestDto);

        assertNotNull(result);
        assertEquals(1L, result.getOwnerId());
        assertEquals("ABC-123", result.getPlate());
        assertEquals("Toyota", result.getBrand());
        assertEquals("Corolla", result.getModel());
        assertEquals("Blanco", result.getColor());
        assertEquals(4, result.getSeats());

        verify(userService).findEntityById(1L);
        verify(vehicleRepository).save(any(Vehicle.class));
    }

    @Test
    void shouldUpdateVehicleWhenVehicleExists() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(userService.findEntityById(1L)).thenReturn(owner);
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle);

        VehicleResponseDto result = vehicleService.update(1L, requestDto);

        assertNotNull(result);
        assertEquals("ABC-123", result.getPlate());
        assertEquals("Toyota", result.getBrand());
        assertEquals("Corolla", result.getModel());
        assertEquals("Blanco", result.getColor());
        assertEquals(4, result.getSeats());
        assertEquals(1L, result.getOwnerId());

        verify(vehicleRepository).findById(1L);
        verify(userService).findEntityById(1L);
        verify(vehicleRepository).save(any(Vehicle.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingVehicle() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> vehicleService.update(1L, requestDto));

        verify(vehicleRepository).findById(1L);
        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }

    @Test
    void shouldDeleteVehicleWhenVehicleExists() {
        when(vehicleRepository.existsById(1L)).thenReturn(true);

        vehicleService.delete(1L);

        verify(vehicleRepository).existsById(1L);
        verify(vehicleRepository).deleteById(1L);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingVehicle() {
        when(vehicleRepository.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> vehicleService.delete(1L));

        verify(vehicleRepository).existsById(1L);
        verify(vehicleRepository, never()).deleteById(anyLong());
    }
}