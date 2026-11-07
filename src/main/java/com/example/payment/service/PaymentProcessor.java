package com.example.payment.service;

import com.example.payment.dto.OrderEvent;
import com.example.payment.dto.PaymentEvent;
import com.example.payment.entity.Payment;
import com.example.payment.repository.PaymentRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentProcessor {

	private static final Logger log = LoggerFactory.getLogger(PaymentProcessor.class);

	private final PaymentRepository paymentRepository;
	private final KafkaTemplate<String, Object> kafkaTemplate;
	private final String paymentTopic;

	public PaymentProcessor(PaymentRepository paymentRepository, KafkaTemplate<String, Object> kafkaTemplate,
			@Value("${app.kafka.payment-topic}") String paymentTopic) {

		this.paymentRepository = paymentRepository;
		this.kafkaTemplate = kafkaTemplate;
		this.paymentTopic = paymentTopic;
	}

	@KafkaListener(topics = "${app.kafka.order-topic}", groupId = "${spring.kafka.consumer.group-id}")
	@Transactional
	public void onOrderEvent(OrderEvent event) {

		if (event == null || event.orderId() == null) {
			throw new IllegalArgumentException("Invalid order event");
		}

		log.info("Received order: {}", event.orderId());

		// Prevent duplicate payments
		if (paymentRepository.existsByOrderId(event.orderId())) {
			log.info("Payment already exists for order: {}", event.orderId());
			return;
		}

		// Automatically process payment
		Payment payment = new Payment();

		payment.setOrderId(event.orderId());
		payment.setCustomerEmail(event.customerEmail());
		payment.setAmount(event.amount());
		payment.setStatus("COMPLETED");
		payment.setReason(null);

		paymentRepository.save(payment);

		PaymentEvent result = new PaymentEvent(payment.getOrderId(), payment.getCustomerEmail(), payment.getAmount(),
				"COMPLETED", null);

		kafkaTemplate.send(paymentTopic, payment.getOrderId(), result);

		log.info("Payment completed for order: {}", payment.getOrderId());
	}
}