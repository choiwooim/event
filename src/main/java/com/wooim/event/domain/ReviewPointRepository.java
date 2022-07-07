package com.wooim.event.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReviewPointRepository extends JpaRepository<ReviewPoint, Integer> {
    @Query("SELECT SUM(reviewPoint) FROM REV_POINT m WHERE m.reviewId = :id AND m.reviewType = 'PLACE'")
    Integer getPlacePoint (@Param("id") String reviewId);
}