package com.booking.mapper;

import com.booking.dto.review.ReviewCreateRequest;
import com.booking.dto.review.ReviewResponse;
import com.booking.entity.Review;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    ReviewResponse toResponse(Review review);
    Review toEntity(ReviewCreateRequest request);
}
