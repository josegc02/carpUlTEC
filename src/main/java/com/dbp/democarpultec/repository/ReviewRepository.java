package com.dbp.democarpultec.repository;

import com.dbp.democarpultec.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    boolean existsByRide_IdAndReviewer_IdAndReviewed_Id(Long rideId, Long reviewerId, Long reviewedId);

    @Query("select avg(r.rating) from Review r where r.reviewed.id = :reviewedId")
    Double averageRatingByReviewedId(Long reviewedId);
}
