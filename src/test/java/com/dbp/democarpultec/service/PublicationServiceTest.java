package com.dbp.democarpultec.service;

import com.dbp.democarpultec.dto.PublicationRequestDto;
import com.dbp.democarpultec.dto.PublicationResponseDto;
import com.dbp.democarpultec.exception.BusinessRuleException;
import com.dbp.democarpultec.exception.ForbiddenException;
import com.dbp.democarpultec.model.Publication;
import com.dbp.democarpultec.model.User;
import com.dbp.democarpultec.repository.PublicationRepository;
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
class PublicationServiceTest {
    @Mock
    private PublicationRepository publicationRepository;

    @Mock
    private UserService userService;

    @Mock
    private GeoService geoService;

    @InjectMocks
    private PublicationService publicationService;

    private User user;
    private Publication publication;
    private PublicationRequestDto requestDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("Juan")
                .email("juan@utec.edu.pe")
                .build();

        publication = Publication.builder()
                .id(1L)
                .fromUTEC(true)
                .driverToPassenger(true)
                .seats(3)
                .titulo("Viaje a Miraflores")
                .descripcion("Salida después de clases")
                .destinationOrOrigin("Miraflores")
                .externalLatitude(-12.135)
                .externalLongitude(-77.022)
                .departureTime(LocalDateTime.now())
                .author(user)
                .build();

        requestDto = PublicationRequestDto.builder()
                .fromUTEC(true)
                .driverToPassenger(true)
                .seats(3)
                .titulo("Viaje a Miraflores")
                .descripcion("Salida después de clases")
                .destinationOrOrigin("Miraflores")
                .externalLatitude(-12.135)
                .externalLongitude(-77.022)
                .departureTime(LocalDateTime.now())
                .authorId(1L)
                .build();
    }

    @Test
    void shouldReturnPublicationListWhenPublicationsExist() {

        when(publicationRepository.findAll()).thenReturn(List.of(publication));
        when(geoService.distanceToUtecKm(anyDouble(), anyDouble())).thenReturn(5.0);

        List<PublicationResponseDto> result = publicationService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Viaje a Miraflores", result.get(0).getTitulo());
        assertEquals("Miraflores", result.get(0).getDestinationOrOrigin());
        assertEquals(1L, result.get(0).getAuthorId());

        verify(publicationRepository).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoPublicationsExist() {

        when(publicationRepository.findAll()).thenReturn(Collections.emptyList());

        List<PublicationResponseDto> result = publicationService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(publicationRepository).findAll();
    }

    @Test
    void shouldReturnPublicationWhenIdExists() {
        when(publicationRepository.findById(1L)).thenReturn(Optional.of(publication));
        when(geoService.distanceToUtecKm(anyDouble(), anyDouble())).thenReturn(5.0);

        PublicationResponseDto result = publicationService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Viaje a Miraflores", result.getTitulo());
        assertEquals("Miraflores", result.getDestinationOrOrigin());
        assertEquals(1L, result.getAuthorId());

        verify(publicationRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenPublicationDoesNotExist() {
        when(publicationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> publicationService.findById(1L));

        verify(publicationRepository).findById(1L);
    }

    @Test
    void shouldCreatePublicationWhenValidData() {
        when(userService.findEntityById(1L)).thenReturn(user);
        when(publicationRepository.save(any(Publication.class))).thenReturn(publication);
        when(geoService.distanceToUtecKm(anyDouble(), anyDouble())).thenReturn(5.0);

        PublicationResponseDto result = publicationService.create(requestDto);

        assertNotNull(result);
        assertEquals("Viaje a Miraflores", result.getTitulo());
        assertEquals("Miraflores", result.getDestinationOrOrigin());
        assertEquals(1L, result.getAuthorId());

        verify(userService).findEntityById(1L);
        verify(publicationRepository).save(any(Publication.class));
    }

    @Test
    void shouldUpdatePublicationWhenPublicationExists() {
        when(publicationRepository.findById(1L)).thenReturn(Optional.of(publication));
        when(userService.findEntityById(1L)).thenReturn(user);
        when(publicationRepository.save(any(Publication.class))).thenReturn(publication);
        when(geoService.distanceToUtecKm(anyDouble(), anyDouble())).thenReturn(5.0);

        PublicationResponseDto result = publicationService.update(1L, requestDto);

        assertNotNull(result);
        assertEquals("Viaje a Miraflores", result.getTitulo());
        assertEquals("Miraflores", result.getDestinationOrOrigin());
        assertEquals(1L, result.getAuthorId());

        verify(publicationRepository).findById(1L);
        verify(userService).findEntityById(1L);
        verify(publicationRepository).save(any(Publication.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingPublication() {
        when(publicationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> publicationService.update(1L, requestDto));

        verify(publicationRepository).findById(1L);
        verify(publicationRepository, never()).save(any(Publication.class));
    }

    @Test
    void shouldDeletePublicationWhenPublicationExists() {
        when(publicationRepository.existsById(1L)).thenReturn(true);

        publicationService.delete(1L);

        verify(publicationRepository).existsById(1L);
        verify(publicationRepository).deleteById(1L);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingPublication() {
        when(publicationRepository.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> publicationService.delete(1L));

        verify(publicationRepository).existsById(1L);
        verify(publicationRepository, never()).deleteById(anyLong());
    }

    @Test
    void shouldThrowExceptionWhenOnlyLatitudeExists() {
        requestDto.setExternalLongitude(null);

        assertThrows(BusinessRuleException.class, () -> publicationService.create(requestDto));

        verify(publicationRepository, never()).save(any(Publication.class));
    }

    @Test
    void shouldThrowExceptionWhenOnlyLongitudeExists() {
        requestDto.setExternalLatitude(null);

        assertThrows(BusinessRuleException.class, () -> publicationService.create(requestDto));

        verify(publicationRepository, never()).save(any(Publication.class));
    }

    @Test
    void shouldThrowForbiddenExceptionWhenUserIsNotOwner() {
        User anotherUser = User.builder()
                .id(99L)
                .build();

        publication.setAuthor(anotherUser);

        when(publicationRepository.findById(1L)).thenReturn(Optional.of(publication));

        assertThrows(ForbiddenException.class, () -> publicationService.updateAuthenticated(1L, 1L, requestDto));

        verify(publicationRepository).findById(1L);
        verify(publicationRepository, never()).save(any(Publication.class));
    }

    @Test
    void shouldCreatePublicationWhenAuthenticatedUserIsValid() {
        when(userService.findEntityById(1L)).thenReturn(user);
        when(publicationRepository.save(any(Publication.class))).thenReturn(publication);
        when(geoService.distanceToUtecKm(anyDouble(), anyDouble())).thenReturn(5.0);

        PublicationResponseDto result = publicationService.createAuthenticated(1L, requestDto);

        assertNotNull(result);
        assertEquals("Viaje a Miraflores", result.getTitulo());
        verify(userService).findEntityById(1L);
        verify(publicationRepository).save(any(Publication.class));
    }

    @Test
    void shouldDeletePublicationWhenUserIsAuthor() {
        when(publicationRepository.findById(1L)).thenReturn(Optional.of(publication));

        publicationService.deleteAuthenticated(1L, 1L);

        verify(publicationRepository).findById(1L);
        verify(publicationRepository).deleteById(1L);
    }

    @Test
    void shouldThrowExceptionWhenUserIsNotAuthorOnDelete() {
        User anotherUser = User.builder().id(99L).build();
        publication.setAuthor(anotherUser);

        when(publicationRepository.findById(1L)).thenReturn(Optional.of(publication));

        assertThrows(ForbiddenException.class, () -> publicationService.deleteAuthenticated(1L, 1L));

        verify(publicationRepository, never()).deleteById(any());
    }
}
