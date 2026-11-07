package com.example.payment.dto;

import java.math.BigDecimal;

// Published to payment-events topic.
// status = COMPLETED or FAILED.

public record PaymentEvent(
        String orderId,
        String customerEmail,
        BigDecimal amount,
        String status,
        String reason
) {}