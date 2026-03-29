package com.webproj.ecom.proj.repo;

import com.webproj.ecom.proj.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}