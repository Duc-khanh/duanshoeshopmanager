package com.codegym.shoeshopmanager.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderID;
    @ManyToOne
    @JoinColumn(name = "userID")
    private User user;
    LocalDateTime currentTime = LocalDateTime.now();

    private Double totalAmount;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderDetail> orderDetails;

    public Order() {
    }

    public Order(User user, LocalDateTime currentTime, Double totalAmount, OrderStatus status) {
        this.user = user;
        this.currentTime = currentTime;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public Order(Integer orderID, User user, LocalDateTime currentTime, Double totalAmount, OrderStatus status) {
        this.orderID = orderID;
        this.user = user;
        this.currentTime = currentTime;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public Order(Integer orderID, User user, LocalDateTime currentTime, Double totalAmount, OrderStatus status, List<OrderDetail> orderDetails) {
        this.orderID = orderID;
        this.user = user;
        this.currentTime = currentTime;
        this.totalAmount = totalAmount;
        this.status = status;
        this.orderDetails = orderDetails;
    }

    public List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public Integer getOrderID() {
        return orderID;
    }

    public void setOrderID(Integer orderID) {
        this.orderID = orderID;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(LocalDateTime currentTime) {
        this.currentTime = currentTime;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
    @Transient
    public int getTotalItems() {
        if (orderDetails == null || orderDetails.isEmpty()) return 0;
        return orderDetails.stream()
                .mapToInt(OrderDetail::getQuantity)
                .sum();
    }



    @Override
    public String toString() {
        return "Order{" +
                "orderID=" + orderID +
                ", user=" + user +
                ", currentTime=" + currentTime +
                ", totalAmount=" + totalAmount +
                ", status=" + status +
                '}';
    }


}
