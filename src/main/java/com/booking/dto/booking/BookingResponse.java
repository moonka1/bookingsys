package com.booking.dto.booking;

import com.booking.entity.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponse {
    private Long id;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer numberOfGuests;
    private BigDecimal totalPrice;
    private BookingStatus status;
    private String specialRequests;
    private String confirmationCode;
    private Long userId;
    private Long accommodationId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
