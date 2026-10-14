package com.paymentsystem.ticketservice.repository;

import com.paymentsystem.ticketservice.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

    Optional<Order> findByPaymentId(String paymentId);
}
