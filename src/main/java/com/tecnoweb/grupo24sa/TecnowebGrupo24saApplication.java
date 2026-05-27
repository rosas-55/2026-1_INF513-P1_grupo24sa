package com.tecnoweb.grupo24sa;

import com.tecnoweb.grupo24sa.connection.ConnectionCore;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TecnowebGrupo24saApplication {

    public static void main(String[] args) {
        SpringApplication.run(TecnowebGrupo24saApplication.class, args);
        
        //prueba
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║       Sistema de Inventario y Ventas para  Las Brazas         ║");
        System.out.println("║           Iniciando servicio de correo...                     ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        
        ConnectionCore.main(args);
    }

}
