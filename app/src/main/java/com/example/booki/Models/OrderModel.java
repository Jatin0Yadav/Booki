package com.example.booki.Models;
public class OrderModel {

    String orderId;
    String paymentId;
    int totalAmount;
    String bookName;

    public OrderModel() {}

    public OrderModel(String orderId, String paymentId, int totalAmount, String bookName) {
        this.orderId = orderId;
        this.paymentId = paymentId;
        this.totalAmount = totalAmount;
        this.bookName = bookName;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public String getBookName() {
        return bookName;
    }
}