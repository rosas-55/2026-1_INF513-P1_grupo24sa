package com.tecnoweb.grupo24sa.utils;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * Utilidad para leer configuraciones desde:
 *   1. Variables de entorno del SO (System.getenv)
 *   2. Archivo .env (dotenv-java)
 *   3. Valor por defecto
 */
public class EnvConfig {
    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

    public static String get(String key, String defaultVal) {
        // 1. Variable de entorno del sistema operativo
        String val = System.getenv(key);
        if (val != null && !val.isBlank()) return val;
        // 2. Archivo .env
        val = dotenv.get(key);
        if (val != null && !val.isBlank()) return val;
        // 3. Valor por defecto
        return defaultVal;
    }
}
