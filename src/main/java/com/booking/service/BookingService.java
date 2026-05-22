package com.booking.service;

import com.booking.dto.booking.BookingCreateRequest;
import com.booking.dto.booking.BookingResponse;
import com.booking.entity.Accommodation;
import com.booking.entity.Booking;
import com.booking.entity.BookingStatus;
import com.booking.entity.User;
import com.booking.exception.InvalidRequestException;
import com.booking.exception.ResourceNotFoundException;
import com.booking.mapper.BookingMapper;
import com.booking.repository.BookingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@Slf4j
@Transactional
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final UserService userService;
    private final AccommodationService accommodationService;

    public BookingService(BookingRepository bookingRepository, BookingMapper bookingMapper,
                         UserService userService, AccommodationService accommodationService) {
        this.bookingRepository = bookingRepository;
        this.bookingMapper = bookingMapper;
        this.userService = userService;
        this.accommodationService = accommodationService;
    }

    public BookingResponse createBooking(Long userId, BookingCreateRequest request) {
        log.info("Creating booking for user ID: {}", userId);

        // Validate dates
        if (request.getCheckInDate().isBefore(LocalDate.now())) {
            log.warn("Check-in date cannot be in the past");
            throw new InvalidRequestException("Check-in date cannot be in the past");
        }
        if (!request.getCheckOutDate().isAfter(request.getCheckInDate())) {
            log.warn("Check-out date must be after check-in date");
            throw new InvalidRequestException("Check-out date must be after check-in date");
        }

        User user = userService.getUserEntityById(userId);
        Accommodation accommodation = accommodationService.getAccommodationEntityById(request.getAccommodationId());

        // Check for conflicting bookings
        var conflictingBookings = bookingRepository.findConflictingBookings(
                accommodation.getId(), request.getCheckInDate(), request.getCheckOutDate());
        if (!conflictingBookings.isEmpty()) {
            log.warn("Accommodation is not available for the requested dates");
            throw new InvalidRequestException("Accommodation is not available for the requested dates");
        }

        // Validate number of guests
        if (request.getNumberOfGuests() > accommodation.getMaxGuests()) {
            log.warn("Number of guests exceeds maximum");
            throw new InvalidRequestException("Number of guests exceeds maximum for this accommodation");
        }

        Booking booking = bookingMapper.toEntity(request);
        booking.setUser(user);
        booking.setAccommodation(accommodation);
        booking.setStatus(BookingStatus.PENDING);
        booking.setConfirmationCode(generateConfirmationCode());

        // Calculate total price
        long nights = java.time.temporal.ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());
        var totalPrice = accommodation.getPricePerNight().multiply(java.math.BigDecimal.valueOf(nights));
        booking.setTotalPrice(totalPrice);

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Booking created successfully with ID: {} and confirmation code: {}", 
                savedBooking.getId(), savedBooking.getConfirmationCode());
        return bookingMapper.toResponse(savedBooking);
    }

    @Transactional(readOnly = true)
    public BookingResponse getBookingById(Long id) {
        log.info("Fetching booking with ID: {}", id);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", id));
        return bookingMapper.toResponse(booking);
    }

    public BookingResponse updateBookingStatus(Long id, BookingStatus status) {
        log.info("Updating booking {} status to {}", id, status);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", id));
        booking.setStatus(status);
        Booking updatedBooking = bookingRepository.save(booking);
        return bookingMapper.toResponse(updatedBooking);
    }

    public void cancelBooking(Long id) {
        log.info("Cancelling booking with ID: {}", id);
        BookingResponse bookingResponse = updateBookingStatus(id, BookingStatus.CANCELLED);
        log.info("Booking {} cancelled successfully", id);
    }

    @Transactional(readOnly = true)
    public Page<BookingResponse> getUserBookings(Long userId, Pageable pageable) {
        log.info("Fetching bookings for user ID: {}", userId);
        return bookingRepository.findByUserId(userId, pageable)
                .map(bookingMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<BookingResponse> getAccommodationBookings(Long accommodationId, Pageable pageable) {
        log.info("Fetching bookings for accommodation ID: {}", accommodationId);
        return bookingRepository.findByAccommodationId(accommodationId, pageable)
                .map(bookingMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<BookingResponse> getBookingsByStatus(BookingStatus status, Pageable pageable) {
        log.info("Fetching bookings with status: {}", status);
        return bookingRepository.findByStatus(status, pageable)
                .map(bookingMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public BookingResponse getBookingByConfirmationCode(String confirmationCode) {
        log.info("Fetching booking with confirmation code: {}", confirmationCode);
        Booking booking = bookingRepository.findByConfirmationCode(confirmationCode)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "confirmationCode", confirmationCode));
        return bookingMapper.toResponse(booking);
    }

    private String generateConfirmationCode() {
        return "BK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    @Transactional(readOnly = true)
    public Booking getBookingEntityById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", id));
    }
}
