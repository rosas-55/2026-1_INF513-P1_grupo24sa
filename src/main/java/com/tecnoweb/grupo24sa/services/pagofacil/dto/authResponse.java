package com.tecnoweb.grupo24sa.services.pagofacil.dto;

public class authResponse {
    private int error;
    private int status;
    private String message;
    private String displayMessage;
    private Values values;

    public static class Values {
        private String accessToken;
        private String tokenType;
        private double expiresInMinutes;

        public String getAccessToken() { return accessToken; }
        public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
        public String getTokenType() { return tokenType; }
        public void setTokenType(String tokenType) { this.tokenType = tokenType; }
        public double getExpiresInMinutes() { return expiresInMinutes; }
        public void setExpiresInMinutes(double expiresInMinutes) { this.expiresInMinutes = expiresInMinutes; }
    }

    public int getError() { return error; }
    public void setError(int error) { this.error = error; }
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getDisplayMessage() { return displayMessage; }
    public void setDisplayMessage(String displayMessage) { this.displayMessage = displayMessage; }
    public Values getValues() { return values; }
    public void setValues(Values values) { this.values = values; }
}
