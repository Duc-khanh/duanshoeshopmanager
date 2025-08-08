package com.codegym.shoeshopmanager.model;
import javax.persistence.*;
@Entity
@Table(name = "CartItems")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer itemID;

    @ManyToOne
    @JoinColumn(name = "cartID")
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "productID")
    private Product product;

    private Integer quantity;
    @Transient
    public Double getDiscountedPrice() {
        if (product.getDiscountPercent() != null && product.getDiscountPercent() > 0) {
            return product.getPrice() * (1 - product.getDiscountPercent() / 100.0);
        }
        return product.getPrice();
    }

    public CartItem() {
    }
    public CartItem(Cart cart, Product product, Integer quantity) {
        this.cart = cart;
        this.product = product;
        this.quantity = quantity;
    }



    public CartItem(Integer itemID, Cart cart, Product product, Integer quantity) {
        this.itemID = itemID;
        this.cart = cart;
        this.product = product;
        this.quantity = quantity;
    }

    public Integer getItemID() {
        return itemID;
    }

    public void setItemID(Integer itemID) {
        this.itemID = itemID;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "itemID=" + itemID +
                ", cart=" + cart +
                ", product=" + product +
                ", quantity=" + quantity +
                '}';
    }
}

