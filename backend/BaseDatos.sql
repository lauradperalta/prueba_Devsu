-- Script de base de datos

-- Crear base de datos
CREATE DATABASE IF NOT EXISTS banco_db;

-- Conectar a la base de datos
\c banco_db;

-- Tabla de Clientes
CREATE TABLE IF NOT EXISTS clientes (
    cliente_id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    genero VARCHAR(10) NOT NULL,
    edad INTEGER NOT NULL,
    identificacion VARCHAR(20) UNIQUE NOT NULL,
    direccion VARCHAR(200) NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    contrasena VARCHAR(100) NOT NULL,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de Cuentas
CREATE TABLE IF NOT EXISTS cuentas (
    cuenta_id SERIAL PRIMARY KEY,
    numero_cuenta VARCHAR(20) UNIQUE NOT NULL,
    tipo_cuenta VARCHAR(20) NOT NULL,
    saldo_inicial NUMERIC(15,2) NOT NULL,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    cliente_id INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(cliente_id) ON DELETE CASCADE
);

-- Tabla de Movimientos
CREATE TABLE IF NOT EXISTS movimientos (
    movimiento_id SERIAL PRIMARY KEY,
    fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tipo_movimiento VARCHAR(20) NOT NULL,
    valor NUMERIC(15,2) NOT NULL,
    saldo NUMERIC(15,2) NOT NULL,
    cuenta_id INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cuenta FOREIGN KEY (cuenta_id) REFERENCES cuentas(cuenta_id) ON DELETE CASCADE
);

-- Índices para mejorar el rendimiento
CREATE INDEX idx_cliente_identificacion ON clientes(identificacion);
CREATE INDEX idx_cuenta_numero ON cuentas(numero_cuenta);
CREATE INDEX idx_cuenta_cliente ON cuentas(cliente_id);
CREATE INDEX idx_movimiento_cuenta ON movimientos(cuenta_id);
CREATE INDEX idx_movimiento_fecha ON movimientos(fecha);

-- Datos de ejemplo (casos de uso)

-- 1. Creación de Usuarios
INSERT INTO clientes (nombre, genero, edad, identificacion, direccion, telefono, contrasena, estado) VALUES
('Jose Lema', 'Masculino', 35, '1234567890', 'Otavalo sn y principal', '098254785', '1234', TRUE),
('Marianela Montalvo', 'Femenino', 28, '0987654321', 'Amazonas y NNUU', '097548965', '5678', TRUE),
('Juan Osorio', 'Masculino', 40, '1122334455', '13 junio y Equinoccial', '098874587', '1245', TRUE);

-- 2. Creación de Cuentas de Usuario
INSERT INTO cuentas (numero_cuenta, tipo_cuenta, saldo_inicial, estado, cliente_id) VALUES
('478758', 'Ahorro', 2000.00, TRUE, (SELECT cliente_id FROM clientes WHERE nombre = 'Jose Lema')),
('225487', 'Corriente', 100.00, TRUE, (SELECT cliente_id FROM clientes WHERE nombre = 'Marianela Montalvo')),
('495878', 'Ahorros', 0.00, TRUE, (SELECT cliente_id FROM clientes WHERE nombre = 'Juan Osorio')),
('496825', 'Ahorros', 540.00, TRUE, (SELECT cliente_id FROM clientes WHERE nombre = 'Marianela Montalvo')),
('585545', 'Corriente', 1000.00, TRUE, (SELECT cliente_id FROM clientes WHERE nombre = 'Jose Lema'));


-- Otras consultas
-- Ver todos los clientes
SELECT * FROM clientes;

-- Ver todas las cuentas con nombre de cliente
SELECT c.numero_cuenta, c.tipo_cuenta, c.saldo_inicial, c.estado, cl.nombre as cliente
FROM cuentas c
INNER JOIN clientes cl ON c.cliente_id = cl.cliente_id;

-- Ver movimientos de una cuenta
SELECT m.fecha, m.tipo_movimiento, m.valor, m.saldo, c.numero_cuenta
FROM movimientos m
INNER JOIN cuentas c ON m.cuenta_id = c.cuenta_id
WHERE c.numero_cuenta = '478758'
ORDER BY m.fecha DESC;

-- Calcular saldo actual de una cuenta
SELECT c.numero_cuenta, c.saldo_inicial, COALESCE(m.ultimo_saldo, c.saldo_inicial) as saldo_actual
FROM cuentas c
LEFT JOIN LATERAL (
     SELECT saldo as ultimo_saldo
     FROM movimientos
     WHERE cuenta_id = c.cuenta_id
     ORDER BY fecha DESC
     LIMIT 1
 ) m ON TRUE
WHERE c.numero_cuenta = '478758';


