package com.wooim.event.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

    Optional<Review> findByReviewId(String reviewId);
    Optional<Review> findByPlaceId(String placeId);
    void deleteByReviewId(String reviewId);
}