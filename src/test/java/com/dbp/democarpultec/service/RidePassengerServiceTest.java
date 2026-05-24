package com.dbp.democarpultec.service;

import com.dbp.democarpultec.dto.RidePassengerRequestDto;
import com.dbp.democarpultec.dto.RidePassengerResponseDto;
import com.dbp.democarpultec.model.Ride;
import com.dbp.democarpultec.model.RidePassenger;
import com.dbp.democarpultec.model.User;
import com.dbp.democarpultec.repository.RidePassengerRepository;
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
class RidePassengerServiceTest {
    @Mock
    private RidePassengerRepository ridePassengerRepository;

    @Mock
    private UserService userService;

    @Mock
    private RideService rideService;

    @InjectMocks
    private RidePassengerService ridePassengerService;

    private User passenger;
    private Ride ride;
    private RidePassenger ridePassenger;
    private RidePassengerRequestDto requestDto;

    @BeforeEach
    void setUp() {
        passenger = User.builder()
                .id(1L)
                .name("Juan")
                .build();

        ride = Ride.builder()
                .id(1L)
                .destinationOrOrigin("Miraflores")
                .build();

        ridePassenger = RidePassenger.builder()
                .id(1L)
                .passenger(passenger)
                .ride(ride)
                .seatsReserved(2)
                .pickupPoint("UTEC")
                .build();

        requestDto = RidePassengerRequestDto.builder()
                .passengerId(1L)
                .rideId(1L)
                .seatsReserved(2)
                .pickupPoint("UTEC")
                .build();
    }

    @Test
    void shouldReturnRidePassengerListWhenRidePassengersExist() {
        when(ridePassengerRepository.findAll()).thenReturn(List.of(ridePassenger));

        List<RidePassengerResponseDto> result = ridePassengerService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getPassengerId());
        assertEquals(1L, result.get(0).getRideId());
        assertEquals(2, result.get(0).getSeatsReserved());
        assertEquals("UTEC", result.get(0).getPickupPoint());

        verify(ridePassengerRepository).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoRidePassengersExist() {
        when(ridePassengerRepository.findAll()).thenReturn(Collections.emptyList());

        List<RidePassengerResponseDto> result = ridePassengerService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(ridePassengerRepository).findAll();
    }

    @Test
    void shouldReturnRidePassengerWhenIdExists() {
        when(ridePassengerRepository.findById(1L)).thenReturn(Optional.of(ridePassenger));

        RidePassengerResponseDto result = ridePassengerService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getPassengerId());
        assertEquals(1L, result.getRideId());
        assertEquals(2, result.getSeatsReserved());
        assertEquals("UTEC", result.getPickupPoint());

        verify(ridePassengerRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenRidePassengerDoesNotExist() {
        when(ridePassengerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> ridePassengerService.findById(1L));

        verify(ridePassengerRepository).findById(1L);
    }

    @Test
    void shouldCreateRidePassengerWhenValidData() {
        when(userService.findEntityById(1L)).thenReturn(passenger);
        when(rideService.findEntityById(1L)).thenReturn(ride);

        when(ridePassengerRepository.save(any(RidePassenger.class))).thenReturn(ridePassenger);

        RidePassengerResponseDto result = ridePassengerService.create(requestDto);

        assertNotNull(result);
        assertEquals(1L, result.getPassengerId());
        assertEquals(1L, result.getRideId());
        assertEquals(2, result.getSeatsReserved());
        assertEquals("UTEC", result.getPickupPoint());

        verify(userService).findEntityById(1L);
        verify(rideService).findEntityById(1L);
        verify(ridePassengerRepository).save(any(RidePassenger.class));
    }

    @Test
    void shouldUpdateRidePassengerWhenRidePassengerExists() {
        when(ridePassengerRepository.findById(1L)).thenReturn(Optional.of(ridePassenger));

        when(userService.findEntityById(1L)).thenReturn(passenger);
        when(rideService.findEntityById(1L)).thenReturn(ride);

        when(ridePassengerRepository.save(any(RidePassenger.class))).thenReturn(ridePassenger);

        RidePassengerResponseDto result = ridePassengerService.update(1L, requestDto);

        assertNotNull(result);
        assertEquals(1L, result.getPassengerId());
        assertEquals(1L, result.getRideId());
        assertEquals(2, result.getSeatsReserved());
        assertEquals("UTEC", result.getPickupPoint());

        verify(ridePassengerRepository).findById(1L);
        verify(userService).findEntityById(1L);
        verify(rideService).findEntityById(1L);
        verify(ridePassengerRepository).save(any(RidePassenger.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingRidePassenger() {
        when(ridePassengerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> ridePassengerService.update(1L, requestDto));

        verify(ridePassengerRepository).findById(1L);
        verify(ridePassengerRepository, never()).save(any(RidePassenger.class));
    }

    @Test
    void shouldDeleteRidePassengerWhenRidePassengerExists() {
        when(ridePassengerRepository.existsById(1L)).thenReturn(true);

        ridePassengerService.delete(1L);

        verify(ridePassengerRepository).existsById(1L);
        verify(ridePassengerRepository).deleteById(1L);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingRidePassenger() {
        when(ridePassengerRepository.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> ridePassengerService.delete(1L));

        verify(ridePassengerRepository).existsById(1L);
        verify(ridePassengerRepository, never()).deleteById(anyLong());
    }
}
