package com.codegym.shoeshopmanager.service;

import com.codegym.shoeshopmanager.model.CartItem;
import com.codegym.shoeshopmanager.model.Order;
import com.codegym.shoeshopmanager.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {


    Order placeOrder(User user, List<CartItem> items);

    List<Order> getOrdersByUser(User user);

    List<CartItem> getCartItems(User user);

    List<CartItem> getCartItemsByIds(List<Integer> selectedItemIds);

    void clearCart(User user);


    List<Order> findAll();

    void updateStatus(Integer orderId, String status);

    Order findById(Integer id);

    Order findOrderWithDetails(Integer id);

    List<Order> getOrdersByUserAndStatus(User user, String status);

    Page<Order> findByUser(User user, Pageable pageable);

    Page<Order> getOrdersByUserAndStatus(User user, String status, Pageable pageable);

    Page<Order> getOrdersByUser(User user, Pageable pageable);

    Page<Order> findPaginated(Pageable pageable);


}
