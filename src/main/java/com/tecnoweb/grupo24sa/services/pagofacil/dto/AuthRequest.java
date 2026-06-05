package com.tecnoweb.grupo24sa.services.pagofacil.dto;

public class AuthRequest {
    private String tcTokenService;
    private String tcTokenSecret;

    public AuthRequest() {}

    public AuthRequest(String tcTokenService, String tcTokenSecret) {
        this.tcTokenService = tcTokenService;
        this.tcTokenSecret = tcTokenSecret;
    }

    public String getTcTokenService() {
        return tcTokenService;
    }

    public void setTcTokenService(String tcTokenService) {
        this.tcTokenService = tcTokenService;
    }

    public String getTcTokenSecret() {
        return tcTokenSecret;
    }

    public void setTcTokenSecret(String tcTokenSecret) {
        this.tcTokenSecret = tcTokenSecret;
    }
}
