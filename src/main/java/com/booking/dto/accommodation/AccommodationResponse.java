package com.booking.dto.accommodation;

import com.booking.entity.AccommodationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccommodationResponse {
    private Long id;
    private String title;
    private String description;
    private String address;
    private String city;
    private String country;
    private String zipCode;
    private Double latitude;
    private Double longitude;
    private BigDecimal pricePerNight;
    private Integer maxGuests;
    private Integer bedrooms;
    private Integer bathrooms;
    private String mainImage;
    private AccommodationType type;
    private Boolean isAvailable;
    private Double averageRating;
    private Long ownerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
