package com.tecnoweb.grupo24sa.services.pagofacil.dto;

public class QrResponse {
    private int error;
    private int status;
    private String message;
    private Values values;

    public static class Values {
        private long transactionId;
        private String paymentMethodTransactionId;
        private int status;
        private String expirationDate;
        private String qrBase64;
        private String checkoutUrl;
        private String deepLink;
        private String qrContentUrl;
        private String universalUrl;

        public long getTransactionId() { return transactionId; }
        public void setTransactionId(long transactionId) { this.transactionId = transactionId; }
        public String getPaymentMethodTransactionId() { return paymentMethodTransactionId; }
        public void setPaymentMethodTransactionId(String paymentMethodTransactionId) { this.paymentMethodTransactionId = paymentMethodTransactionId; }
        public int getStatus() { return status; }
        public void setStatus(int status) { this.status = status; }
        public String getExpirationDate() { return expirationDate; }
        public void setExpirationDate(String expirationDate) { this.expirationDate = expirationDate; }
        public String getQrBase64() { return qrBase64; }
        public void setQrBase64(String qrBase64) { this.qrBase64 = qrBase64; }
        public String getCheckoutUrl() { return checkoutUrl; }
        public void setCheckoutUrl(String checkoutUrl) { this.checkoutUrl = checkoutUrl; }
        public String getDeepLink() { return deepLink; }
        public void setDeepLink(String deepLink) { this.deepLink = deepLink; }
        public String getQrContentUrl() { return qrContentUrl; }
        public void setQrContentUrl(String qrContentUrl) { this.qrContentUrl = qrContentUrl; }
        public String getUniversalUrl() { return universalUrl; }
        public void setUniversalUrl(String universalUrl) { this.universalUrl = universalUrl; }
    }

    public int getError() { return error; }
    public void setError(int error) { this.error = error; }
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Values getValues() { return values; }
    public void setValues(Values values) { this.values = values; }
}
