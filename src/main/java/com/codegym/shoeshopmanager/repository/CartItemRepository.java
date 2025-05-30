package com.codegym.shoeshopmanager.repository;

import com.codegym.shoeshopmanager.model.Cart;
import com.codegym.shoeshopmanager.model.CartItem;
import com.codegym.shoeshopmanager.model.Product;
import com.codegym.shoeshopmanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {




        Optional<CartItem> findByCartAndProduct(Cart cart, Product product);

        List<CartItem> findByCart_User(User user);

        List<CartItem> findAllByItemIDIn(List<Integer> ids);

        void deleteAllByItemIDIn(List<Integer> ids);

        int countByCart_User(User user);



}

