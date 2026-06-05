package com.tecnoweb.grupo24sa.services.pagofacil;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tecnoweb.grupo24sa.services.pagofacil.dto.AuthRequest;
import com.tecnoweb.grupo24sa.services.pagofacil.dto.authResponse;
import com.tecnoweb.grupo24sa.services.pagofacil.dto.QrRequest;
import com.tecnoweb.grupo24sa.services.pagofacil.dto.QrResponse;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Properties;

public class pagoFacilService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl = "https://masterqr.pagofacil.com.bo/api/services/v2";
    
    private String accessToken;
    private String tcTokenService;
    private String tcTokenSecret;

    public pagoFacilService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
        cargarCredenciales();
    }

    /**
     * Carga las credenciales desde variables de entorno (Producción) 
     * o desde application.properties (Desarrollo local).
     */
    private void cargarCredenciales() {
        // 1. Intentar leer desde Variables de Entorno (Prioridad alta - Seguro para producción)
        this.tcTokenService = System.getenv("PAGOFACIL_TOKEN_SERVICE");
        this.tcTokenSecret = System.getenv("PAGOFACIL_TOKEN_SECRET");

        // 2. Si no están en el entorno, leer desde application.properties
        if (this.tcTokenService == null || this.tcTokenSecret == null) {
            try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
                Properties prop = new Properties();
                if (input != null) {
                    prop.load(input);
                    this.tcTokenService = prop.getProperty("pagofacil.token.service");
                    this.tcTokenSecret = prop.getProperty("pagofacil.token.secret");
                }
            } catch (Exception ex) {
                System.err.println("Advertencia: No se pudo leer application.properties");
            }
        }
    }

    /**
     * Autentica con el servicio y obtiene el token de acceso.
     */
    public boolean autenticar() {
        if (this.tcTokenService == null || this.tcTokenSecret == null) {
            System.err.println("Error: Credenciales de PagoFacil no configuradas.");
            return false;
        }

        try {
            AuthRequest authReq = new AuthRequest(this.tcTokenService, this.tcTokenSecret);
            String jsonPayload = objectMapper.writeValueAsString(authReq);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/login"))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                authResponse authRes = objectMapper.readValue(response.body(), authResponse.class);
                if (authRes.getError() == 0 && authRes.getValues() != null) {
                    this.accessToken = authRes.getValues().getAccessToken();
                    return true;
                }
            } else {
                System.err.println("Error en autenticación PagoFacil: HTTP " + response.statusCode());
                System.err.println("Body: " + response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Genera el código QR para el pago.
     * Requiere que el servicio ya esté autenticado.
     */
    public QrResponse generarQR(QrRequest qrRequest) {
        if (this.accessToken == null || this.accessToken.isEmpty()) {
            throw new IllegalStateException("El servicio no ha sido autenticado. Llame a autenticar() primero.");
        }

        try {
            String jsonPayload = objectMapper.writeValueAsString(qrRequest);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/generate-qr"))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .header("Authorization", "Bearer " + this.accessToken)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), QrResponse.class);
            } else {
                System.err.println("Error al generar QR PagoFacil: HTTP " + response.statusCode());
                System.err.println("Body: " + response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
