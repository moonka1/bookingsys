package com.booking.service;

import com.booking.dto.accommodation.AccommodationCreateRequest;
import com.booking.dto.accommodation.AccommodationResponse;
import com.booking.entity.Accommodation;
import com.booking.entity.User;
import com.booking.exception.ResourceNotFoundException;
import com.booking.exception.UnauthorizedException;
import com.booking.mapper.AccommodationMapper;
import com.booking.repository.AccommodationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Slf4j
@Transactional
public class AccommodationService {

    private final AccommodationRepository accommodationRepository;
    private final AccommodationMapper accommodationMapper;
    private final UserService userService;

    public AccommodationService(AccommodationRepository accommodationRepository, 
                               AccommodationMapper accommodationMapper,
                               UserService userService) {
        this.accommodationRepository = accommodationRepository;
        this.accommodationMapper = accommodationMapper;
        this.userService = userService;
    }

    public AccommodationResponse createAccommodation(Long ownerId, AccommodationCreateRequest request) {
        log.info("Creating new accommodation for user ID: {}", ownerId);
        
        User owner = userService.getUserEntityById(ownerId);
        Accommodation accommodation = accommodationMapper.toEntity(request);
        accommodation.setOwner(owner);
        accommodation.setIsAvailable(true);
        accommodation.setAverageRating(0.0);

        Accommodation savedAccommodation = accommodationRepository.save(accommodation);
        log.info("Accommodation created successfully with ID: {}", savedAccommodation.getId());
        return accommodationMapper.toResponse(savedAccommodation);
    }

    @Transactional(readOnly = true)
    public AccommodationResponse getAccommodationById(Long id) {
        log.info("Fetching accommodation with ID: {}", id);
        Accommodation accommodation = accommodationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Accommodation", "id", id));
        return accommodationMapper.toResponse(accommodation);
    }

    public AccommodationResponse updateAccommodation(Long id, Long userId, AccommodationCreateRequest request) {
        log.info("Updating accommodation with ID: {}", id);
        
        Accommodation accommodation = accommodationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Accommodation", "id", id));

        if (!accommodation.getOwner().getId().equals(userId)) {
            log.warn("User {} attempted to update accommodation they don't own", userId);
            throw new UnauthorizedException("You can only update your own accommodations");
        }

        accommodation.setTitle(request.getTitle());
        accommodation.setDescription(request.getDescription());
        accommodation.setAddress(request.getAddress());
        accommodation.setCity(request.getCity());
        accommodation.setCountry(request.getCountry());
        accommodation.setZipCode(request.getZipCode());
        accommodation.setLatitude(request.getLatitude());
        accommodation.setLongitude(request.getLongitude());
        accommodation.setPricePerNight(request.getPricePerNight());
        accommodation.setMaxGuests(request.getMaxGuests());
        accommodation.setBedrooms(request.getBedrooms());
        accommodation.setBathrooms(request.getBathrooms());
        accommodation.setType(request.getType());

        Accommodation updatedAccommodation = accommodationRepository.save(accommodation);
        log.info("Accommodation with ID {} updated successfully", id);
        return accommodationMapper.toResponse(updatedAccommodation);
    }

    public void deleteAccommodation(Long id, Long userId) {
        log.info("Deleting accommodation with ID: {}", id);
        
        Accommodation accommodation = accommodationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Accommodation", "id", id));

        if (!accommodation.getOwner().getId().equals(userId)) {
            log.warn("User {} attempted to delete accommodation they don't own", userId);
            throw new UnauthorizedException("You can only delete your own accommodations");
        }

        accommodationRepository.delete(accommodation);
        log.info("Accommodation with ID {} deleted successfully", id);
    }

    @Transactional(readOnly = true)
    public Page<AccommodationResponse> getAccommodationsByOwner(Long ownerId, Pageable pageable) {
        log.info("Fetching accommodations for owner ID: {}", ownerId);
        return accommodationRepository.findByOwnerId(ownerId, pageable)
                .map(accommodationMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<AccommodationResponse> searchAccommodations(String searchTerm, Pageable pageable) {
        log.info("Searching accommodations with term: {}", searchTerm);
        return accommodationRepository.searchByTitleOrDescriptionOrCity(searchTerm, pageable)
                .map(accommodationMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<AccommodationResponse> filterByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        log.info("Filtering accommodations by price range: {} - {}", minPrice, maxPrice);
        return accommodationRepository.findByPriceRange(minPrice, maxPrice, pageable)
                .map(accommodationMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<AccommodationResponse> searchWithFilters(String city, BigDecimal minPrice, BigDecimal maxPrice, 
                                                        Integer guests, Pageable pageable) {
        log.info("Advanced search: city={}, price={}-{}, guests={}", city, minPrice, maxPrice, guests);
        return accommodationRepository.searchWithFilters(city, minPrice, maxPrice, guests, pageable)
                .map(accommodationMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Accommodation getAccommodationEntityById(Long id) {
        return accommodationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Accommodation", "id", id));
    }
}
