package com.dbp.democarpultec.service;

import com.dbp.democarpultec.dto.RideRequestDto;
import com.dbp.democarpultec.dto.RideResponseDto;
import com.dbp.democarpultec.model.Publication;
import com.dbp.democarpultec.model.Ride;
import com.dbp.democarpultec.model.User;
import com.dbp.democarpultec.model.Vehicle;
import com.dbp.democarpultec.model.enums.Carreras;
import com.dbp.democarpultec.repository.RideRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RideServiceTest {
    @Mock
    private RideRepository rideRepository;

    @Mock
    private PublicationService publicationService;

    @Mock
    private UserService userService;

    @Mock
    private VehicleService vehicleService;

    @InjectMocks
    private RideService rideService;

    private User driver;
    private Vehicle vehicle;
    private Publication publication;
    private Ride ride;
    private RideRequestDto requestDto;


    @BeforeEach
    void setUp() {
        publication = Publication.builder()
                .id(1L)
                .titulo("Viaje UTEC")
                .build();

        driver = User.builder()
                .id(1L)
                .name("Juan")
                .build();

        vehicle = Vehicle.builder()
                .id(1L)
                .plate("ABC-123")
                .build();

        ride = Ride.builder()
                .id(1L)
                .publication(publication)
                .driver(driver)
                .vehicle(vehicle)
                .fromUTEC(true)
                .destinationOrOrigin("Miraflores")
                .departureTime(LocalDateTime.now())
                .build();

        requestDto = RideRequestDto.builder()
                .publicationId(1L)
                .driverId(1L)
                .vehicleId(1L)
                .fromUTEC(true)
                .destinationOrOrigin("Miraflores")
                .departureTime(LocalDateTime.now())
                .build();
    }

    @Test
    void shouldReturnRideListWhenRidesExist() {
        when(rideRepository.findAll()).thenReturn(List.of(ride));

        List<RideResponseDto> result = rideService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getPublicationId());
        assertEquals(1L, result.get(0).getDriverId());
        assertEquals(1L, result.get(0).getVehicleId());
        assertEquals("Miraflores", result.get(0).getDestinationOrOrigin());

        verify(rideRepository).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoRidesExist() {
        when(rideRepository.findAll()).thenReturn(Collections.emptyList());

        List<RideResponseDto> result = rideService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(rideRepository).findAll();
    }

    @Test
    void shouldReturnRideWhenIdExists() {
        when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));

        RideResponseDto result = rideService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getPublicationId());
        assertEquals(1L, result.getDriverId());
        assertEquals(1L, result.getVehicleId());
        assertEquals("Miraflores", result.getDestinationOrOrigin());

        verify(rideRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenRideDoesNotExist() {
        when(rideRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> rideService.findById(1L));

        verify(rideRepository).findById(1L);
    }

    @Test
    void shouldCreateRideWhenValidData() {
        when(publicationService.findEntityById(1L)).thenReturn(publication);
        when(userService.findEntityById(1L)).thenReturn(driver);
        when(vehicleService.findEntityById(1L)).thenReturn(vehicle);

        when(rideRepository.save(any(Ride.class))).thenReturn(ride);

        RideResponseDto result = rideService.create(requestDto);

        assertNotNull(result);
        assertEquals(1L, result.getPublicationId());
        assertEquals(1L, result.getDriverId());
        assertEquals(1L, result.getVehicleId());
        assertEquals("Miraflores", result.getDestinationOrOrigin());

        verify(publicationService).findEntityById(1L);
        verify(userService).findEntityById(1L);
        verify(vehicleService).findEntityById(1L);
        verify(rideRepository).save(any(Ride.class));
    }

    @Test
    void shouldUpdateRideWhenRideExists() {
        when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));
        when(publicationService.findEntityById(1L)).thenReturn(publication);
        when(userService.findEntityById(1L)).thenReturn(driver);
        when(vehicleService.findEntityById(1L)).thenReturn(vehicle);

        when(rideRepository.save(any(Ride.class))).thenReturn(ride);

        RideResponseDto result = rideService.update(1L, requestDto);

        assertNotNull(result);
        assertEquals(1L, result.getPublicationId());
        assertEquals(1L, result.getDriverId());
        assertEquals(1L, result.getVehicleId());
        assertEquals("Miraflores", result.getDestinationOrOrigin());

        verify(rideRepository).findById(1L);
        verify(publicationService).findEntityById(1L);
        verify(userService).findEntityById(1L);
        verify(vehicleService).findEntityById(1L);
        verify(rideRepository).save(any(Ride.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingRide() {
        when(rideRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> rideService.update(1L, requestDto));

        verify(rideRepository).findById(1L);
        verify(rideRepository, never()).save(any(Ride.class));
    }

    @Test
    void shouldDeleteRideWhenRideExists() {
        when(rideRepository.existsById(1L)).thenReturn(true);

        rideService.delete(1L);

        verify(rideRepository).existsById(1L);
        verify(rideRepository).deleteById(1L);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingRide() {
        when(rideRepository.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> rideService.delete(1L));

        verify(rideRepository).existsById(1L);
        verify(rideRepository, never()).deleteById(anyLong());
    }

    @Test
    void shouldThrowExceptionWhenPublicationDoesNotExistOnCreate() {
        when(publicationService.findEntityById(99L)).thenThrow(new EntityNotFoundException("Publication not found with id 99"));

        requestDto.setPublicationId(99L);

        assertThrows(EntityNotFoundException.class, () -> rideService.create(requestDto));

        verify(rideRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenDriverDoesNotExistOnCreate() {
        when(publicationService.findEntityById(1L)).thenReturn(publication);
        when(userService.findEntityById(99L)).thenThrow(new EntityNotFoundException("User not found with id 99"));

        requestDto.setDriverId(99L);

        assertThrows(EntityNotFoundException.class, () -> rideService.create(requestDto));

        verify(rideRepository, never()).save(any());
    }
}
