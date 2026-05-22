package com.booking.mapper;

import com.booking.dto.payment.PaymentCreateRequest;
import com.booking.dto.payment.PaymentResponse;
import com.booking.entity.Payment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    PaymentResponse toResponse(Payment payment);
    Payment toEntity(PaymentCreateRequest request);
}
