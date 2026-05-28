package com.tecnoweb.grupo24sa.business;

import com.tecnoweb.grupo24sa.data.DProducto;

import java.util.List;

/**
 * CU2 - Gestión de Productos
 */
public class BProducto {

    private final DProducto dProducto;

    public BProducto() {
        this.dProducto = new DProducto();
    }

    /**
     * Registra un nuevo producto con validaciones
     * @param estado  estado del producto (activo/inactivo)
     * @param nombre  nombre del producto
     * @param precioVenta precio de venta (> 0)
     */
    public String registrarProducto(String estado, String nombre, double precioVenta) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return "Error: El nombre del producto es obligatorio";
        }
        if (nombre.trim().length() < 2) {
            return "Error: El nombre debe tener al menos 2 caracteres";
        }
        if (precioVenta <= 0) {
            return "Error: El precio de venta debe ser mayor a 0";
        }
        if (estado == null || estado.trim().isEmpty()) {
            return "Error: El estado es obligatorio";
        }
        return dProducto.save(estado.trim(), nombre.trim(), precioVenta, 0);
    }

    /**
     * Actualiza un producto existente
     */
    public String actualizarProducto(int id, String estado, String nombre, double precioVenta) {
        String[] prod = dProducto.findOneById(id);
        if (prod == null) {
            return "Error: Producto no encontrado con ID: " + id;
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            return "Error: El nombre del producto es obligatorio";
        }
        if (precioVenta <= 0) {
            return "Error: El precio de venta debe ser mayor a 0";
        }
        if (estado == null || estado.trim().isEmpty()) {
            return "Error: El estado es obligatorio";
        }
        int stockActual = (prod.length > 4 && prod[4] != null) ? Integer.parseInt(prod[4]) : 0;
        return dProducto.update(id, estado.trim(), nombre.trim(), precioVenta, stockActual);
    }

    /**
     * Elimina un producto por ID
     */
    public String eliminarProducto(int id) {
        if (dProducto.findOneById(id) == null) {
            return "Error: Producto no encontrado con ID: " + id;
        }
        return dProducto.delete(id);
    }

    /**
     * Lista todos los productos
     */
    public List<String[]> listarProductos() {
        return dProducto.findAll();
    }

    /**
     * Busca un producto por ID
     */
    public String[] buscarPorId(int id) {
        return dProducto.findOneById(id);
    }
}
