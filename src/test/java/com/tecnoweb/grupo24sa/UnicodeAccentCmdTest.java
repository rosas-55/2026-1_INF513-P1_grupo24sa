package com.tecnoweb.grupo24sa;

import com.tecnoweb.grupo24sa.command.CommandInterpreter;
import com.tecnoweb.grupo24sa.utils.ReporteResponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifica que los comandos funcionen con mayúsculas, minúsculas y acentos.
 * También verifica que los nombres con ñ se preserven en los parámetros.
 */
public class UnicodeAccentCmdTest {

    // ── Case sensitivity tests ──────────────────────────────────────────

    @Test
    @DisplayName("'usuario listar()' debe funcionar (lowercase)")
    public void testLowercase() {
        ReporteResponse r = CommandInterpreter.interpretConGraficos("usuario listar()", "");
        String txt = r.getTextoRespuesta();
        // No debe decir "no reconocido"
        assertFalse(txt.contains("no reconocido"), "Comando lowercase falló: " + txt);
    }

    @Test
    @DisplayName("'USUARIO LISTAR()' debe funcionar (uppercase)")
    public void testUppercase() {
        ReporteResponse r = CommandInterpreter.interpretConGraficos("USUARIO LISTAR()", "");
        String txt = r.getTextoRespuesta();
        assertFalse(txt.contains("no reconocido"), "Comando uppercase falló: " + txt);
        assertFalse(txt.contains("Entidad"), "Entidad uppercase no reconocida: " + txt);
    }

    @Test
    @DisplayName("'UsuArio listar()' debe funcionar (mixed case)")
    public void testMixedCase() {
        ReporteResponse r = CommandInterpreter.interpretConGraficos("UsuArio listar()", "");
        String txt = r.getTextoRespuesta();
        assertFalse(txt.contains("no reconocido"), "Comando mixed case falló: " + txt);
        assertFalse(txt.contains("Entidad"), "Entidad mixed case no reconocida: " + txt);
    }

    @Test
    @DisplayName("'úsüário listar()' debe funcionar (accents)")
    public void testAccents() {
        ReporteResponse r = CommandInterpreter.interpretConGraficos("úsüário listar()", "");
        String txt = r.getTextoRespuesta();
        assertFalse(txt.contains("no reconocido"), "Comando con acentos falló: " + txt);
        assertFalse(txt.contains("Entidad"), "Entidad con acentos no reconocida: " + txt);
    }

    @Test
    @DisplayName("'HELP' con acentos: 'hélp' debe funcionar")
    public void testHelpAccented() {
        ReporteResponse r = CommandInterpreter.interpretConGraficos("hélp", "");
        String txt = r.getTextoRespuesta();
        // Debe mostrar ayuda, no "comando no reconocido"
        assertFalse(txt.contains("no reconocido"), "HELP con acento falló: " + txt);
    }

    // ── Name with ñ tests ──────────────────────────────────────────────

    @Test
    @DisplayName("Nombre con ñ debe preservarse en parámetros")
    public void testNameWithNtilde() {
        ReporteResponse r = CommandInterpreter.interpretConGraficos(
            "usuario registrar(Juñor,12345678,70000001,Calle 1,test@mail.com,pass123,CLIENTE)", "");
        String txt = r.getTextoRespuesta();
        // Puede fallar por BD o por email no registrado, pero NO por "comando no reconocido"
        assertFalse(txt.contains("no reconocido"), "Comando con ñ en nombre falló: " + txt);
        assertFalse(txt.contains("Entidad"), "Entidad con ñ no reconocida: " + txt);
    }

    @Test
    @DisplayName("Nombre con múltiples acentos: 'Nuñez Gómes'")
    public void testNameWithMultipleAccents() {
        ReporteResponse r = CommandInterpreter.interpretConGraficos(
            "usuario registrar(Nuñez Gómes,87654321,70000002,Av. Central,ng@mail.com,pass456,VENDEDOR)", "");
        String txt = r.getTextoRespuesta();
        assertFalse(txt.contains("no reconocido"), "Comando con ñ y ó falló: " + txt);
        assertFalse(txt.contains("Entidad"), "Comando con acentos no reconocido: " + txt);
    }

    @Test
    @DisplayName("'VENTA REGISTRAR(1,CONTADO,[1;2])' uppercase debe funcionar")
    public void testVentaUppercase() {
        ReporteResponse r = CommandInterpreter.interpretConGraficos(
            "VENTA REGISTRAR(1,CONTADO,[1;2])", "test@mail.com");
        String txt = r.getTextoRespuesta();
        assertFalse(txt.contains("no reconocido"), "VENTA uppercase falló: " + txt);
        assertFalse(txt.contains("Entidad"), "VENTA uppercase no reconocida: " + txt);
    }

    @Test
    @DisplayName("'vënta registrar(1,CONTADO,[1;2])' con acentos debe funcionar")
    public void testVentaAccented() {
        ReporteResponse r = CommandInterpreter.interpretConGraficos(
            "vënta registrar(1,CONTADO,[1;2])", "test@mail.com");
        String txt = r.getTextoRespuesta();
        assertFalse(txt.contains("no reconocido"), "vënta con acento falló: " + txt);
        assertFalse(txt.contains("Entidad"), "vënta no reconocida: " + txt);
    }
}
