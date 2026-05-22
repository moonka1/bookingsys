package com.booking.mapper;

import com.booking.dto.accommodation.AccommodationCreateRequest;
import com.booking.dto.accommodation.AccommodationResponse;
import com.booking.entity.Accommodation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccommodationMapper {
    AccommodationResponse toResponse(Accommodation accommodation);
    Accommodation toEntity(AccommodationCreateRequest request);
}
