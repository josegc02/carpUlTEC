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

class ReviewRepositoryTest extends BaseRepositoryTest {
    @Autowired private ReviewRepository reviewRepository;
    @Autowired private RideRepository rideRepository;
    @Autowired private PublicationRepository publicationRepository;
    @Autowired private VehicleRepository vehicleRepository;
    @Autowired private UserRepository userRepository;

    private User reviewer;
    private User reviewed;
    private Ride ride;
    private Review review;
    private final LocalDateTime departureTime = LocalDateTime.of(2025, 6, 1, 7, 30);

    @BeforeEach
    void setUp() {
        reviewRepository.deleteAll();
        rideRepository.deleteAll();
        publicationRepository.deleteAll();
        vehicleRepository.deleteAll();
        userRepository.deleteAll();

        reviewer = userRepository.save(User.builder()
                .name("Juan").lastName("Perez")
                .email("juan.perez@utec.edu.pe")
                .phone("111111111").studentCode("202410001")
                .career(Carreras.Ciencia_de_la_Computacion)
                .cycle(5).rating(4.5)
                .build());

        reviewed = userRepository.save(User.builder()
                .name("Ana").lastName("Lopez")
                .email("ana.lopez@utec.edu.pe")
                .phone("222222222").studentCode("202410002")
                .career(Carreras.Ciencia_de_Datos)
                .cycle(4).rating(4.0)
                .build());

        Vehicle vehicle = vehicleRepository.save(Vehicle.builder()
                .owner(reviewer).plate("ABC-123")
                .brand("Toyota").model("Corolla")
                .color("Blanco").seats(4)
                .build());

        Publication publication = publicationRepository.save(Publication.builder()
                .fromUTEC(true).driverToPassenger(true)
                .seats(3).titulo("Viaje a Miraflores")
                .destinationOrOrigin("Miraflores")
                .departureTime(departureTime)
                .author(reviewer)
                .build());

        ride = rideRepository.save(Ride.builder()
                .publication(publication)
                .driver(reviewer).vehicle(vehicle)
                .fromUTEC(true)
                .destinationOrOrigin("Miraflores")
                .departureTime(departureTime)
                .build());

        review = Review.builder()
                .ride(ride)
                .reviewer(reviewer)
                .reviewed(reviewed)
                .rating(5)
                .comment("Excelente conductor")
                .build();
    }

    @Test
    void shouldSaveReviewWhenValidData() {
        Review saved = reviewRepository.save(review);

        assertNotNull(saved.getId());
        assertEquals(5, saved.getRating());
        assertEquals("Excelente conductor", saved.getComment());
    }

    @Test
    void shouldGenerateIdAutomaticallyWhenReviewIsSaved() {
        Review saved = reviewRepository.save(review);

        assertNotNull(saved.getId());
        assertTrue(saved.getId() > 0);
    }

    @Test
    void shouldSetCreatedAtAutomaticallyWhenReviewIsSaved() {
        Review saved = reviewRepository.save(review);

        assertNotNull(saved.getCreatedAt());
        assertTrue(saved.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void shouldSaveReviewWithReviewerAndReviewedWhenBothExist() {
        Review saved = reviewRepository.save(review);

        assertEquals(reviewer.getId(), saved.getReviewer().getId());
        assertEquals(reviewed.getId(), saved.getReviewed().getId());
        assertEquals(ride.getId(), saved.getRide().getId());
    }

    @Test
    void shouldReturnReviewWhenIdExists() {
        Review saved = reviewRepository.save(review);

        Optional<Review> result = reviewRepository.findById(saved.getId());

        assertTrue(result.isPresent());
        assertEquals(5, result.get().getRating());
        assertEquals("Excelente conductor", result.get().getComment());
    }

    @Test
    void shouldReturnEmptyWhenIdDoesNotExist() {
        Optional<Review> result = reviewRepository.findById(999L);

        assertFalse(result.isPresent());
    }

    @Test
    void shouldReturnAllReviewsWhenMultipleReviewsExist() {
        reviewRepository.save(review);
        reviewRepository.save(Review.builder()
                .ride(ride)
                .reviewer(reviewed)
                .reviewed(reviewer)
                .rating(4)
                .comment("Buen pasajero")
                .build());

        List<Review> result = reviewRepository.findAll();

        assertEquals(2, result.size());
    }

    @Test
    void shouldReturnEmptyListWhenNoReviewsExist() {
        assertTrue(reviewRepository.findAll().isEmpty());
    }

    @Test
    void shouldUpdateReviewRatingWhenReviewExists() {
        Review saved = reviewRepository.save(review);

        saved.setRating(3);
        saved.setComment("Regular");
        Review updated = reviewRepository.save(saved);

        assertEquals(3, updated.getRating());
        assertEquals("Regular", updated.getComment());
        assertEquals(saved.getId(), updated.getId());
    }

    @Test
    void shouldDeleteReviewWhenReviewExists() {
        Review saved = reviewRepository.save(review);
        Long id = saved.getId();

        reviewRepository.deleteById(id);

        assertFalse(reviewRepository.findById(id).isPresent());
    }

    @Test
    void shouldNotFailWhenDeletingAllReviews() {
        reviewRepository.save(review);

        reviewRepository.deleteAll();

        assertEquals(0, reviewRepository.count());
    }

    @Test
    void shouldReturnTrueWhenReviewExists() {
        Review saved = reviewRepository.save(review);

        assertTrue(reviewRepository.existsById(saved.getId()));
    }

    @Test
    void shouldReturnFalseWhenReviewDoesNotExist() {
        assertFalse(reviewRepository.existsById(999L));
    }

    @Test
    void shouldThrowExceptionWhenSameReviewerReviewsSameUserInSameRideTwice() {
        reviewRepository.save(review);
        Review duplicate = Review.builder()
                .ride(ride)
                .reviewer(reviewer)
                .reviewed(reviewed)
                .rating(3)
                .comment("Segunda review")
                .build();

        assertThrows(Exception.class, () -> reviewRepository.saveAndFlush(duplicate));
    }

    @Test
    void shouldReturnCorrectCountWhenReviewsAreSaved() {
        reviewRepository.save(review);
        reviewRepository.save(Review.builder()
                .ride(ride)
                .reviewer(reviewed)
                .reviewed(reviewer)
                .rating(4)
                .comment("Buen pasajero")
                .build());

        assertEquals(2, reviewRepository.count());
    }
}