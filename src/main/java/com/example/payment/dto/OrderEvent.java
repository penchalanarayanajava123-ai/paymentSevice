package com.example.payment.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import java.math.BigDecimal;

public record OrderEvent(

        @JsonAlias({"id", "orderID"})
        String orderId,

        @JsonAlias({"email", "userEmail"})
        String customerEmail,

        @JsonAlias({"totalAmount", "total", "price", "totalPrice"})
        BigDecimal amount

) {}