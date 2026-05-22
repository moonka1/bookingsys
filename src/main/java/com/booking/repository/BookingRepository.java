package com.booking.repository;

import com.booking.entity.Booking;
import com.booking.entity.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> findByConfirmationCode(String confirmationCode);

    Page<Booking> findByUserId(Long userId, Pageable pageable);

    Page<Booking> findByAccommodationId(Long accommodationId, Pageable pageable);

    Page<Booking> findByStatus(BookingStatus status, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE " +
            "b.accommodation.id = :accommodationId AND " +
            "b.status IN (com.booking.entity.BookingStatus.CONFIRMED, com.booking.entity.BookingStatus.COMPLETED) AND " +
            "((b.checkInDate <= :checkOutDate AND b.checkOutDate >= :checkInDate))")
    List<Booking> findConflictingBookings(@Param("accommodationId") Long accommodationId,
                                          @Param("checkInDate") LocalDate checkInDate,
                                          @Param("checkOutDate") LocalDate checkOutDate);

    @Query("SELECT b FROM Booking b WHERE " +
            "b.user.id = :userId AND " +
            "b.checkInDate >= :fromDate AND " +
            "b.checkOutDate <= :toDate")
    Page<Booking> findByUserIdAndDateRange(@Param("userId") Long userId,
                                           @Param("fromDate") LocalDate fromDate,
                                           @Param("toDate") LocalDate toDate,
                                           Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE " +
            "b.accommodation.id = :accommodationId AND " +
            "b.checkInDate >= :fromDate AND " +
            "b.checkOutDate <= :toDate")
    Page<Booking> findByAccommodationIdAndDateRange(@Param("accommodationId") Long accommodationId,
                                                     @Param("fromDate") LocalDate fromDate,
                                                     @Param("toDate") LocalDate toDate,
                                                     Pageable pageable);
}
