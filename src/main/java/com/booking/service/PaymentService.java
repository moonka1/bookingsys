package com.booking.service;

import com.booking.dto.payment.PaymentCreateRequest;
import com.booking.dto.payment.PaymentResponse;
import com.booking.entity.Booking;
import com.booking.entity.Payment;
import com.booking.entity.PaymentStatus;
import com.booking.exception.InvalidRequestException;
import com.booking.exception.ResourceNotFoundException;
import com.booking.mapper.PaymentMapper;
import com.booking.repository.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final BookingService bookingService;

    public PaymentService(PaymentRepository paymentRepository, PaymentMapper paymentMapper,
                         BookingService bookingService) {
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
        this.bookingService = bookingService;
    }

    public PaymentResponse createPayment(PaymentCreateRequest request) {
        log.info("Creating payment for booking ID: {}", request.getBookingId());

        Booking booking = bookingService.getBookingEntityById(request.getBookingId());

        // Check if payment already exists
        if (paymentRepository.findByBookingId(request.getBookingId()).isPresent()) {
            log.warn("Payment already exists for booking {}", request.getBookingId());
            throw new InvalidRequestException("Payment already exists for this booking");
        }

        // Validate amount matches booking total
        if (request.getAmount().compareTo(booking.getTotalPrice()) != 0) {
            log.warn("Payment amount {} does not match booking total {}", request.getAmount(), booking.getTotalPrice());
            throw new InvalidRequestException("Payment amount must match booking total price");
        }

        Payment payment = paymentMapper.toEntity(request);
        payment.setBooking(booking);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setTransactionId(generateTransactionId());

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment created successfully with ID: {} and transaction ID: {}", 
                savedPayment.getId(), savedPayment.getTransactionId());
        return paymentMapper.toResponse(savedPayment);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Long id) {
        log.info("Fetching payment with ID: {}", id);
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));
        return paymentMapper.toResponse(payment);
    }

    public PaymentResponse completePayment(Long id) {
        log.info("Completing payment with ID: {}", id);
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));
        
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setPaymentDate(LocalDateTime.now());
        
        Payment completedPayment = paymentRepository.save(payment);
        log.info("Payment {} completed successfully", id);
        return paymentMapper.toResponse(completedPayment);
    }

    public PaymentResponse refundPayment(Long id) {
        log.info("Refunding payment with ID: {}", id);
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));
        
        payment.setStatus(PaymentStatus.REFUNDED);
        
        Payment refundedPayment = paymentRepository.save(payment);
        log.info("Payment {} refunded successfully", id);
        return paymentMapper.toResponse(refundedPayment);
    }

    @Transactional(readOnly = true)
    public Page<PaymentResponse> getPaymentsByStatus(PaymentStatus status, Pageable pageable) {
        log.info("Fetching payments with status: {}", status);
        return paymentRepository.findByStatus(status, pageable)
                .map(paymentMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByTransactionId(String transactionId) {
        log.info("Fetching payment with transaction ID: {}", transactionId);
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "transactionId", transactionId));
        return paymentMapper.toResponse(payment);
    }

    private String generateTransactionId() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
    }
}
