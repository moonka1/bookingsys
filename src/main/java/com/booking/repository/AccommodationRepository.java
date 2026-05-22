package com.booking.repository;

import com.booking.entity.Accommodation;
import com.booking.entity.AccommodationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {

    Page<Accommodation> findByOwnerId(Long ownerId, Pageable pageable);

    Page<Accommodation> findByIsAvailable(Boolean isAvailable, Pageable pageable);

    Page<Accommodation> findByType(AccommodationType type, Pageable pageable);

    Page<Accommodation> findByCity(String city, Pageable pageable);

    Page<Accommodation> findByCountry(String country, Pageable pageable);

    Page<Accommodation> findByMaxGuestsGreaterThanEqual(Integer maxGuests, Pageable pageable);

    @Query("SELECT a FROM Accommodation a WHERE " +
            "LOWER(a.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(a.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(a.city) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Accommodation> searchByTitleOrDescriptionOrCity(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT a FROM Accommodation a WHERE " +
            "a.pricePerNight BETWEEN :minPrice AND :maxPrice AND " +
            "a.isAvailable = true")
    Page<Accommodation> findByPriceRange(@Param("minPrice") BigDecimal minPrice,
                                         @Param("maxPrice") BigDecimal maxPrice,
                                         Pageable pageable);

    @Query("SELECT a FROM Accommodation a WHERE " +
            "a.city = :city AND " +
            "a.pricePerNight BETWEEN :minPrice AND :maxPrice AND " +
            "a.maxGuests >= :guests AND " +
            "a.isAvailable = true")
    Page<Accommodation> searchWithFilters(@Param("city") String city,
                                          @Param("minPrice") BigDecimal minPrice,
                                          @Param("maxPrice") BigDecimal maxPrice,
                                          @Param("guests") Integer guests,
                                          Pageable pageable);

    List<Accommodation> findByAverageRatingGreaterThanEqual(Double minRating);
}
