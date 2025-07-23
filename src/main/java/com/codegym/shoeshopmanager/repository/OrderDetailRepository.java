package com.codegym.shoeshopmanager.repository;
import com.codegym.shoeshopmanager.model.Order;
import com.codegym.shoeshopmanager.model.OrderDetail;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
    List<OrderDetail> findByOrder(Order order);

}

