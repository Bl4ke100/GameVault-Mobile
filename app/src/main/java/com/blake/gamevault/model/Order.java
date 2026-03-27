package com.blake.gamevault.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private String orderId;
    private String userId;
    private double totalAmount;
    private String status;
    private long orderDate;
    private  List<OrderItem> orderItems;
    private Address billingAddress;


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderItem {
        private String gameId;
        private double unitPrice;
        private int qty;
        private List<OrderItem.Attribute> attributes;

        @Data
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        public static class Attribute {
            private String name;
            private String value;
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Address {

        private String fullName;
        private String email;
        private String phoneNumber;
        private String addressLine1;
        private String addressLine2;
        private String city;
        private String postalCode;

    }

}
