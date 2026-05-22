package com.booking.dto.review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponse {
    private Long id;
    private Double rating;
    private String comment;
    private Integer cleanliness;
    private Integer communication;
    private Integer accuracy;
    private Integer location;
    private Integer value;
    private Long reviewerId;
    private Long accommodationId;
    private Long bookingId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
