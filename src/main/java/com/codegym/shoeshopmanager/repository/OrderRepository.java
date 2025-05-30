package com.codegym.shoeshopmanager.repository;

import com.codegym.shoeshopmanager.model.Order;
import com.codegym.shoeshopmanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByUser(User user);
}
