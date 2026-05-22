package com.booking.mapper;

import com.booking.dto.amenity.AmenityCreateRequest;
import com.booking.dto.amenity.AmenityResponse;
import com.booking.entity.Amenity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AmenityMapper {
    AmenityResponse toResponse(Amenity amenity);
    Amenity toEntity(AmenityCreateRequest request);
}
