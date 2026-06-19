package com.tecnoweb.grupo24sa.services.pagofacil.dto;

public class QueryTransactionResponse {
    private int error;
    private int status;
    private String message;
    private Values values;

    public static class Values {
        private long pagofacilTransactionId;
        private String companyTransactionId;
        private String clientCompanyCode;
        private String amount;
        private int currencyId;
        private String currencyCode;
        private int paymentStatus;
        private String paymentStatusDescription;
        private int paymentMethodId;
        private String paymentMethodDetail;
        private String requestDate;
        private String requestTime;
        private String paymentDate;
        private String paymentTime;
        private String payerName;
        private String payerDocument;
        private String payerAccount;
        private String payerBank;
        private String payerBankAcronym;

        public long getPagofacilTransactionId() { return pagofacilTransactionId; }
        public void setPagofacilTransactionId(long pagofacilTransactionId) { this.pagofacilTransactionId = pagofacilTransactionId; }
        public String getCompanyTransactionId() { return companyTransactionId; }
        public void setCompanyTransactionId(String companyTransactionId) { this.companyTransactionId = companyTransactionId; }
        public String getClientCompanyCode() { return clientCompanyCode; }
        public void setClientCompanyCode(String clientCompanyCode) { this.clientCompanyCode = clientCompanyCode; }
        public String getAmount() { return amount; }
        public void setAmount(String amount) { this.amount = amount; }
        public int getCurrencyId() { return currencyId; }
        public void setCurrencyId(int currencyId) { this.currencyId = currencyId; }
        public String getCurrencyCode() { return currencyCode; }
        public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }
        public int getPaymentStatus() { return paymentStatus; }
        public void setPaymentStatus(int paymentStatus) { this.paymentStatus = paymentStatus; }
        public String getPaymentStatusDescription() { return paymentStatusDescription; }
        public void setPaymentStatusDescription(String paymentStatusDescription) { this.paymentStatusDescription = paymentStatusDescription; }
        public int getPaymentMethodId() { return paymentMethodId; }
        public void setPaymentMethodId(int paymentMethodId) { this.paymentMethodId = paymentMethodId; }
        public String getPaymentMethodDetail() { return paymentMethodDetail; }
        public void setPaymentMethodDetail(String paymentMethodDetail) { this.paymentMethodDetail = paymentMethodDetail; }
        public String getRequestDate() { return requestDate; }
        public void setRequestDate(String requestDate) { this.requestDate = requestDate; }
        public String getRequestTime() { return requestTime; }
        public void setRequestTime(String requestTime) { this.requestTime = requestTime; }
        public String getPaymentDate() { return paymentDate; }
        public void setPaymentDate(String paymentDate) { this.paymentDate = paymentDate; }
        public String getPaymentTime() { return paymentTime; }
        public void setPaymentTime(String paymentTime) { this.paymentTime = paymentTime; }
        public String getPayerName() { return payerName; }
        public void setPayerName(String payerName) { this.payerName = payerName; }
        public String getPayerDocument() { return payerDocument; }
        public void setPayerDocument(String payerDocument) { this.payerDocument = payerDocument; }
        public String getPayerAccount() { return payerAccount; }
        public void setPayerAccount(String payerAccount) { this.payerAccount = payerAccount; }
        public String getPayerBank() { return payerBank; }
        public void setPayerBank(String payerBank) { this.payerBank = payerBank; }
        public String getPayerBankAcronym() { return payerBankAcronym; }
        public void setPayerBankAcronym(String payerBankAcronym) { this.payerBankAcronym = payerBankAcronym; }
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
