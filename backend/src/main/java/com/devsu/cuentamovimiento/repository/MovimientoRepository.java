package com.devsu.cuentamovimiento.repository;

import com.devsu.cuentamovimiento.entity.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {
    
    List<Movimiento> findByCuentaId(Long cuentaId);
    
    List<Movimiento> findByCuentaIdOrderByFechaDesc(Long cuentaId);
    
    @Query("SELECT m FROM Movimiento m WHERE m.cuenta.id = :cuentaId " +
           "AND m.fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY m.fecha DESC")
    List<Movimiento> findByCuentaIdAndFechaBetween(
        @Param("cuentaId") Long cuentaId,
        @Param("fechaInicio") LocalDateTime fechaInicio,
        @Param("fechaFin") LocalDateTime fechaFin
    );
    
    @Query("SELECT COALESCE(SUM(ABS(m.valor)), 0) FROM Movimiento m " +
           "WHERE m.cuenta.id = :cuentaId " +
           "AND m.valor < 0 " +
           "AND DATE(m.fecha) = DATE(:fecha)")
    BigDecimal calcularRetirosDiarios(
        @Param("cuentaId") Long cuentaId,
        @Param("fecha") LocalDateTime fecha
    );
    
    @Query("SELECT m FROM Movimiento m " +
           "JOIN m.cuenta c " +
           "JOIN c.cliente cl " +
           "WHERE cl.clienteId = :clienteId " +
           "AND m.fecha BETWEEN :fechaInicio AND :fechaFin " +
           "ORDER BY m.fecha DESC")
    List<Movimiento> findByClienteIdAndFechaBetween(
        @Param("clienteId") Long clienteId,
        @Param("fechaInicio") LocalDateTime fechaInicio,
        @Param("fechaFin") LocalDateTime fechaFin
    );
}


