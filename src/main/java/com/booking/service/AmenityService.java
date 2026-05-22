package com.booking.service;

import com.booking.dto.amenity.AmenityCreateRequest;
import com.booking.dto.amenity.AmenityResponse;
import com.booking.entity.Amenity;
import com.booking.exception.DuplicateResourceException;
import com.booking.exception.ResourceNotFoundException;
import com.booking.mapper.AmenityMapper;
import com.booking.repository.AmenityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
public class AmenityService {

    private final AmenityRepository amenityRepository;
    private final AmenityMapper amenityMapper;

    public AmenityService(AmenityRepository amenityRepository, AmenityMapper amenityMapper) {
        this.amenityRepository = amenityRepository;
        this.amenityMapper = amenityMapper;
    }

    public AmenityResponse createAmenity(AmenityCreateRequest request) {
        log.info("Creating amenity: {}", request.getName());
        
        if (amenityRepository.findByName(request.getName()).isPresent()) {
            log.warn("Amenity with name {} already exists", request.getName());
            throw new DuplicateResourceException("Amenity with name " + request.getName() + " already exists");
        }

        Amenity amenity = amenityMapper.toEntity(request);
        Amenity savedAmenity = amenityRepository.save(amenity);
        log.info("Amenity created successfully with ID: {}", savedAmenity.getId());
        return amenityMapper.toResponse(savedAmenity);
    }

    @Transactional(readOnly = true)
    public AmenityResponse getAmenityById(Long id) {
        log.info("Fetching amenity with ID: {}", id);
        Amenity amenity = amenityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Amenity", "id", id));
        return amenityMapper.toResponse(amenity);
    }

    public AmenityResponse updateAmenity(Long id, AmenityCreateRequest request) {
        log.info("Updating amenity with ID: {}", id);
        Amenity amenity = amenityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Amenity", "id", id));

        amenity.setName(request.getName());
        amenity.setDescription(request.getDescription());
        amenity.setIconUrl(request.getIconUrl());

        Amenity updatedAmenity = amenityRepository.save(amenity);
        log.info("Amenity with ID {} updated successfully", id);
        return amenityMapper.toResponse(updatedAmenity);
    }

    public void deleteAmenity(Long id) {
        log.info("Deleting amenity with ID: {}", id);
        Amenity amenity = amenityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Amenity", "id", id));
        amenityRepository.delete(amenity);
        log.info("Amenity with ID {} deleted successfully", id);
    }

    @Transactional(readOnly = true)
    public Page<AmenityResponse> getAllAmenities(Pageable pageable) {
        log.info("Fetching all amenities with pagination");
        return amenityRepository.findAll(pageable).map(amenityMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<AmenityResponse> searchAmenities(String name, Pageable pageable) {
        log.info("Searching amenities with name: {}", name);
        return amenityRepository.findByNameContainingIgnoreCase(name, pageable)
                .map(amenityMapper::toResponse);
    }
}
