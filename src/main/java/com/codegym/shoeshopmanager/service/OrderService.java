package com.codegym.shoeshopmanager.service;

import com.codegym.shoeshopmanager.model.Cart;
import com.codegym.shoeshopmanager.model.CartItem;
import com.codegym.shoeshopmanager.model.Order;
import com.codegym.shoeshopmanager.model.User;

import java.util.List;

public interface OrderService {
    Order placeOrder(User user, List<CartItem> cartItems);

    List<Order> getOrdersByUser(User user);

    List<CartItem> getCartItems(User user);

    List<CartItem> getCartItemsByIds(List<Integer> selectedItemIds);

    void clearCart(User user);


}
