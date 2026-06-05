package com.tecnoweb.grupo24sa.services.pagofacil.dto;

import java.util.List;

public class QrRequest {
    private int paymentMethod;
    private String clientName;
    private int documentType;
    private String documentId;
    private String phoneNumber;
    private String email;
    private String paymentNumber;
    private double amount;
    private int currency;
    private String clientCode;
    private String callbackUrl;
    private List<OrderDetail> orderDetail;

    public static class OrderDetail {
        private int serial;
        private String product;
        private int quantity;
        private double price;
        private double discount;
        private double total;

        public OrderDetail() {}

        public OrderDetail(int serial, String product, int quantity, double price, double discount, double total) {
            this.serial = serial;
            this.product = product;
            this.quantity = quantity;
            this.price = price;
            this.discount = discount;
            this.total = total;
        }

        public int getSerial() { return serial; }
        public void setSerial(int serial) { this.serial = serial; }
        public String getProduct() { return product; }
        public void setProduct(String product) { this.product = product; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }
        public double getDiscount() { return discount; }
        public void setDiscount(double discount) { this.discount = discount; }
        public double getTotal() { return total; }
        public void setTotal(double total) { this.total = total; }
    }

    public int getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(int paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }
    public int getDocumentType() { return documentType; }
    public void setDocumentType(int documentType) { this.documentType = documentType; }
    public String getDocumentId() { return documentId; }
    public void setDocumentId(String documentId) { this.documentId = documentId; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPaymentNumber() { return paymentNumber; }
    public void setPaymentNumber(String paymentNumber) { this.paymentNumber = paymentNumber; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public int getCurrency() { return currency; }
    public void setCurrency(int currency) { this.currency = currency; }
    public String getClientCode() { return clientCode; }
    public void setClientCode(String clientCode) { this.clientCode = clientCode; }
    public String getCallbackUrl() { return callbackUrl; }
    public void setCallbackUrl(String callbackUrl) { this.callbackUrl = callbackUrl; }
    public List<OrderDetail> getOrderDetail() { return orderDetail; }
    public void setOrderDetail(List<OrderDetail> orderDetail) { this.orderDetail = orderDetail; }
}
