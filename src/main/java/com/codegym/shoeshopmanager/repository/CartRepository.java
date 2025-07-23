package com.codegym.shoeshopmanager.repository;

import com.codegym.shoeshopmanager.model.Cart;
import com.codegym.shoeshopmanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {
    Optional<Cart> findByUser(User user);
}
