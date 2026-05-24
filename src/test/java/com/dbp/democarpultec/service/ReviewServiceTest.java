package com.dbp.democarpultec.service;

import com.dbp.democarpultec.dto.ReviewRequestDto;
import com.dbp.democarpultec.dto.ReviewResponseDto;
import com.dbp.democarpultec.model.Review;
import com.dbp.democarpultec.model.Ride;
import com.dbp.democarpultec.model.User;
import com.dbp.democarpultec.repository.ReviewRepository;
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
class ReviewServiceTest {
    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private RideService rideService;

    @Mock
    private UserService userService;

    @InjectMocks
    private ReviewService reviewService;

    private User reviewer;
    private User reviewed;
    private Ride ride;
    private Review review;
    private ReviewRequestDto requestDto;

    @BeforeEach
    void setUp() {
        reviewer = User.builder()
                .id(1L)
                .name("Juan")
                .build();

        reviewed = User.builder()
                .id(2L)
                .name("Pedro")
                .build();

        ride = Ride.builder()
                .id(1L)
                .destinationOrOrigin("Miraflores")
                .build();

        review = Review.builder()
                .id(1L)
                .ride(ride)
                .reviewer(reviewer)
                .reviewed(reviewed)
                .rating(5)
                .comment("Excelente viaje")
                .createdAt(LocalDateTime.now())
                .build();

        requestDto = ReviewRequestDto.builder()
                .rideId(1L)
                .reviewerId(1L)
                .reviewedId(2L)
                .rating(5)
                .comment("Excelente viaje")
                .build();
    }

    @Test
    void shouldReturnReviewListWhenReviewsExist() {
        when(reviewRepository.findAll()).thenReturn(List.of(review));

        List<ReviewResponseDto> result = reviewService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());

        assertEquals(1L, result.get(0).getRideId());
        assertEquals(1L, result.get(0).getReviewerId());
        assertEquals(2L, result.get(0).getReviewedId());
        assertEquals(5, result.get(0).getRating());
        assertEquals("Excelente viaje", result.get(0).getComment());

        verify(reviewRepository).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoReviewsExist() {

        when(reviewRepository.findAll()).thenReturn(Collections.emptyList());

        List<ReviewResponseDto> result = reviewService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(reviewRepository).findAll();
    }

    @Test
    void shouldReturnReviewWhenIdExists() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        ReviewResponseDto result = reviewService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getRideId());
        assertEquals(1L, result.getReviewerId());
        assertEquals(2L, result.getReviewedId());
        assertEquals(5, result.getRating());
        assertEquals("Excelente viaje", result.getComment());
        assertNotNull(result.getCreatedAt());

        verify(reviewRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenReviewDoesNotExist() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> reviewService.findById(1L));

        verify(reviewRepository).findById(1L);
    }

    @Test
    void shouldCreateReviewWhenValidData() {
        when(rideService.findEntityById(1L)).thenReturn(ride);
        when(userService.findEntityById(1L)).thenReturn(reviewer);
        when(userService.findEntityById(2L)).thenReturn(reviewed);
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        ReviewResponseDto result = reviewService.create(requestDto);

        assertNotNull(result);
        assertEquals(1L, result.getRideId());
        assertEquals(1L, result.getReviewerId());
        assertEquals(2L, result.getReviewedId());
        assertEquals(5, result.getRating());
        assertEquals("Excelente viaje", result.getComment());
        assertNotNull(result.getCreatedAt());

        verify(rideService).findEntityById(1L);
        verify(userService).findEntityById(1L);
        verify(userService).findEntityById(2L);
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void shouldUpdateReviewWhenReviewExists() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(rideService.findEntityById(1L)).thenReturn(ride);
        when(userService.findEntityById(1L)).thenReturn(reviewer);
        when(userService.findEntityById(2L)).thenReturn(reviewed);
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        ReviewResponseDto result = reviewService.update(1L, requestDto);

        assertNotNull(result);
        assertEquals(1L, result.getRideId());
        assertEquals(1L, result.getReviewerId());
        assertEquals(2L, result.getReviewedId());
        assertEquals(5, result.getRating());
        assertEquals("Excelente viaje", result.getComment());

        verify(reviewRepository).findById(1L);
        verify(rideService).findEntityById(1L);
        verify(userService).findEntityById(1L);
        verify(userService).findEntityById(2L);
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingReview() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> reviewService.update(1L, requestDto));

        verify(reviewRepository).findById(1L);
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void shouldDeleteReviewWhenReviewExists() {
        when(reviewRepository.existsById(1L)).thenReturn(true);

        reviewService.delete(1L);

        verify(reviewRepository).existsById(1L);
        verify(reviewRepository).deleteById(1L);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingReview() {
        when(reviewRepository.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> reviewService.delete(1L));

        verify(reviewRepository).existsById(1L);
        verify(reviewRepository, never()).deleteById(anyLong());
    }
}
