package com.dbp.democarpultec.controller;

import com.dbp.democarpultec.dto.RideRequestDto;
import com.dbp.democarpultec.dto.RideResponseDto;
import com.dbp.democarpultec.service.RideService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RideController.class)
public class RideControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RideService rideService;

    private final LocalDateTime departureTime = LocalDateTime.of(2025, 6, 1, 7, 30);

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    private RideResponseDto buildResponse() {
        return RideResponseDto.builder()
                .id(1L)
                .publicationId(1L)
                .driverId(1L)
                .vehicleId(1L)
                .fromUTEC(true)
                .destinationOrOrigin("Miraflores")
                .departureTime(departureTime)
                .build();
    }

    private RideRequestDto buildRequest() {
        return RideRequestDto.builder()
                .publicationId(1L)
                .driverId(1L)
                .vehicleId(1L)
                .fromUTEC(true)
                .destinationOrOrigin("Miraflores")
                .departureTime(departureTime)
                .build();
    }

    @Test
    void shouldReturnAllRidesWhenRidesExist() throws Exception {
        when(rideService.findAll()).thenReturn(List.of(buildResponse()));

        mockMvc.perform(get("/api/rides"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].destinationOrOrigin").value("Miraflores"));

        verify(rideService).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoRidesExist() throws Exception {
        when(rideService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/rides"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldReturnRideWhenIdExists() throws Exception {
        when(rideService.findById(1L)).thenReturn(buildResponse());

        mockMvc.perform(get("/api/rides/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.publicationId").value(1))
                .andExpect(jsonPath("$.driverId").value(1))
                .andExpect(jsonPath("$.vehicleId").value(1));

        verify(rideService).findById(1L);
    }

    @Test
    void shouldReturn404WhenRideNotFound() throws Exception {
        when(rideService.findById(99L)).thenThrow(new EntityNotFoundException("Ride not found with id 99"));

        mockMvc.perform(get("/api/rides/99"))
                .andExpect(status().isNotFound());

        verify(rideService).findById(99L);
    }

    @Test
    void shouldCreateRideWhenValidRequest() throws Exception {
        when(rideService.create(any(RideRequestDto.class))).thenReturn(buildResponse());

        mockMvc.perform(post("/api/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.destinationOrOrigin").value("Miraflores"))
                .andExpect(jsonPath("$.fromUTEC").value(true));

        verify(rideService).create(any(RideRequestDto.class));
    }

    @Test
    void shouldReturn400WhenRequiredFieldsAreMissing() throws Exception {
        RideRequestDto invalid = RideRequestDto.builder()
                .destinationOrOrigin("")
                .build();

        mockMvc.perform(post("/api/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());

        verify(rideService, never()).create(any());
    }

    @Test
    void shouldUpdateRideWhenValidRequest() throws Exception {
        RideResponseDto updated = RideResponseDto.builder()
                .id(1L)
                .publicationId(2L)
                .driverId(3L)
                .vehicleId(4L)
                .fromUTEC(false)
                .destinationOrOrigin("UTEC")
                .departureTime(departureTime)
                .build();

        RideRequestDto request = RideRequestDto.builder()
                .publicationId(2L)
                .driverId(3L)
                .vehicleId(4L)
                .fromUTEC(false)
                .destinationOrOrigin("UTEC")
                .departureTime(departureTime)
                .build();

        when(rideService.update(eq(1L), any(RideRequestDto.class))).thenReturn(updated);

        mockMvc.perform(put("/api/rides/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fromUTEC").value(false))
                .andExpect(jsonPath("$.destinationOrOrigin").value("UTEC"));

        verify(rideService).update(eq(1L), any(RideRequestDto.class));
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistentRide() throws Exception {
        when(rideService.update(eq(99L), any(RideRequestDto.class))).thenThrow(new EntityNotFoundException("Ride not found with id 99"));

        mockMvc.perform(put("/api/rides/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isNotFound());

        verify(rideService).update(eq(99L), any(RideRequestDto.class));
    }

    @Test
    void shouldDeleteRideWhenIdExists() throws Exception {
        doNothing().when(rideService).delete(1L);

        mockMvc.perform(delete("/api/rides/1"))
                .andExpect(status().isNoContent());

        verify(rideService).delete(1L);
    }

    @Test
    void shouldReturn404WhenDeletingNonExistentRide() throws Exception {
        doThrow(new EntityNotFoundException("Ride not found with id 99")).when(rideService).delete(99L);

        mockMvc.perform(delete("/api/rides/99"))
                .andExpect(status().isNotFound());

        verify(rideService).delete(99L);
    }
}