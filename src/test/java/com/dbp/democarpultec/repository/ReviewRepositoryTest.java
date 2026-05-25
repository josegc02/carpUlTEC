package com.dbp.democarpultec.repository;

import com.dbp.democarpultec.PostgresContainerTest;
import com.dbp.democarpultec.model.Publication;
import com.dbp.democarpultec.model.Review;
import com.dbp.democarpultec.model.Ride;
import com.dbp.democarpultec.model.User;
import com.dbp.democarpultec.model.Vehicle;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class ReviewRepositoryTest extends PostgresContainerTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private PublicationRepository publicationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Test
    void shouldSaveReviewWhenValidData() {
        Ride ride = saveRide();
        User reviewer = userRepository.save(RepositoryTestFactory.user("reviewer"));
        User reviewed = userRepository.save(RepositoryTestFactory.user("reviewed"));
        Review review = RepositoryTestFactory.review(ride, reviewer, reviewed);

        Review savedReview = reviewRepository.save(review);

        assertNotNull(savedReview.getId());
        assertEquals(5, savedReview.getRating());
        assertEquals(ride.getId(), savedReview.getRide().getId());
    }

    @Test
    void shouldDeleteReviewWhenReviewExists() {
        Ride ride = saveRide();
        User reviewer = userRepository.save(RepositoryTestFactory.user("deleteReviewer"));
        User reviewed = userRepository.save(RepositoryTestFactory.user("deleteReviewed"));
        Review savedReview = reviewRepository.save(RepositoryTestFactory.review(ride, reviewer, reviewed));

        reviewRepository.deleteById(savedReview.getId());

        assertTrue(reviewRepository.findById(savedReview.getId()).isEmpty());
    }

    @Test
    void shouldReturnEmptyWhenReviewDoesNotExist() {
        assertTrue(reviewRepository.findById(999L).isEmpty());
    }

    private Ride saveRide() {
        User author = userRepository.save(RepositoryTestFactory.user("reviewRideAuthor"));
        User driver = userRepository.save(RepositoryTestFactory.user("reviewRideDriver"));
        Vehicle vehicle = vehicleRepository.save(RepositoryTestFactory.vehicle(driver, "REV"));
        Publication publication = publicationRepository.save(RepositoryTestFactory.publication(author));
        return rideRepository.save(RepositoryTestFactory.ride(publication, driver, vehicle));
    }
}
