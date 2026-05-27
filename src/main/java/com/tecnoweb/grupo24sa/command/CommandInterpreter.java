package com.tecnoweb.grupo24sa.command;

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
        COMMANDS.put("cuota", new String[] { "registrar", "pagar", "eliminar", "listarPorVenta", "buscar" });

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
        COMMANDS.put("rol", new String[] { "registrar", "actualizar", "eliminar", "listar", "buscar" });

        // CU15 - Gestión de Role Users
        COMMANDS.put("roleusers", new String[] { "registrar", "eliminar", "listarPorUsuario", "listarPorRol" });

        // CU16 - Gestión de Módulos
        COMMANDS.put("modulo", new String[] { "registrar", "actualizar", "eliminar", "listar", "buscar" });

        // CU17 - Gestión de Acciones
        COMMANDS.put("accion", new String[] { "registrar", "actualizar", "eliminar", "listar", "buscar" });

        // CU18 - Gestión de Role Módulo
        COMMANDS.put("rolemodulo", new String[] { "registrar", "eliminar", "listarPorRol", "listarPorModulo" });
    }

    public static String interpret(String subject) {
        ReporteResponse response = interpretConGraficos(subject);
        return response.getTextoRespuesta();
    }

    /**
     * Interpreta comandos y devuelve respuesta con gráficos adjuntos si aplica
     */
    public static ReporteResponse interpretConGraficos(String subject) {
        subject = subject.replaceAll("[^\\p{L}\\p{N}\\s\\(\\),./@;_\\[\\]-]", "");
        subject = subject.replaceAll("\\s+", " ").trim();

        System.out.println("Subject luego de formatear: " + subject);

        if (subject.equalsIgnoreCase("help")) {
            return new ReporteResponse(getHelpMessage());
        }

        String pattern = "([a-zA-Z]+)\\s+([a-zA-Z]+)\\s*\\((.*)\\)";
        java.util.regex.Pattern regex = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher matcher = regex.matcher(subject);

        if (!matcher.matches()) {
            return new ReporteResponse(
                    "Comando no reconocido. Por favor, asegúrate de seguir la estructura: {entidad} {comando} (parametros)");
        }

        String entity = matcher.group(1).trim().toLowerCase();
        String commandInput = matcher.group(2).trim();
        String params = matcher.group(3).trim();

        if (!COMMANDS.containsKey(entity)) {
            return new ReporteResponse(
                    "Entidad '" + entity + "' no reconocida. Usa 'help' para ver entidades disponibles.");
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
                    + "'. Usa 'help' para ver comandos disponibles.");
        }

        // Ejecutar comandos según entidad
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
                return new ReporteResponse(HandleVenta.execute(command, params));

            case "cuota":
                return new ReporteResponse(HandleCuota.execute(command, params));
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
                        "Entidad '" + entity + "' reconocida pero lógica de negocio no enlazada en intérprete aún.");
        }
    }

    private static String getHelpMessage() {
        return "**************** SISTEMA DE INVENTARIO Y VENTAS - LAS BRAZAS ****************\r\n" +
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
                "registrar(estado,nombre,precio_venta)\r\n" +
                "actualizar(id,estado,nombre,precio_venta)\r\n" +
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
                "registrar(estado,fecha,proveedor_id,[insumo_id1;cantidad1;precio_unitario1],...)\r\n" +
                "  Ejemplo: compra registrar(PAGADO,2026-05-26,1,[1;10;15.00],[2;5;43.00])\r\n" +
                "actualizarEstado(id,estado) | eliminar(id)\r\n" +
                "listar() | buscar(id) | listarPorProveedor(proveedor_id)\r\n" +
                "\r\n" +
                "=== VENTA ===\r\n" +
                "registrar(cliente_id,estado,fecha,interes_mora,nro_cuotas,tipo,vendedor_id,[producto_id1;cantidad1],...)\r\n" +
                "  tipo: CONTADO (1 cuota) o CREDITO (>= 2 cuotas)\r\n" +
                "  Ejemplo: venta registrar(3,pendiente,2026-05-26,0.0,1,CONTADO,2,[1;2],[2;1])\r\n" +
                "actualizarEstado(id,estado) | eliminar(id)\r\n" +
                "listar() | buscar(id) | listarPorCliente(cliente_id)\r\n" +
                "\r\n" +
                "=== CUOTA ===\r\n" +
                "registrar(estado,fecha_pago,fecha_vencimiento,interes_mora,monto,nro_cuota,plan_pago,venta_id)\r\n" +
                "  Usar 'null' en fecha_pago si aún no se ha pagado.\r\n" +
                "pagar(id,fecha_pago,monto_pagado)   <- detecta mora automaticamente\r\n" +
                "eliminar(id) | listarPorVenta(venta_id) | buscar(id)\r\n" +
                "\r\n" +
                "=== INVENTARIO ===\r\n" +
                "registrar(cantidad,fecha,insumo_id,metodo_inventario,observacion,tipo_movimiento)\r\n" +
                "  tipo_movimiento: INGRESO o SALIDA\r\n" +
                "  metodo_inventario: FIFO, LIFO o PROMEDIO\r\n" +
                "actualizar(id,cantidad,observacion)\r\n" +
                "eliminar(id) | listar() | buscar(id) | listarPorInsumo(insumo_id)\r\n" +
                "\r\n" +
                "=== RECETA ===\r\n" +
                "registrar(descripcion,producto_id,tiempo_preparacion,[insumo_id1;cantidad1],...)\r\n" +
                "  Ejemplo: receta registrar(Hamburguesa Clásica,1,15,[1;1.0],[2;1.0])\r\n" +
                "actualizar(id,descripcion,producto_id,tiempo_preparacion)\r\n" +
                "eliminar(id) | listar() | buscar(id) | listarPorProducto(producto_id)\r\n" +
                "\r\n" +
                "=== PRODUCCION ===\r\n" +
                "registrar(cantidad_producida,fecha,receta_id)  <- descuenta stock de insumos\r\n" +
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
