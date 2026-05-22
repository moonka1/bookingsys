package com.booking.repository;

import com.booking.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByAccommodationId(Long accommodationId, Pageable pageable);

    Page<Review> findByReviewerId(Long reviewerId, Pageable pageable);

    Optional<Review> findByBookingId(Long bookingId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.accommodation.id = :accommodationId")
    Double getAverageRatingByAccommodation(@Param("accommodationId") Long accommodationId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.accommodation.id = :accommodationId")
    Long getReviewCountByAccommodation(@Param("accommodationId") Long accommodationId);

    @Query("SELECT r FROM Review r WHERE " +
            "r.accommodation.id = :accommodationId AND " +
            "r.rating >= :minRating")
    Page<Review> findByAccommodationIdAndMinRating(@Param("accommodationId") Long accommodationId,
                                                    @Param("minRating") Double minRating,
                                                    Pageable pageable);
}
