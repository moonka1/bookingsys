package com.booking.dto.accommodation;

import com.booking.entity.AccommodationType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccommodationCreateRequest {
    @NotBlank(message = "Title is required")
    @Size(min = 5, max = 255)
    private String title;

    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 3000)
    private String description;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "Country is required")
    private String country;

    @NotBlank(message = "Zip code is required")
    private String zipCode;

    @NotNull(message = "Latitude is required")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    private Double longitude;

    @NotNull(message = "Price per night is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal pricePerNight;

    @NotNull(message = "Max guests is required")
    private Integer maxGuests;

    @NotNull(message = "Bedrooms is required")
    private Integer bedrooms;

    @NotNull(message = "Bathrooms is required")
    private Integer bathrooms;

    @NotNull(message = "Type is required")
    private AccommodationType type;
}
