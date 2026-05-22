package com.booking.mapper;

import com.booking.dto.booking.BookingCreateRequest;
import com.booking.dto.booking.BookingResponse;
import com.booking.entity.Booking;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    BookingResponse toResponse(Booking booking);
    Booking toEntity(BookingCreateRequest request);
}
