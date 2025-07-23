package com.codegym.shoeshopmanager.repository;

import com.codegym.shoeshopmanager.model.Order;
import com.codegym.shoeshopmanager.model.OrderStatus;
import com.codegym.shoeshopmanager.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByUser(User user);
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderDetails d LEFT JOIN FETCH d.product WHERE o.orderID = :orderID")
    Optional<Order> findOrderWithDetails(@Param("orderID") Integer orderID);
    List<Order> findByUserAndStatus(User user, OrderStatus status);



}
