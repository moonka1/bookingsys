package com.booking.dto.payment;

import com.booking.entity.PaymentMethod;
import com.booking.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {
    private Long id;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private String transactionId;
    private LocalDateTime paymentDate;
    private String notes;
    private Long bookingId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
