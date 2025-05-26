package com.codegym.shoeshopmanager.service;

import com.codegym.shoeshopmanager.model.CartItem;
import com.codegym.shoeshopmanager.model.Product;
import com.codegym.shoeshopmanager.model.User;

import java.util.List;

public interface ICartService {
    void addToCart(User user, Product product, int quantity);
    List<CartItem> getCartItems(User user);
    void removeItem(Integer id);

}
