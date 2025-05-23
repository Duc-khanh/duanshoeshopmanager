package com.codegym.shoeshopmanager.model;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.security.Timestamp;
@Entity
@Table(name = "Cart")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer cartID;

    @ManyToOne
    @JoinColumn(name = "userID")
    private User user;

    @CreationTimestamp
    private Timestamp createdAt;

    public Cart() {
    }

    public Cart(User user, Timestamp createdAt) {
        this.user = user;
        this.createdAt = createdAt;
    }

    public Cart(Integer cartID, User user, Timestamp createdAt) {
        this.cartID = cartID;
        this.user = user;
        this.createdAt = createdAt;
    }

    public Integer getCartID() {
        return cartID;
    }

    public void setCartID(Integer cartID) {
        this.cartID = cartID;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}

