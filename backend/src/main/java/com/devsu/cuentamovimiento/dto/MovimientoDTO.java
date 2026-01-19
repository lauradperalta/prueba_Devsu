package com.devsu.cuentamovimiento.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimientoDTO {
    
    private Long id;
    
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime fecha;
    
    @NotBlank(message = "El tipo de movimiento es obligatorio")
    private String tipoMovimiento;
    
    @NotNull(message = "El valor es obligatorio")
    private BigDecimal valor;
    
    private BigDecimal saldo;
    
    @NotBlank(message = "El número de cuenta es obligatorio")
    private String numeroCuenta;
}


