package com.codegym.shoeshopmanager.service;

import com.codegym.shoeshopmanager.model.*;
import com.codegym.shoeshopmanager.repository.CartItemRepository;
import com.codegym.shoeshopmanager.repository.OrderDetailRepository;
import com.codegym.shoeshopmanager.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private com.codegym.shoeshopmanager.service.CartService cartService;

    @Autowired
    private CartItemRepository cartItemRepository;


    @Override
    public Order placeOrder(User user, List<CartItem> cartItems) {
        if (user == null || cartItems == null || cartItems.isEmpty()) {
            throw new IllegalArgumentException("Người dùng hoặc giỏ hàng không hợp lệ.");
        }

        Order order = new Order();
        order.setUser(user);
        order.setCurrentTime(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);

        order = orderRepository.save(order);

        double totalAmount = 0;

        for (CartItem item : cartItems) {
            Product product = item.getProduct();

            if (product == null || product.getProductID() == null) {
                throw new IllegalArgumentException("Sản phẩm không hợp lệ: " + product);
            }

            int quantity = item.getQuantity();
            int discountPercent = (product.getDiscountPercent() != null) ? product.getDiscountPercent() : 0;
            double discountedPrice = product.getPrice() * (100 - discountPercent) / 100.0;

            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setProduct(product);
            detail.setQuantity(quantity);
            detail.setPrice(discountedPrice);

            orderDetailRepository.save(detail);
            totalAmount += quantity * discountedPrice;
        }

        order.setTotalAmount(totalAmount);
        orderRepository.save(order);

        return order;
    }


    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Order findById(Integer id) {
        return orderRepository.findById(id).orElse(null);
    }

    public void updateStatus(Integer id, String status) {
        Order order = findById(id);
        if (order != null) {
            order.setStatus(OrderStatus.valueOf(status));
            orderRepository.save(order);
        }
    }
    public Order findOrderWithDetails(Integer id) {
        return orderRepository.findOrderWithDetails(id).orElse(null);
    }


    @Override
    public List<Order> getOrdersByUser(User user) {
        return orderRepository.findByUser(user);
    }

    @Override
    public List<CartItem> getCartItems(User user) {
        return cartService.getCartItems(user);
    }

    @Override
    public List<CartItem> getCartItemsByIds(List<Integer> itemID) {
        return cartItemRepository.findAllById(itemID);
    }

    @Override
    public void clearCart(User user) {
        cartService.clearCart(user);
    }
    public List<Order> getOrdersByUserAndStatus(User user, String status) {
        return orderRepository.findByUserAndStatus(user, OrderStatus.valueOf(status));
    }

    @Override
    public Page<Order> findByUser(User user, Pageable pageable) {
        return null;
    }

    @Override
    public Page<Order> getOrdersByUserAndStatus(User user, String status, Pageable pageable) {
        OrderStatus orderStatus = OrderStatus.valueOf(status);
        return orderRepository.findByUserAndStatus(user, orderStatus, pageable);
    }

    @Override
    public Page<Order> getOrdersByUser(User user, Pageable pageable) {
        return orderRepository.findByUser(user, pageable);
    }




}
