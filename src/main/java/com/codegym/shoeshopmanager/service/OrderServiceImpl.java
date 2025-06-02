package com.codegym.shoeshopmanager.service;

import com.codegym.shoeshopmanager.model.*;
import com.codegym.shoeshopmanager.repository.CartItemRepository;
import com.codegym.shoeshopmanager.repository.OrderDetailRepository;
import com.codegym.shoeshopmanager.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
    public void placeOrder(User user, List<CartItem> cartItems) {
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
            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setProduct(item.getProduct());
            detail.setQuantity(item.getQuantity());
            detail.setPrice(item.getProduct().getPrice());

            orderDetailRepository.save(detail);

            totalAmount += item.getQuantity() * item.getProduct().getPrice();
        }

        order.setTotalAmount(totalAmount);
        orderRepository.save(order);


        List<Integer> ids = cartItems.stream()
                .map(CartItem::getItemID)
                .collect(Collectors.toList());
        cartItemRepository.deleteAllById(ids);
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

}
