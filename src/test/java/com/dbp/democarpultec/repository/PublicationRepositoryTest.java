package com.dbp.democarpultec.repository;

import com.dbp.democarpultec.model.Publication;
import com.dbp.democarpultec.model.User;
import com.dbp.democarpultec.model.enums.Carreras;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PublicationRepositoryTest extends BaseRepositoryTest {
    @Autowired
    private PublicationRepository publicationRepository;

    @Autowired
    private UserRepository userRepository;

    private User author;
    private Publication publication;
    private final LocalDateTime departureTime = LocalDateTime.of(2025, 6, 1, 7, 30);

    @BeforeEach
    void setUp() {
        publicationRepository.deleteAll();
        userRepository.deleteAll();

        author = userRepository.save(User.builder()
                .name("Juan")
                .lastName("Perez")
                .email("juan.perez@utec.edu.pe")
                .phone("111111111")
                .studentCode("202410001")
                .career(Carreras.Ciencia_de_la_Computacion)
                .cycle(5)
                .rating(4.5)
                .build());

        publication = Publication.builder()
                .fromUTEC(true)
                .driverToPassenger(true)
                .seats(3)
                .titulo("Viaje a Miraflores")
                .descripcion("Salgo puntual")
                .destinationOrOrigin("Miraflores")
                .externalLatitude(-12.1211)
                .externalLongitude(-77.0282)
                .departureTime(departureTime)
                .author(author)
                .build();
    }

    @Test
    void shouldSavePublicationWhenValidData() {
        Publication saved = publicationRepository.save(publication);

        assertNotNull(saved.getId());
        assertEquals("Viaje a Miraflores", saved.getTitulo());
        assertEquals("Miraflores", saved.getDestinationOrOrigin());
        assertTrue(saved.getFromUTEC());
        assertEquals(3, saved.getSeats());
    }

    @Test
    void shouldGenerateIdAutomaticallyWhenPublicationIsSaved() {
        Publication saved = publicationRepository.save(publication);

        assertNotNull(saved.getId());
        assertTrue(saved.getId() > 0);
    }

    @Test
    void shouldSavePublicationWithAuthorWhenAuthorExists() {
        Publication saved = publicationRepository.save(publication);

        assertNotNull(saved.getAuthor());
        assertEquals(author.getId(), saved.getAuthor().getId());
        assertEquals("juan.perez@utec.edu.pe", saved.getAuthor().getEmail());
    }

    @Test
    void shouldReturnPublicationWhenIdExists() {
        Publication saved = publicationRepository.save(publication);

        Optional<Publication> result = publicationRepository.findById(saved.getId());

        assertTrue(result.isPresent());
        assertEquals("Viaje a Miraflores", result.get().getTitulo());
        assertEquals("Miraflores", result.get().getDestinationOrOrigin());
    }

    @Test
    void shouldReturnEmptyWhenIdDoesNotExist() {
        Optional<Publication> result = publicationRepository.findById(999L);

        assertFalse(result.isPresent());
    }

    @Test
    void shouldReturnAllPublicationsWhenMultiplePublicationsExist() {
        publicationRepository.save(publication);
        publicationRepository.save(Publication.builder()
                .fromUTEC(false)
                .driverToPassenger(false)
                .seats(2)
                .titulo("Viaje a San Isidro")
                .destinationOrOrigin("San Isidro")
                .departureTime(departureTime)
                .author(author)
                .build());

        List<Publication> result = publicationRepository.findAll();

        assertEquals(2, result.size());
    }

    @Test
    void shouldReturnEmptyListWhenNoPublicationsExist() {
        assertTrue(publicationRepository.findAll().isEmpty());
    }

    @Test
    void shouldUpdatePublicationWhenPublicationExists() {
        Publication saved = publicationRepository.save(publication);

        saved.setTitulo("Viaje a Surco");
        saved.setDestinationOrOrigin("Surco");
        saved.setSeats(2);
        Publication updated = publicationRepository.save(saved);

        assertEquals("Viaje a Surco", updated.getTitulo());
        assertEquals("Surco", updated.getDestinationOrOrigin());
        assertEquals(2, updated.getSeats());
        assertEquals(saved.getId(), updated.getId());
    }

    @Test
    void shouldUpdateFromUTECFlagWhenPublicationExists() {
        Publication saved = publicationRepository.save(publication);

        saved.setFromUTEC(false);
        Publication updated = publicationRepository.save(saved);

        assertFalse(updated.getFromUTEC());
    }

    @Test
    void shouldDeletePublicationWhenPublicationExists() {
        Publication saved = publicationRepository.save(publication);
        Long id = saved.getId();

        publicationRepository.deleteById(id);

        assertFalse(publicationRepository.findById(id).isPresent());
    }

    @Test
    void shouldNotFailWhenDeletingAllPublications() {
        publicationRepository.save(publication);
        publicationRepository.save(Publication.builder()
                .fromUTEC(false)
                .driverToPassenger(false)
                .seats(2)
                .titulo("Viaje a San Isidro")
                .destinationOrOrigin("San Isidro")
                .departureTime(departureTime)
                .author(author)
                .build());

        publicationRepository.deleteAll();

        assertEquals(0, publicationRepository.count());
    }

    @Test
    void shouldReturnTrueWhenPublicationExists() {
        Publication saved = publicationRepository.save(publication);

        assertTrue(publicationRepository.existsById(saved.getId()));
    }

    @Test
    void shouldReturnFalseWhenPublicationDoesNotExist() {
        assertFalse(publicationRepository.existsById(999L));
    }

    @Test
    void shouldSavePublicationWithCoordinatesWhenProvided() {
        Publication saved = publicationRepository.save(publication);

        assertEquals(-12.1211, saved.getExternalLatitude());
        assertEquals(-77.0282, saved.getExternalLongitude());
    }

    @Test
    void shouldSavePublicationWithNullCoordinatesWhenNotProvided() {
        Publication noCoords = Publication.builder()
                .fromUTEC(true)
                .driverToPassenger(true)
                .seats(2)
                .titulo("Viaje sin coords")
                .destinationOrOrigin("Barranco")
                .departureTime(departureTime)
                .author(author)
                .build();

        Publication saved = publicationRepository.save(noCoords);

        assertNull(saved.getExternalLatitude());
        assertNull(saved.getExternalLongitude());
    }

    @Test
    void shouldSavePublicationWithCorrectDepartureTime() {
        Publication saved = publicationRepository.save(publication);

        assertEquals(departureTime, saved.getDepartureTime());
    }

    @Test
    void shouldReturnCorrectCountWhenPublicationsAreSaved() {
        publicationRepository.save(publication);
        publicationRepository.save(Publication.builder()
                .fromUTEC(false)
                .driverToPassenger(false)
                .seats(2)
                .titulo("Viaje a San Isidro")
                .destinationOrOrigin("San Isidro")
                .departureTime(departureTime)
                .author(author)
                .build());

        assertEquals(2, publicationRepository.count());
    }
}