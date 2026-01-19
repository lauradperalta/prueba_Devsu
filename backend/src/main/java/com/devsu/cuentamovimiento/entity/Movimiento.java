package com.devsu.cuentamovimiento.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimientos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movimiento {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movimiento_id")
    private Long id;
    
    @NotNull(message = "La fecha es obligatoria")
    @Column(nullable = false)
    private LocalDateTime fecha;
    
    @NotBlank(message = "El tipo de movimiento es obligatorio")
    @Column(name = "tipo_movimiento", nullable = false, length = 20)
    private String tipoMovimiento;
    
    @NotNull(message = "El valor es obligatorio")
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;
    
    @NotNull(message = "El saldo es obligatorio")
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal saldo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_id", nullable = false)
    @ToString.Exclude
    private Cuenta cuenta;
}


