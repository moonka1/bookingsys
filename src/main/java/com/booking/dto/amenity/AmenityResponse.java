package com.booking.dto.amenity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AmenityResponse {
    private Long id;
    private String name;
    private String description;
    private String iconUrl;
    private LocalDateTime createdAt;
}
