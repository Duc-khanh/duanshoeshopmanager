package com.codegym.shoeshopmanager.service;

import com.codegym.shoeshopmanager.model.Cart;
import com.codegym.shoeshopmanager.model.CartItem;
import com.codegym.shoeshopmanager.model.Product;
import com.codegym.shoeshopmanager.model.User;
import com.codegym.shoeshopmanager.repository.CartItemRepository;
import com.codegym.shoeshopmanager.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartService implements ICartService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CartRepository cartRepository;

    @Override
    public void addToCart(User user, Product product, int quantity) {

        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });

        Optional<CartItem> existingItem = cartItemRepository.findByCartAndProduct(cart, product);
        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartItemRepository.save(item);
        } else {
            CartItem newItem = new CartItem(cart, product, quantity);
            cartItemRepository.save(newItem);
        }
    }

    @Override
    public List<CartItem> getCartItems(User user) {
        return cartItemRepository.findByCart_User(user);
    }

    @Override
    public void removeItem(Integer id) {
        cartItemRepository.deleteById(id);
    }
}

