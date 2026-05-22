package com.booking.mapper;

import com.booking.dto.user.UserCreateRequest;
import com.booking.dto.user.UserResponse;
import com.booking.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toResponse(User user);
    User toEntity(UserCreateRequest request);
}
