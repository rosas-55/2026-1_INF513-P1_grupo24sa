package com.tecnoweb.grupo24sa.command;

import com.tecnoweb.grupo24sa.data.DUsuario;
import com.tecnoweb.grupo24sa.utils.ContextoEmail;
import com.tecnoweb.grupo24sa.utils.ReporteResponse;

import java.util.HashMap;
import java.util.Map;

public class CommandInterpreter {

    private static final Map<String, String[]> COMMANDS = new HashMap<>();

    static {
        // CU1 - Gestión de Usuarios
        COMMANDS.put("usuario", new String[] { "registrar", "autenticar", "actualizar", "desactivar",
                "cambiarPassword", "listar", "buscar", "estadisticas" });

        // CU2 - Gestión de Ventas
        COMMANDS.put("venta",
                new String[] { "registrar", "actualizarEstado", "eliminar", "listar", "buscar", "listarPorCliente" });

        // CU3 - Gestión de Productos
        COMMANDS.put("producto", new String[] { "registrar", "actualizar", "eliminar", "listar", "buscar" });

        // CU5 - Gestión de Cuotas
        COMMANDS.put("cuota", new String[] { "pagar", "eliminar", "listarPorVenta", "buscar", "listarPorCliente" });

        // CU6 - Gestión de Insumos
        COMMANDS.put("insumo",
                new String[] { "registrar", "actualizar", "eliminar", "listar", "buscar", "listarStockBajo" });

        // CU7 - Gestión de Compras
        COMMANDS.put("compra",
                new String[] { "registrar", "actualizarEstado", "eliminar", "listar", "buscar", "listarPorProveedor" });

        // CU9 - Gestión de Proveedores
        COMMANDS.put("proveedor", new String[] { "registrar", "actualizar", "eliminar", "listar", "buscar" });

        // CU10 - Gestión de Recetas
        COMMANDS.put("receta",
                new String[] { "registrar", "actualizar", "eliminar", "listar", "buscar", "listarPorProducto" });

        // CU12 - Gestión de Producción
        COMMANDS.put("produccion",
                new String[] { "registrar", "actualizar", "eliminar", "listar", "buscar", "listarPorReceta" });

        // CU13 - Gestión de Inventario
        COMMANDS.put("inventario",
                new String[] { "registrar", "actualizar", "eliminar", "listar", "buscar", "listarPorInsumo" });

        // CU14 - Gestión de Roles
        COMMANDS.put("role", new String[] { "registrar", "actualizar", "eliminar", "listar", "buscar" });
        COMMANDS.put("rol",  new String[] { "registrar", "actualizar", "eliminar", "listar", "buscar" });

        // CU15 - Gestión de Role Users
        COMMANDS.put("roleusers", new String[] { "registrar", "eliminar", "listarPorUsuario", "listarPorRol" });

        // CU16 - Gestión de Módulos
        COMMANDS.put("modulo", new String[] { "registrar", "actualizar", "eliminar", "listar", "buscar" });

        // CU17 - Gestión de Acciones
        COMMANDS.put("accion", new String[] { "registrar", "actualizar", "eliminar", "listar", "buscar" });

        // CU18 - Gestión de Role Módulo
        COMMANDS.put("rolemodulo", new String[] { "registrar", "eliminar", "listarPorRol", "listarPorModulo" });

        // CU8 - Reportes
        COMMANDS.put("reporte", new String[] { "ventas", "cuotasPendientes", "stockBajo", "produccion", "compras", "ingresos" });
    }

    /** Compatibilidad hacia atrás — sin emailFrom */
    public static String interpret(String subject) {
        return interpretConGraficos(subject, "").getTextoRespuesta();
    }

    /** Compatibilidad hacia atrás — sin emailFrom */
    public static ReporteResponse interpretConGraficos(String subject) {
        return interpretConGraficos(subject, "");
    }

    /**
     * Interpreta el comando del asunto del correo, valida el remitente y ejecuta la acción.
     *
     * @param subject   Asunto del correo (el comando)
     * @param emailFrom Email del remitente (se usa para identificar al usuario)
     */
    public static ReporteResponse interpretConGraficos(String subject, String emailFrom) {
        subject = subject.replaceAll("[^\\p{L}\\p{N}\\s\\(\\),./@;_\\[\\]-]", "");
        subject = subject.replaceAll("\\s+", " ").trim();

        System.out.println("Subject luego de formatear: " + subject);
        System.out.println("Email remitente: " + emailFrom);

        // ── 1. Resolver contexto del remitente ──────────────────────────────────
        ContextoEmail ctx;
        try {
            ctx = resolverContexto(emailFrom);
        } catch (RuntimeException e) {
            System.err.println("[CommandInterpreter] Error de conexión a BD: " + e.getMessage());
            return new ReporteResponse(
                "⚠ Error del Sistema ⚠\n\n" +
                "No se pudo conectar a la base de datos.\n" +
                "El servicio no está disponible en este momento.\n\n" +
                "Por favor, intenta de nuevo más tarde.\n\n" +
                "Detalle técnico: " + e.getMessage()
            );
        }

        // ── 2. Comando HELP — diferenciado por rol ──────────────────────────────
        // Normalizar acentos para comparación case/accent-insensitive
        String normalizedSubject = java.text.Normalizer.normalize(subject, java.text.Normalizer.Form.NFD)
                .replaceAll("[\\p{M}]", "");
        if (normalizedSubject.equalsIgnoreCase("help")) {
            if (ctx == null)           return new ReporteResponse(getHelpNoRegistrado(emailFrom));
            if (ctx.esCliente())       return new ReporteResponse(getHelpCliente(ctx.getNombre()));
            return new ReporteResponse(getHelpCompleto());
        }

        // ── 3. Parsear la estructura del comando ────────────────────────────────
        //      [\\p{L}] acepta letras con acentos (á, é, ñ, ü, etc.)
        String pattern = "([\\p{L}]+)\\s+([\\p{L}]+)\\s*\\((.*)\\)";
        java.util.regex.Pattern regex = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher matcher = regex.matcher(subject);

        if (!matcher.matches()) {
            return new ReporteResponse(
                    "Comando no reconocido. Asegúrate de seguir la estructura: {entidad} {comando} (parametros)\n" +
                    "Envía 'help' para ver los comandos disponibles.");
        }

        // Normalizar acentos y lowercase para comparación case/accent-insensitive
        String entity       = java.text.Normalizer.normalize(matcher.group(1).trim(), java.text.Normalizer.Form.NFD)
                                .replaceAll("[\\p{M}]", "").toLowerCase();
        String commandInput = java.text.Normalizer.normalize(matcher.group(2).trim(), java.text.Normalizer.Form.NFD)
                                .replaceAll("[\\p{M}]", "").toLowerCase();
        String params       = matcher.group(3).trim();  // preserva acentos en parámetros (nombres, direcciones)

        if (!COMMANDS.containsKey(entity)) {
            return new ReporteResponse(
                    "Entidad '" + entity + "' no reconocida. Envía 'help' para ver entidades disponibles.");
        }

        boolean commandExists = false;
        String command = null;
        for (String validCommand : COMMANDS.get(entity)) {
            if (validCommand.equalsIgnoreCase(commandInput)) {
                commandExists = true;
                command = validCommand;
                break;
            }
        }

        if (!commandExists) {
            return new ReporteResponse("Comando '" + commandInput + "' no reconocido para '" + entity
                    + "'. Envía 'help' para ver comandos disponibles.");
        }

        // ── 4. Validar autenticación por correo ─────────────────────────────────
        // Comandos públicos que NO requieren estar registrado:
        //   - usuario registrar  (cualquiera puede registrarse)
        //   - producto listar    (ver el menú del restaurante)
        //   - producto buscar    (ver detalle de un producto)
        boolean esComandoPublico = ("usuario".equals(entity) && "registrar".equals(command))
                || ("producto".equals(entity) && ("listar".equals(command) || "buscar".equals(command)));

        if (!esComandoPublico && ctx == null) {
            String emailMostrado = (emailFrom != null && !emailFrom.trim().isEmpty())
                    ? emailFrom.trim() : "tu-correo@ejemplo.com";
            return new ReporteResponse(
                "❌ Tu correo '" + emailMostrado + "' no está registrado en el sistema.\n\n" +
                "Para registrarte como CLIENTE envía en el ASUNTO del correo:\n" +
                "  usuario registrar(nombre,cedula,celular,direccion," + emailMostrado + ",password,CLIENTE)\n\n" +
                "O envía 'help' para más información."
            );
        }

        // ── 5. Ejecutar el comando ──────────────────────────────────────────────
        switch (entity) {
            case "usuario":
                return new ReporteResponse(HandleUsuario.execute(command, params));
            case "role":
            case "rol":
                return new ReporteResponse(HandleRole.execute(command, params));
            case "producto":
                return new ReporteResponse(HandleProducto.execute(command, params));
            case "proveedor":
                return new ReporteResponse(HandleProveedor.execute(command, params));
            case "insumo":
                return new ReporteResponse(HandleInsumo.execute(command, params));
            case "compra":
                return new ReporteResponse(HandleCompra.execute(command, params));
            case "venta":
                // HandleVenta necesita el contexto para auto-asignar vendedor_id y fecha
                return HandleVenta.execute(command, params, ctx);
            case "cuota":
                // HandleCuota necesita el contexto para auto-detectar cliente en listarPorCliente
                return HandleCuota.execute(command, params, ctx);
            case "inventario":
                return new ReporteResponse(HandleInventario.execute(command, params));
            case "produccion":
                return new ReporteResponse(HandleProduccion.execute(command, params));
            case "receta":
                return new ReporteResponse(HandleReceta.execute(command, params));
            case "reporte":
                return new ReporteResponse(HandleReporte.execute(command, params));
            default:
                return new ReporteResponse(
                        "Entidad '" + entity + "' reconocida pero lógica de negocio no enlazada aún.");
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  Métodos privados
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Busca al usuario en la BD por su email y construye un ContextoEmail.
     * Retorna null si el email está vacío o no se encuentra en la BD.
     * Lanza RuntimeException si hay un error de conexión a la BD.
     */
    private static ContextoEmail resolverContexto(String emailFrom) {
        if (emailFrom == null || emailFrom.trim().isEmpty()) return null;
        DUsuario dUsuario = new DUsuario();
        String[] usuario = dUsuario.findByEmail(emailFrom.trim().toLowerCase());
        return ContextoEmail.desde(usuario); // retorna null si usuario == null
    }

    // ── Mensajes de ayuda ────────────────────────────────────────────────────

    /**
     * Help para remitentes NO registrados en el sistema.
     */
    private static String getHelpNoRegistrado(String emailFrom) {
        String emailEjemplo = (emailFrom != null && !emailFrom.trim().isEmpty())
                ? emailFrom.trim() : "tucorreo@gmail.com";
        return
            "╔═══════════════════════════════════════════════════════════════╗\r\n" +
            "║      Bienvenido al Sistema de Correos - Las Brazas           ║\r\n" +
            "╚═══════════════════════════════════════════════════════════════╝\r\n" +
            "\r\n" +
            "¡Hola! Tu correo no está registrado en nuestro sistema.\r\n" +
            "\r\n" +
            "Para registrarte como CLIENTE y poder realizar pedidos,\r\n" +
            "envía el siguiente comando en el ASUNTO de un correo nuevo:\r\n" +
            "\r\n" +
            "  usuario registrar(nombre,cedula,celular,direccion,email,password,CLIENTE)\r\n" +
            "\r\n" +
            "Ejemplo:\r\n" +
            "  usuario registrar(Juan Perez,12345678,70000001,Calle 1 #123," + emailEjemplo + ",mipass123,CLIENTE)\r\n" +
            "\r\n" +
            "Una vez registrado, envía 'help' para ver todos tus comandos.\r\n" +
            "\r\n" +
            "Puedes ver nuestro menú de productos sin registrarte:\r\n" +
            "  producto listar()";
    }

    /**
     * Help reducido para usuarios con rol CLIENTE.
     */
    private static String getHelpCliente(String nombre) {
        return
            "╔═══════════════════════════════════════════════════════════════╗\r\n" +
            "║         Menú de Comandos - Las Brazas (Cliente)              ║\r\n" +
            "╚═══════════════════════════════════════════════════════════════╝\r\n" +
            "Hola, " + nombre + "! Aquí están tus comandos disponibles.\r\n" +
            "\r\n" +
            "Formato: escribe el comando en el ASUNTO del correo.\r\n" +
            "\r\n" +
            "=== PRODUCTOS (ver el menú del restaurante) ===\r\n" +
            "producto listar()\r\n" +
            "producto buscar(id)\r\n" +
            "\r\n" +
            "=== VENTAS (realizar un pedido) ===\r\n" +
            "venta registrar(nro_cuotas,tipo,[producto_id;cantidad],...)\r\n" +
            "  tipo: CONTADO (pago unico) o CREDITO (>= 2 cuotas mensuales)\r\n" +
            "  Nota: cliente_id, estado, interes_mora, fecha y vendedor se asignan automaticamente.\r\n" +
            "  Ejemplo contado:  venta registrar(1,CONTADO,[1;2])\r\n" +
            "  Ejemplo credito:  venta registrar(3,CREDITO,[1;1],[2;2])\r\n" +
            "venta listarPorCliente(tu_id)\r\n" +
            "venta buscar(id)\r\n" +
            "\r\n" +
            "=== CUOTAS (gestionar tus pagos pendientes) ===\r\n" +
            "cuota listarPorCliente()     <- ver tus cuotas y estado de cada una\r\n" +
            "cuota pagar(id_cuota)        <- genera un QR para pagar ESA cuota especifica\r\n" +
            "cuota listarPorVenta(venta_id)\r\n" +
            "cuota buscar(id)\r\n" +
            "\r\n" +
            "=== CUENTA ===\r\n" +
            "usuario cambiarPassword(id,password_actual,password_nueva)\r\n" +
            "usuario buscar(id)\r\n";
    }

    /**
     * Help completo para PROPIETARIO y VENDEDOR.
     */
    private static String getHelpCompleto() {
        return
            "**************** SISTEMA DE INVENTARIO Y VENTAS - LAS BRAZAS ****************\r\n" +
            "\r\n" +
            "Formato general: {entidad} {comando} (parametros)\r\n" +
            "- Usar el asunto del correo para enviar el comando.\r\n" +
            "- Parametros separados por coma. Escribe 'null' para campos opcionales.\r\n" +
            "- Fechas: YYYY-MM-DD. Numeros decimales: 9.00\r\n" +
            "\r\n" +
            "=== USUARIO ===\r\n" +
            "registrar(nombre,cedula,celular,direccion,email,password,rol)\r\n" +
            "autenticar(email,password)\r\n" +
            "actualizar(id,nombre,cedula,celular,direccion,email,password,rol)\r\n" +
            "desactivar(id) | cambiarPassword(id,actual,nueva)\r\n" +
            "listar() | buscar(id) | estadisticas()\r\n" +
            "\r\n" +
            "=== ROLE ===\r\n" +
            "registrar(descripcion,nombre)\r\n" +
            "actualizar(id,descripcion,nombre)\r\n" +
            "eliminar(id) | listar() | buscar(id)\r\n" +
            "\r\n" +
            "=== PRODUCTO ===\r\n" +
            "registrar(estado,nombre,precio_venta,insumo_id)\r\n" +
            "actualizar(id,estado,nombre,precio_venta,insumo_id)\r\n" +
            "  Usar 0 en insumo_id si es un producto preparado (con receta).\r\n" +
            "eliminar(id) | listar() | buscar(id)\r\n" +
            "\r\n" +
            "=== PROVEEDOR ===\r\n" +
            "registrar(direccion,nombre,telefono)\r\n" +
            "actualizar(id,direccion,nombre,telefono)\r\n" +
            "eliminar(id) | listar() | buscar(id)\r\n" +
            "\r\n" +
            "=== INSUMO ===\r\n" +
            "registrar(costo_unitario,descripcion,estado,nombre,stock_actual,stock_minimo,unidad_medida)\r\n" +
            "actualizar(id,costo_unitario,descripcion,estado,nombre,stock_actual,stock_minimo,unidad_medida)\r\n" +
            "eliminar(id) | listar() | buscar(id) | listarStockBajo()\r\n" +
            "\r\n" +
            "=== COMPRA ===\r\n" +
            "registrar(estado,proveedor_id,[insumo_id1;cantidad1;precio_unitario1],...)\r\n" +
            "  Nota: la fecha se asigna automaticamente.\r\n" +
            "  Ejemplo: compra registrar(PAGADO,1,[1;10;15.00],[2;5;43.00])\r\n" +
            "actualizarEstado(id,estado) | eliminar(id)\r\n" +
            "listar() | buscar(id) | listarPorProveedor(proveedor_id)\r\n" +
            "\r\n" +
            "=== VENTA ===\r\n" +
            "registrar(nro_cuotas,tipo,[producto_id1;cantidad1],...)\r\n" +
            "  tipo: CONTADO (1 cuota) o CREDITO (>= 2 cuotas)\r\n" +
            "  Nota: cliente_id, estado, interes_mora, fecha y vendedor se asignan automaticamente desde tu correo.\r\n" +
            "  Ejemplo: venta registrar(1,CONTADO,[1;2],[2;1])\r\n" +
            "actualizarEstado(id,estado) | eliminar(id)\r\n" +
            "listar() | buscar(id) | listarPorCliente(cliente_id)\r\n" +
            "\r\n" +
            "=== CUOTA ===\r\n" +
            "pagar(id,fecha_pago,monto_pagado)   <- pago manual (propietario/vendedor)\r\n" +
            "pagar(id_cuota)                     <- genera QR para ESA cuota\r\n" +
            "eliminar(id) | listarPorVenta(venta_id) | buscar(id)\r\n" +
            "listarPorCliente()      <- tus cuotas (cliente)\r\n" +
            "listarPorCliente(cliente_id)   <- cuotas de otro cliente (staff)\r\n" +
            "\r\n" +
            "=== INVENTARIO ===\r\n" +
            "registrar(cantidad,insumo_id,costo_unitario,observacion,tipo_movimiento)\r\n" +
            "  tipo_movimiento: INGRESO o SALIDA\r\n" +
            "  Nota: la fecha se asigna automaticamente.\r\n" +
            "actualizar(id,cantidad,observacion)\r\n" +
            "eliminar(id) | listar() | buscar(id) | listarPorInsumo(insumo_id)\r\n" +
            "\r\n" +
            "=== RECETA ===\r\n" +
            "registrar(descripcion,producto_id,tiempo_preparacion,[insumo_id1;cantidad1],...)\r\n" +
            "  Ejemplo: receta registrar(Hamburguesa Clasica,1,15,[1;1.0],[2;1.0])\r\n" +
            "actualizar(id,descripcion,producto_id,tiempo_preparacion)\r\n" +
            "eliminar(id) | listar() | buscar(id) | listarPorProducto(producto_id)\r\n" +
            "\r\n" +
            "=== PRODUCCION ===\r\n" +
            "registrar(cantidad_producida,receta_id)  <- descuenta stock de insumos\r\n" +
            "  Nota: la fecha se asigna automaticamente.\r\n" +
            "actualizar(id,cantidad_producida,fecha,receta_id)\r\n" +
            "eliminar(id) | listar() | buscar(id) | listarPorReceta(receta_id)\r\n" +
            "\r\n" +
            "=== REPORTE ===\r\n" +
            "reporte ventas()\r\n" +
            "reporte cuotasPendientes()\r\n" +
            "reporte stockBajo()\r\n" +
            "reporte produccion()\r\n" +
            "reporte compras()\r\n" +
            "reporte ingresos()\r\n" +
            "***************************************************************";
    }
}
