package com.booking.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequest {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String bio;
    private String profileImage;
}
