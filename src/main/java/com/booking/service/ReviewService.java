package com.booking.service;

import com.booking.dto.review.ReviewCreateRequest;
import com.booking.dto.review.ReviewResponse;
import com.booking.entity.Accommodation;
import com.booking.entity.Booking;
import com.booking.entity.Review;
import com.booking.entity.User;
import com.booking.exception.InvalidRequestException;
import com.booking.exception.ResourceNotFoundException;
import com.booking.mapper.ReviewMapper;
import com.booking.repository.ReviewRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final BookingService bookingService;
    private final UserService userService;
    private final AccommodationService accommodationService;

    public ReviewService(ReviewRepository reviewRepository, ReviewMapper reviewMapper,
                        BookingService bookingService, UserService userService,
                        AccommodationService accommodationService) {
        this.reviewRepository = reviewRepository;
        this.reviewMapper = reviewMapper;
        this.bookingService = bookingService;
        this.userService = userService;
        this.accommodationService = accommodationService;
    }

    public ReviewResponse createReview(ReviewCreateRequest request) {
        log.info("Creating review for booking ID: {}", request.getBookingId());

        Booking booking = bookingService.getBookingEntityById(request.getBookingId());
        
        // Check if review already exists for this booking
        if (reviewRepository.findByBookingId(request.getBookingId()).isPresent()) {
            log.warn("Review already exists for booking {}", request.getBookingId());
            throw new InvalidRequestException("Review already exists for this booking");
        }

        Review review = reviewMapper.toEntity(request);
        review.setBooking(booking);
        review.setReviewer(booking.getUser());
        review.setAccommodation(booking.getAccommodation());

        Review savedReview = reviewRepository.save(review);
        updateAccommodationRating(booking.getAccommodation().getId());
        
        log.info("Review created successfully with ID: {}", savedReview.getId());
        return reviewMapper.toResponse(savedReview);
    }

    @Transactional(readOnly = true)
    public ReviewResponse getReviewById(Long id) {
        log.info("Fetching review with ID: {}", id);
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", id));
        return reviewMapper.toResponse(review);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getAccommodationReviews(Long accommodationId, Pageable pageable) {
        log.info("Fetching reviews for accommodation ID: {}", accommodationId);
        return reviewRepository.findByAccommodationId(accommodationId, pageable)
                .map(reviewMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getUserReviews(Long userId, Pageable pageable) {
        log.info("Fetching reviews from user ID: {}", userId);
        return reviewRepository.findByReviewerId(userId, pageable)
                .map(reviewMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getHighRatedReviews(Long accommodationId, Double minRating, Pageable pageable) {
        log.info("Fetching reviews for accommodation {} with minimum rating {}", accommodationId, minRating);
        return reviewRepository.findByAccommodationIdAndMinRating(accommodationId, minRating, pageable)
                .map(reviewMapper::toResponse);
    }

    private void updateAccommodationRating(Long accommodationId) {
        Double avgRating = reviewRepository.getAverageRatingByAccommodation(accommodationId);
        if (avgRating != null) {
            Accommodation accommodation = accommodationService.getAccommodationEntityById(accommodationId);
            accommodation.setAverageRating(avgRating);
            log.info("Updated average rating for accommodation {}: {}", accommodationId, avgRating);
        }
    }
}
