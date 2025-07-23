//package com.codegym.shoeshopmanager.model;
//
//public enum OrderStatus {
//    PENDING, CONFIRMED, SHIPPED, CANCELLED
//}
//
package com.codegym.shoeshopmanager.model;

public enum OrderStatus {
    PENDING("Chờ xác nhận"),
    CONFIRMED("Đã xác nhận"),
    SHIPPED("Đang giao"),
    CANCELLED("Đã huỷ");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
