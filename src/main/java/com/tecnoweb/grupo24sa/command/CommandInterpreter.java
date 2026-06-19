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
                new String[] { "registrar", "actualizarEstado", "eliminar", "listar", "buscar", "listarPorCliente", "verificarPago" });

        // CU3 - Gestión de Productos
        COMMANDS.put("producto", new String[] { "registrar", "actualizar", "eliminar", "listar", "buscar" });

        // CU5 - Gestión de Cuotas
        COMMANDS.put("cuota", new String[] { "pagar", "eliminar", "listarPorVenta", "buscar", "listarPorCliente", "verificarPago" });

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
            "****** SISTEMA DE INVENTARIO Y VENTAS - LAS BRAZAS ******\r\n" +
            "\r\n" +
            "Tu correo no esta registrado en el sistema.\r\n" +
            "\r\n" +
            "Formato: {entidad} {comando}(parametros)\r\n" +
            "- Usar el ASUNTO del correo para enviar el comando.\r\n" +
            "- Parametros separados por coma.\r\n" +
            "\r\n" +
            "=== SIN ROL ===\r\n" +
            "\r\n" +
            "usuario registrar(nombre,cedula,celular,direccion,email,password,rol)\r\n" +
            "  Registra un usuario nuevo.\r\n" +
            "  rol: CLIENTE, VENDEDOR, PROPIETARIO\r\n" +
            "  Ejemplo: usuario registrar(Juan Perez,12345678,70000001,Calle 1 #123," + emailEjemplo + ",mipass123,CLIENTE)\r\n" +
            "\r\n" +
            "producto listar()\r\n" +
            "  Muestra productos.\r\n" +
            "\r\n" +
            "producto buscar(id)\r\n" +
            "  Muestra un producto.\r\n" +
            "\r\n" +
            "Una vez registrado envia help para ver tus comandos.\r\n" +
            "*************************************************************";
    }

    /**
     * Help reducido para usuarios con rol CLIENTE.
     */
    private static String getHelpCliente(String nombre) {
        return
            "****** SISTEMA DE INVENTARIO Y VENTAS - LAS BRAZAS ******\r\n" +
            "Hola, " + nombre + "!\r\n" +
            "\r\n" +
            "Formato: {entidad} {comando}(parametros)\r\n" +
            "- Usar el ASUNTO del correo para enviar el comando.\r\n" +
            "- Parametros separados por coma.\r\n" +
            "\r\n" +
            "=== PRODUCTOS ===\r\n" +
            "\r\n" +
            "producto listar()\r\n" +
            "  Muestra productos.\r\n" +
            "\r\n" +
            "producto buscar(id)\r\n" +
            "  Muestra un producto.\r\n" +
            "\r\n" +
            "=== VENTA ===\r\n" +
            "\r\n" +
            "venta registrar(nro_cuotas,tipo,[producto_id;cantidad],...)\r\n" +
            "  Registra una venta con QR de pago.\r\n" +
            "  tipo: CONTADO, CREDITO\r\n" +
            "  CONTADO: nro_cuotas = 1, genera QR automatico.\r\n" +
            "  CREDITO: nro_cuotas = 2 o mas, genera cuotas.\r\n" +
            "  Auto: cliente_id, estado=PENDIENTE, fecha, vendedor.\r\n" +
            "\r\n" +
            "venta buscar(id)\r\n" +
            "  Muestra una venta.\r\n" +
            "\r\n" +
            "venta listarPorCliente(cliente_id)\r\n" +
            "  Muestra ventas de un cliente.\r\n" +
            "\r\n" +
            "venta verificarPago(id_venta)\r\n" +
            "  Consulta PagoFacil y actualiza estado si fue pagado.\r\n" +
            "  Requiere: venta con pagofacilTransactionId guardado.\r\n" +
            "\r\n" +
            "=== CUOTA ===\r\n" +
            "\r\n" +
            "cuota pagar(id_cuota)\r\n" +
            "  Genera QR de pago para una cuota.\r\n" +
            "  Estados permitidos: PENDIENTE, EN_MORA.\r\n" +
            "\r\n" +
            "cuota buscar(id)\r\n" +
            "  Muestra una cuota.\r\n" +
            "\r\n" +
            "cuota listarPorCliente()\r\n" +
            "  Muestra tus cuotas.\r\n" +
            "\r\n" +
            "cuota listarPorVenta(venta_id)\r\n" +
            "  Muestra cuotas de una venta.\r\n" +
            "\r\n" +
            "cuota verificarPago(id_cuota)\r\n" +
            "  Consulta PagoFacil y actualiza estado si fue pagado.\r\n" +
            "  Requiere: cuota con pagofacilTransactionId guardado.\r\n" +
            "\r\n" +
            "=== CUENTA ===\r\n" +
            "\r\n" +
            "usuario buscar(id)\r\n" +
            "  Muestra tus datos.\r\n" +
            "\r\n" +
            "usuario cambiarPassword(id,actual,nueva)\r\n" +
            "  Cambia la password.\r\n" +
            "\r\n" +
            "=== FLUJO DE COMPRA ===\r\n" +
            "\r\n" +
            "1. Ver productos:           producto listar()\r\n" +
            "2. Registrar venta:         venta registrar(1,CONTADO,[1;2])\r\n" +
            "   -> Recibes QR adjunto para pagar.\r\n" +
            "3. Escanear QR y pagar con tu app bancaria.\r\n" +
            "4. Verificar pago:          venta verificarPago(id_venta)\r\n" +
            "\r\n" +
            "Para credito:\r\n" +
            "1. Registrar venta:         venta registrar(3,CREDITO,[1;1],[2;2])\r\n" +
            "2. Ver cuotas:              cuota listarPorCliente()\r\n" +
            "3. Generar QR de cuota:     cuota pagar(id_cuota)\r\n" +
            "4. Escanear QR y pagar.\r\n" +
            "5. Verificar pago:          cuota verificarPago(id_cuota)\r\n" +
            "\r\n" +
            "*************************************************************";
    }

    /**
     * Help completo para PROPIETARIO y VENDEDOR.
     */
    private static String getHelpCompleto() {
        return
            "****** SISTEMA DE INVENTARIO Y VENTAS - LAS BRAZAS ******\r\n" +
            "\r\n" +
            "Formato general: {entidad} {comando}(parametros)\r\n" +
            "- Usar el asunto del correo para enviar el comando.\r\n" +
            "- Parametros separados por coma.\r\n" +
            "- Fechas: YYYY-MM-DD.\r\n" +
            "- Numeros decimales: 9.00.\r\n" +
            "\r\n" +
            "=== SIN ROL ===\r\n" +
            "Rol: usuario no registrado\r\n" +
            "\r\n" +
            "usuario registrar(nombre,cedula,celular,direccion,email,password,rol)\r\n" +
            "  Registra un usuario nuevo.\r\n" +
            "  rol: CLIENTE, VENDEDOR, PROPIETARIO\r\n" +
            "\r\n" +
            "producto listar()\r\n" +
            "  Muestra productos.\r\n" +
            "\r\n" +
            "producto buscar(id)\r\n" +
            "  Muestra un producto.\r\n" +
            "\r\n" +
            "=== USUARIO ===\r\n" +
            "Rol: vendedor / propietario\r\n" +
            "Nota: cliente puede usar buscar(id) y cambiarPassword(id,actual,nueva).\r\n" +
            "\r\n" +
            "registrar(nombre,cedula,celular,direccion,email,password,rol)\r\n" +
            "  Registra un usuario nuevo.\r\n" +
            "  rol: CLIENTE, VENDEDOR, PROPIETARIO\r\n" +
            "\r\n" +
            "autenticar(email,password)\r\n" +
            "  Verifica email y password.\r\n" +
            "\r\n" +
            "actualizar(id,nombre,cedula,celular,direccion,email,password,rol)\r\n" +
            "  Actualiza un usuario.\r\n" +
            "  rol: CLIENTE, VENDEDOR, PROPIETARIO\r\n" +
            "\r\n" +
            "desactivar(id)\r\n" +
            "  Desactiva un usuario.\r\n" +
            "\r\n" +
            "cambiarPassword(id,actual,nueva)\r\n" +
            "  Cambia la password.\r\n" +
            "\r\n" +
            "listar()\r\n" +
            "  Muestra usuarios.\r\n" +
            "\r\n" +
            "buscar(id)\r\n" +
            "  Muestra un usuario.\r\n" +
            "\r\n" +
            "estadisticas()\r\n" +
            "  Muestra estadisticas de usuarios.\r\n" +
            "\r\n" +
            "=== ROLE ===\r\n" +
            "Rol: vendedor / propietario\r\n" +
            "\r\n" +
            "registrar(descripcion,nombre)\r\n" +
            "  Registra un rol.\r\n" +
            "\r\n" +
            "actualizar(id,descripcion,nombre)\r\n" +
            "  Actualiza un rol.\r\n" +
            "\r\n" +
            "eliminar(id)\r\n" +
            "  Elimina un rol.\r\n" +
            "\r\n" +
            "listar()\r\n" +
            "  Muestra roles.\r\n" +
            "\r\n" +
            "buscar(id)\r\n" +
            "  Muestra un rol.\r\n" +
            "\r\n" +
            "=== PRODUCTO ===\r\n" +
            "Rol: vendedor / propietario\r\n" +
            "Nota: sin rol y cliente pueden usar listar() y buscar(id).\r\n" +
            "\r\n" +
            "registrar(estado,nombre,precio_venta,insumo_id)\r\n" +
            "  Registra un producto.\r\n" +
            "  estado: usar ACTIVO o INACTIVO.\r\n" +
            "  insumo_id: usar 0 si es producto con receta.\r\n" +
            "\r\n" +
            "actualizar(id,estado,nombre,precio_venta,insumo_id)\r\n" +
            "  Actualiza un producto.\r\n" +
            "  estado: usar ACTIVO o INACTIVO.\r\n" +
            "  insumo_id: usar 0 si es producto con receta.\r\n" +
            "\r\n" +
            "eliminar(id)\r\n" +
            "  Elimina un producto.\r\n" +
            "\r\n" +
            "listar()\r\n" +
            "  Muestra productos.\r\n" +
            "\r\n" +
            "buscar(id)\r\n" +
            "  Muestra un producto.\r\n" +
            "\r\n" +
            "=== PROVEEDOR ===\r\n" +
            "Rol: vendedor / propietario\r\n" +
            "\r\n" +
            "registrar(direccion,nombre,telefono)\r\n" +
            "  Registra un proveedor.\r\n" +
            "\r\n" +
            "actualizar(id,direccion,nombre,telefono)\r\n" +
            "  Actualiza un proveedor.\r\n" +
            "\r\n" +
            "eliminar(id)\r\n" +
            "  Elimina un proveedor.\r\n" +
            "\r\n" +
            "listar()\r\n" +
            "  Muestra proveedores.\r\n" +
            "\r\n" +
            "buscar(id)\r\n" +
            "  Muestra un proveedor.\r\n" +
            "\r\n" +
            "=== INSUMO ===\r\n" +
            "Rol: vendedor / propietario\r\n" +
            "\r\n" +
            "registrar(costo_unitario,descripcion,estado,nombre,stock_actual,stock_minimo,unidad_medida)\r\n" +
            "  Registra un insumo.\r\n" +
            "  estado: usar ACTIVO o INACTIVO.\r\n" +
            "\r\n" +
            "actualizar(id,costo_unitario,descripcion,estado,nombre,stock_actual,stock_minimo,unidad_medida)\r\n" +
            "  Actualiza un insumo.\r\n" +
            "  estado: usar ACTIVO o INACTIVO.\r\n" +
            "\r\n" +
            "eliminar(id)\r\n" +
            "  Elimina un insumo.\r\n" +
            "\r\n" +
            "listar()\r\n" +
            "  Muestra insumos.\r\n" +
            "\r\n" +
            "buscar(id)\r\n" +
            "  Muestra un insumo.\r\n" +
            "\r\n" +
            "listarStockBajo()\r\n" +
            "  Muestra insumos con stock bajo.\r\n" +
            "\r\n" +
            "=== COMPRA ===\r\n" +
            "Rol: vendedor / propietario\r\n" +
            "\r\n" +
            "registrar(estado,proveedor_id,[insumo_id;cantidad;precio_unitario],...)\r\n" +
            "  Registra una compra.\r\n" +
            "  estado: usar PAGADO o PENDIENTE.\r\n" +
            "  [insumo_id;cantidad;precio_unitario]: insumo, cantidad y precio.\r\n" +
            "  Fecha: automatica.\r\n" +
            "\r\n" +
            "actualizarEstado(id,estado)\r\n" +
            "  Actualiza estado de compra.\r\n" +
            "  estado: usar PAGADO o PENDIENTE.\r\n" +
            "\r\n" +
            "eliminar(id)\r\n" +
            "  Elimina una compra.\r\n" +
            "\r\n" +
            "listar()\r\n" +
            "  Muestra compras.\r\n" +
            "\r\n" +
            "buscar(id)\r\n" +
            "  Muestra una compra.\r\n" +
            "\r\n" +
            "listarPorProveedor(proveedor_id)\r\n" +
            "  Muestra compras de un proveedor.\r\n" +
            "\r\n" +
            "=== VENTA ===\r\n" +
            "Rol: cliente / vendedor / propietario\r\n" +
            "\r\n" +
            "registrar(nro_cuotas,tipo,[producto_id;cantidad],...)\r\n" +
            "  Registra una venta.\r\n" +
            "  tipo: CONTADO, CREDITO\r\n" +
            "  CONTADO: nro_cuotas = 1, genera QR automatico.\r\n" +
            "  CREDITO: nro_cuotas = 2 o mas, genera cuotas.\r\n" +
            "  Auto: cliente_id, estado=PENDIENTE, interes_mora, fecha, vendedor.\r\n" +
            "\r\n" +
            "actualizarEstado(id,estado)\r\n" +
            "  Actualiza estado de venta.\r\n" +
            "  estado: PENDIENTE, PAGADO\r\n" +
            "\r\n" +
            "eliminar(id)\r\n" +
            "  Elimina una venta.\r\n" +
            "\r\n" +
            "listar()\r\n" +
            "  Muestra ventas.\r\n" +
            "\r\n" +
            "buscar(id)\r\n" +
            "  Muestra una venta.\r\n" +
            "\r\n" +
            "listarPorCliente(cliente_id)\r\n" +
            "  Muestra ventas de un cliente.\r\n" +
            "\r\n" +
            "verificarPago(id_venta)\r\n" +
            "  Consulta PagoFacil y actualiza estado si fue pagado.\r\n" +
            "  Requiere: venta con pagofacilTransactionId guardado.\r\n" +
            "\r\n" +
            "=== CUOTA ===\r\n" +
            "Rol: cliente / vendedor / propietario\r\n" +
            "\r\n" +
            "pagar(id,fecha_pago,monto_pagado)\r\n" +
            "  Registra pago manual.\r\n" +
            "  estado generado: PAGADO, PAGADO_CON_MORA\r\n" +
            "\r\n" +
            "pagar(id_cuota)\r\n" +
            "  Genera QR de pago.\r\n" +
            "  estados permitidos para pagar: PENDIENTE, EN_MORA\r\n" +
            "\r\n" +
            "eliminar(id)\r\n" +
            "  Elimina una cuota.\r\n" +
            "\r\n" +
            "listarPorVenta(venta_id)\r\n" +
            "  Muestra cuotas de una venta.\r\n" +
            "\r\n" +
            "buscar(id)\r\n" +
            "  Muestra una cuota.\r\n" +
            "\r\n" +
            "listarPorCliente()\r\n" +
            "  Muestra tus cuotas.\r\n" +
            "\r\n" +
            "listarPorCliente(cliente_id)\r\n" +
            "  Muestra cuotas de un cliente.\r\n" +
            "\r\n" +
            "verificarPago(id_cuota)\r\n" +
            "  Consulta PagoFacil y actualiza estado si fue pagado.\r\n" +
            "  Requiere: cuota con pagofacilTransactionId guardado.\r\n" +
            "\r\n" +
            "=== INVENTARIO ===\r\n" +
            "Rol: vendedor / propietario\r\n" +
            "\r\n" +
            "registrar(cantidad,insumo_id,costo_unitario,observacion,tipo_movimiento)\r\n" +
            "  Registra movimiento de inventario.\r\n" +
            "  tipo_movimiento: INGRESO, SALIDA\r\n" +
            "  INGRESO: aumenta stock.\r\n" +
            "  SALIDA: descuenta stock.\r\n" +
            "  Fecha: automatica.\r\n" +
            "\r\n" +
            "actualizar(id,cantidad,observacion)\r\n" +
            "  Actualiza un movimiento.\r\n" +
            "\r\n" +
            "eliminar(id)\r\n" +
            "  Elimina un movimiento.\r\n" +
            "\r\n" +
            "listar()\r\n" +
            "  Muestra movimientos.\r\n" +
            "\r\n" +
            "buscar(id)\r\n" +
            "  Muestra un movimiento.\r\n" +
            "\r\n" +
            "listarPorInsumo(insumo_id)\r\n" +
            "  Muestra movimientos de un insumo.\r\n" +
            "\r\n" +
            "=== RECETA ===\r\n" +
            "Rol: vendedor / propietario\r\n" +
            "\r\n" +
            "registrar(descripcion,producto_id,tiempo_preparacion,[insumo_id;cantidad],...)\r\n" +
            "  Registra una receta.\r\n" +
            "  [insumo_id;cantidad]: insumo usado y cantidad.\r\n" +
            "\r\n" +
            "actualizar(id,descripcion,producto_id,tiempo_preparacion)\r\n" +
            "  Actualiza una receta.\r\n" +
            "\r\n" +
            "eliminar(id)\r\n" +
            "  Elimina una receta.\r\n" +
            "\r\n" +
            "listar()\r\n" +
            "  Muestra recetas.\r\n" +
            "\r\n" +
            "buscar(id)\r\n" +
            "  Muestra una receta.\r\n" +
            "\r\n" +
            "listarPorProducto(producto_id)\r\n" +
            "  Muestra recetas de un producto.\r\n" +
            "\r\n" +
            "=== PRODUCCION ===\r\n" +
            "Rol: vendedor / propietario\r\n" +
            "\r\n" +
            "registrar(cantidad_producida,receta_id)\r\n" +
            "  Registra produccion.\r\n" +
            "  Descuenta stock de insumos.\r\n" +
            "  Fecha: automatica.\r\n" +
            "\r\n" +
            "actualizar(id,cantidad_producida,fecha,receta_id)\r\n" +
            "  Actualiza una produccion.\r\n" +
            "  fecha: YYYY-MM-DD\r\n" +
            "\r\n" +
            "eliminar(id)\r\n" +
            "  Elimina una produccion.\r\n" +
            "\r\n" +
            "listar()\r\n" +
            "  Muestra producciones.\r\n" +
            "\r\n" +
            "buscar(id)\r\n" +
            "  Muestra una produccion.\r\n" +
            "\r\n" +
            "listarPorReceta(receta_id)\r\n" +
            "  Muestra producciones de una receta.\r\n" +
            "\r\n" +
            "=== REPORTE ===\r\n" +
            "Rol: propietario\r\n" +
            "\r\n" +
            "reporte ventas()\r\n" +
            "  Muestra reporte de ventas.\r\n" +
            "\r\n" +
            "reporte cuotasPendientes()\r\n" +
            "  Muestra cuotas pendientes.\r\n" +
            "\r\n" +
            "reporte stockBajo()\r\n" +
            "  Muestra stock bajo.\r\n" +
            "\r\n" +
            "reporte produccion()\r\n" +
            "  Muestra reporte de produccion.\r\n" +
            "\r\n" +
            "reporte compras()\r\n" +
            "  Muestra reporte de compras.\r\n" +
            "\r\n" +
            "reporte ingresos()\r\n" +
            "  Muestra reporte de ingresos.\r\n" +
            "*************************************************************";
    }
}
