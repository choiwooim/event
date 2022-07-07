package com.wooim.event.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

    @Query("SELECT COALESCE(COUNT(*),0) FROM REV m WHERE m.placeId = :id")
    Integer getPlaceCnt (@Param("id") String placeId);

    Optional<Review> findByReviewId(String reviewId);
    Optional<Review> findByPlaceId(String placeId);
    void deleteByReviewId(String reviewId);
}