package com.booking.dto.review;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewCreateRequest {
    @NotNull(message = "Booking ID is required")
    private Long bookingId;

    @NotNull(message = "Rating is required")
    @DecimalMin(value = "1.0", message = "Rating must be at least 1")
    @DecimalMax(value = "5.0", message = "Rating must not exceed 5")
    private Double rating;

    private String comment;

    @NotNull(message = "Cleanliness rating is required")
    @DecimalMin(value = "1.0")
    @DecimalMax(value = "5.0")
    private Integer cleanliness;

    @NotNull(message = "Communication rating is required")
    @DecimalMin(value = "1.0")
    @DecimalMax(value = "5.0")
    private Integer communication;

    @NotNull(message = "Accuracy rating is required")
    @DecimalMin(value = "1.0")
    @DecimalMax(value = "5.0")
    private Integer accuracy;

    @NotNull(message = "Location rating is required")
    @DecimalMin(value = "1.0")
    @DecimalMax(value = "5.0")
    private Integer location;

    @NotNull(message = "Value rating is required")
    @DecimalMin(value = "1.0")
    @DecimalMax(value = "5.0")
    private Integer value;
}
