package com.codegym.shoeshopmanager.model;
import javax.persistence.*;
@Entity
@Table(name = "Cart")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer cartID;

    @ManyToOne
    @JoinColumn(name = "userID")
    private User user;

    public Cart() {
    }

    public Cart(Integer cartID, User user) {
        this.cartID = cartID;
        this.user = user;
    }

    public Cart(User user) {
        this.user = user;
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
}

