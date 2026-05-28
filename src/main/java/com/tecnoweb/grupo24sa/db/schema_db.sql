CREATE DATABASE IF NOT EXISTS SistemaVentas;
USE SistemaVentas;

-- ==========================================
-- 1. CREACIÓN DE TABLAS INDEPENDIENTES
-- ==========================================

CREATE TABLE `User` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `cedula` VARCHAR(50),
    `celular` VARCHAR(20),
    `direccion` VARCHAR(255),
    `email` VARCHAR(150),
    `name` VARCHAR(150),
    `password` VARCHAR(255)
);

CREATE TABLE `Role` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `descripcion` VARCHAR(255),
    `nombre` VARCHAR(100)
);

CREATE TABLE `Modulo` (
    `codigo` INT PRIMARY KEY,
    `descripcion` VARCHAR(255),
    `estado` INT,
    `name` VARCHAR(100),
    `nivel` INT
);

CREATE TABLE `Producto` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `estado` INT,
    `nombre` VARCHAR(150),
    `precio_venta` DECIMAL(10,2),
    `stock_actual` INT,
    `insumo_id` int,
    FOREIGN KEY (`insumo_id`) REFERENCES `Insumo`(`id`) 
);

CREATE TABLE `Insumo` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `costo_unitario` DECIMAL(10,2),
    `descripcion` VARCHAR(255),
    `estado` INT,
    `nombre` VARCHAR(150),
    `stock_actual` INT,
    `stock_minimo` INT,
    `unidad_medida` VARCHAR(50)
);

CREATE TABLE `Proveedor` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `direccion` VARCHAR(255),
    `nombre` VARCHAR(150),
    `telefono` VARCHAR(20)
);

-- ==========================================
-- 2. CREACIÓN DE TABLAS CON DEPENDENCIAS (NIVEL 1)
-- ==========================================

-- Tabla intermedia (Asociación) entre Role y User
CREATE TABLE `Role_users` (
    `role_id` INT,
    `user_id` INT,
    PRIMARY KEY (`role_id`, `user_id`),
    FOREIGN KEY (`role_id`) REFERENCES `Role`(`id`),
    FOREIGN KEY (`user_id`) REFERENCES `User`(`id`)
);

-- Tabla intermedia (Asociación) entre Role y Modulo
CREATE TABLE `role_modulo` (
    `role_id` INT,
    `modulo_codigo` INT,
    PRIMARY KEY (`role_id`, `modulo_codigo`),
    FOREIGN KEY (`role_id`) REFERENCES `Role`(`id`),
    FOREIGN KEY (`modulo_codigo`) REFERENCES `Modulo`(`codigo`)
);

-- Accion depende de Modulo (Relación 1 a 1..*)
CREATE TABLE `Accion` (
    `codigo` INT PRIMARY KEY,
    `descripcion` VARCHAR(255),
    `estado` INT,
    `name` VARCHAR(100),
    `modulo_codigo` INT,
    FOREIGN KEY (`modulo_codigo`) REFERENCES `Modulo`(`codigo`)
);

CREATE TABLE `Venta` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `cliente_id` INT,
    `estado` INT,
    `fecha` DATETIME,
    `interes_mora` INT,
    `nro_cuotas` INT,
    `tipo` VARCHAR(50),
    `total` DECIMAL(10,2),
    `vendedor_id` INT,
    FOREIGN KEY (`vendedor_id`) REFERENCES `User`(`id`)
);

CREATE TABLE `Receta` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `descripcion` VARCHAR(255),
    `producto_id` INT,
    `tiempo_preparacion` VARCHAR(100),
    FOREIGN KEY (`producto_id`) REFERENCES `Producto`(`id`)
);

CREATE TABLE `Compra` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `estado` INT,
    `fecha` DATETIME,
    `proveedor` INT, -- Funciona como proveedor_id según el diagrama
    `total` DECIMAL(10,2),
    FOREIGN KEY (`proveedor`) REFERENCES `Proveedor`(`id`)
);

CREATE TABLE `Inventario` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `cantidad` INT,
    `fecha` DATETIME,
    `insumo_id` INT,
    `observacion` VARCHAR(255),
    `tipo_movimiento` VARCHAR(50),
    `costo_unitario` numeric(10, 2),
    `valor_total` numeric(10, 2),
    FOREIGN KEY (`insumo_id`) REFERENCES `Insumo`(`id`)
);

-- ==========================================
-- 3. CREACIÓN DE TABLAS CON DEPENDENCIAS (NIVEL 2)
-- ==========================================

CREATE TABLE `Cuota` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `estado` INT,
    `fecha_pago` DATE,
    `fecha_vencimiento` DATE,
    `interes_mora` INT,
    `monto_pagado` DECIMAL(10,2),
    `nro_cuota` INT,
    `plan_pago` VARCHAR(100),
    `venta_id` INT,
    `monto_fijo` DECIMAL(10,2),
    FOREIGN KEY (`venta_id`) REFERENCES `Venta`(`id`)
);

-- Tabla intermedia (Asociación) entre Venta y Producto
CREATE TABLE `Detalle_venta` (
    `venta_id` INT,
    `producto_id` INT,
    `cantidad` INT,
    `precio_unitario` DECIMAL(10,2),
    `sub_total` DECIMAL(10,2),
    PRIMARY KEY (`venta_id`, `producto_id`),
    FOREIGN KEY (`venta_id`) REFERENCES `Venta`(`id`),
    FOREIGN KEY (`producto_id`) REFERENCES `Producto`(`id`)
);

CREATE TABLE `Produccion` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `cantidad_producida` INT,
    `fecha` DATETIME,
    `receta_id` INT,
    FOREIGN KEY (`receta_id`) REFERENCES `Receta`(`id`)
);

-- Tabla intermedia (Asociación) entre Receta e Insumo
CREATE TABLE `Receta_Insumo` (
    `receta_id` INT,
    `insumo_id` INT,
    `cantidad` INT,
    PRIMARY KEY (`receta_id`, `insumo_id`),
    FOREIGN KEY (`receta_id`) REFERENCES `Receta`(`id`),
    FOREIGN KEY (`insumo_id`) REFERENCES `Insumo`(`id`)
);

-- Tabla intermedia (Asociación) entre Compra e Insumo
CREATE TABLE `Detalle_Compra` (
    `compra_id` INT,
    `insumo_id` INT,
    `cantida` INT, -- Escrito exactamente como en el diagrama (aunque parece un typo de "cantidad")
    `precio_unitario` DECIMAL(10,2),
    `subtotal` DECIMAL(10,2),
    PRIMARY KEY (`compra_id`, `insumo_id`),
    FOREIGN KEY (`compra_id`) REFERENCES `Compra`(`id`),
    FOREIGN KEY (`insumo_id`) REFERENCES `Insumo`(`id`)
);